package com.danielgs.nonotes.notes.presentation.notes

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.model.NoteDatabaseData
import com.danielgs.nonotes.notes.domain.use_case.NoteUseCases
import com.danielgs.nonotes.notes.domain.util.NoteOrder
import com.danielgs.nonotes.notes.presentation.APP_UID
import com.danielgs.nonotes.notes.presentation.dataStore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Función para lanzar el autenticador de Firebase
 *
 * @param onAuthComplete Si se completa la autenticación
 * @param onAuthError Si se produce un error en la autenticación
 *
 *
 */
@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit, onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}

/**
 * Clase viewmodel de la pantalla de las notas.
 *
 * @property noteUseCases Usos de caso
 *
 *
 */
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    suspend fun storeUserName(name: String, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[APP_UID] = name
        }
        Log.d("INCIADO", "Guardado username: " + name)
    }

    val db = FirebaseDatabase.getInstance()
    val ref = db.getReference("notes")

    private val _user = mutableStateOf(Firebase.auth.currentUser)
    val user = _user

    private val _loading = mutableStateOf(true)
    val loading = _loading

    val _loadAgain = mutableStateOf(true)
    val loadAgain = _loadAgain

    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    init {
        //getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onLoginButtonPressed(
        context: Context,
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
        token: String
    ) {

        loading.value = true
        Log.d("DEBUGNAME", "Loading true3")

        val firebaseUser = Firebase.auth.currentUser

        if (firebaseUser == null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token).requestEmail().requestProfile().build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)

        } else {

            val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
                preferences[APP_UID] ?: ""
            }

            val db = FirebaseDatabase.getInstance()

            viewModelScope.launch {
                userNameFlow.collect { userName ->

                    val refNube = db.getReference("notes/${firebaseUser.uid}")
                    val refLocal = db.getReference("notes/${userName}")

                    val dataSnapshot = refNube.get().await()

                    val notesList = mutableListOf<NoteDatabaseData>()

                    dataSnapshot.children.forEach { innerSnapshot ->

                        val title = innerSnapshot.child("title").getValue(String::class.java)
                        val content = innerSnapshot.child("content").getValue(String::class.java)
                        val favourite =
                            innerSnapshot.child("favourite").getValue(Boolean::class.java)
                        val timestamp = innerSnapshot.child("timestamp").getValue(Long::class.java)
                        val color = innerSnapshot.child("color").getValue(Int::class.java)
                        val id = innerSnapshot.key

                        val note = NoteDatabaseData(
                            title ?: "",
                            content ?: "",
                            favourite ?: false,
                            timestamp ?: 0L,
                            color ?: 0,
                            id ?: "",
                        )
                        notesList.add(note)
                    }

                    val childUpdates = hashMapOf<String, Any>()

                    notesList.forEach { note ->
                        note.id?.let { childUpdates.put(it, note) }
                    }

                    refLocal.updateChildren(childUpdates)

                    loadAgain.value = !loadAgain.value
                    loading.value = false
                    Firebase.auth.signOut()
                    user.value = null
                    Log.d("DEBUGNAME", "Loading false3")
                }
            }
        }
    }

    fun onEvent(event: NotesEvent) {
        when (event) {

            is NotesEvent.ChangeFavourite -> {

                viewModelScope.launch {
                    noteUseCases.addNote(event.note)
                }

            }

            is NotesEvent.Order -> {

                if (state.value.noteOrder::class == event.noteOrder::class && state.value.noteOrder.orderType == event.noteOrder.orderType) {
                    return
                }

                //getNotes(event.noteOrder)

            }

            is NotesEvent.DeleteNote -> {

                viewModelScope.launch {

                    val ref = db.getReference("notes")

                    var refSelfNotes = ref

                    val userNameFlow: Flow<String> =
                        event.context.dataStore.data.map { preferences ->
                            preferences[APP_UID] ?: ""
                        }

                    val db = FirebaseDatabase.getInstance()

                    if (Firebase.auth.currentUser == null) {

                        userNameFlow.collect { userName ->

                            refSelfNotes = db.getReference("notes/${userName}/${event.note.id}")

                            refSelfNotes.removeValue()

                        }

                    } else {

                        refSelfNotes = db.getReference("notes/${Firebase.auth.currentUser!!.uid}/${event.note.id}")
                        refSelfNotes.removeValue()

                    }

                    Toast.makeText(
                        event.context,
                        "Nota eliminada",
                        Toast.LENGTH_SHORT
                    ).show()

                   loadAgain.value = !loadAgain.value

                }

            }

            is NotesEvent.RestoreNote -> {

                viewModelScope.launch {

                    noteUseCases.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }

            }
        }

    }

    fun getNotes(noteOrder: NoteOrder, context: Context) {

        try {
            val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
                preferences[APP_UID] ?: ""
            }

            var refSelfNotes = ref

            val deferred = viewModelScope.async {

                if (Firebase.auth.currentUser == null) {

                    userNameFlow.collect { userName ->

                        refSelfNotes = db.getReference("notes/${userName}")
                        refSelfNotes.keepSynced(true)
                        Log.d("INCIADO", "Carga el UID de App" + "notes/${userName}")

                        try {
                            val dataSnapshot =
                                refSelfNotes.get().await()

                            Log.d("DEBUGNAME", "CARGA ESTA REF" + refSelfNotes.toString())

                            val notesList = mutableListOf<NoteDatabaseData>()

                            dataSnapshot.children.forEach { innerSnapshot ->

                                val title =
                                    innerSnapshot.child("title").getValue(String::class.java)
                                val content =
                                    innerSnapshot.child("content").getValue(String::class.java)
                                val favourite =
                                    innerSnapshot.child("favourite").getValue(Boolean::class.java)
                                val timestamp =
                                    innerSnapshot.child("timestamp").getValue(Long::class.java)
                                val color = innerSnapshot.child("color").getValue(Int::class.java)
                                val id = innerSnapshot.key

                                val note = NoteDatabaseData(
                                    title ?: "",
                                    content ?: "",
                                    favourite ?: false,
                                    timestamp ?: 0L,
                                    color ?: 0,
                                    id ?: "",
                                )
                                notesList.add(note)
                                loading.value = false
                            }
                            _state.value = state.value.copy(
                                notes = notesList, noteOrder = noteOrder
                            )

                        } catch (e: Exception) {
                            // Manejo de errores
                        } finally {
                            loading.value = false
                        }

                    }

                } else {
                    refSelfNotes = db.getReference("notes/${Firebase.auth.currentUser!!.uid}")
                    refSelfNotes.keepSynced(true)
                    Log.d("INCIADO", "Carga el UID de Google")
                }

                val dataSnapshot =
                    refSelfNotes.get().await()

                Log.d("DEBUGNAME", "CARGA ESTA REF" + refSelfNotes.toString())

                val notesList = mutableListOf<NoteDatabaseData>()

                dataSnapshot.children.forEach { innerSnapshot ->

                    val title = innerSnapshot.child("title").getValue(String::class.java)
                    val content = innerSnapshot.child("content").getValue(String::class.java)
                    val favourite =
                        innerSnapshot.child("favourite").getValue(Boolean::class.java)
                    val timestamp = innerSnapshot.child("timestamp").getValue(Long::class.java)
                    val color = innerSnapshot.child("color").getValue(Int::class.java)
                    val id = innerSnapshot.key

                    val note = NoteDatabaseData(
                        title ?: "",
                        content ?: "",
                        favourite ?: false,
                        timestamp ?: 0L,
                        color ?: 0,
                        id ?: "",
                    )
                    notesList.add(note)
                }
                _state.value = state.value.copy(
                    notes = notesList, noteOrder = noteOrder
                )
                Log.d("DEBUGNAME", "Acaba getNotes de Google")
                //loading.value= false
            }

            /*viewModelScope.launch(Dispatchers.Main) {
                Log.d("DEBUGNAME", "Se lanza deferred")
                delay(1000)
                if (deferred.isActive) {
                    loading.value = true
                    Log.d("DEBUGNAME", "Loading true4")
                    try {
                        val result = deferred.await()
                    } finally {
                        loading.value = false
                        Log.d("DEBUGNAME", "Loading false5")
                    }
                } else {
                    Log.d("DEBUGNAME", "ESPERA")
                    val result = deferred.await()

                }
            }*/

        } catch (e: Exception) {
            // Manejo de errores
        } finally {
            loading.value = false
        }

    }

}