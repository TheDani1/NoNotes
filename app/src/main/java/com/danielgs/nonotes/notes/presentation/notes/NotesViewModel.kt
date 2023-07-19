package com.danielgs.nonotes.notes.presentation.notes

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.model.NoteDatabaseData
import com.danielgs.nonotes.notes.domain.use_case.NoteUseCases
import com.danielgs.nonotes.notes.domain.util.NoteOrder
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

val Context.dataStore by preferencesDataStore("user_preferences")
val USER_NAME_KEY = stringPreferencesKey("USER_NAME")

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

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    suspend fun storeUserName(name: String, context: Context) {
        context.dataStore.edit {
                preferences ->
            preferences[USER_NAME_KEY] = name
        }
        Log.d("INCIADO", "Guardado username: " + name)
    }

    val db = FirebaseDatabase.getInstance()
    val ref = db.getReference("notes")

    val _user = mutableStateOf(Firebase.auth.currentUser)
    val user = _user

    val _loading = mutableStateOf(true)
    val loading = _loading

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
        if (user.value == null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token).requestEmail().requestProfile().build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)

        } else {
            Firebase.auth.signOut()
            user.value = null
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
                    noteUseCases.deleteNote(event.note)
                    recentlyDeletedNote = event.note
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

    fun getNotes(noteOrder: NoteOrder, context: Context) {/*getNotesJob?.cancel()

        getNotesJob = noteUseCases.getNotes(noteOrder)
            .onEach { notes ->
                _state.value = state.value.copy(
                    notes = notes,
                    noteOrder = noteOrder
                )
            }
            .launchIn(viewModelScope)*/

        val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY] ?: ""
        }

        var refSelfNotes = ref

        val deferred = viewModelScope.async {

            userNameFlow.collect { userName ->

                Log.d("DEBUGNAME", "Obtengo del Flow de obtener notas el userUID: " + userName )

                if(userName == ""){

                    val appRandomUUID = UUID.randomUUID().toString()

                    context.dataStore.edit {
                            preferences ->
                        preferences[USER_NAME_KEY] = appRandomUUID
                    }

                    refSelfNotes = db.getReference("notes/${appRandomUUID}")
                    Log.d("INCIADO", "Carga el UID de App")

                }else{
                    refSelfNotes = db.getReference("notes/${userName}")
                    Log.d("INCIADO", "Carga el UID de Google")
                }

                try {
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

                } catch (e: Exception) {
                    // Manejo de errores
                }
            }
        }

        viewModelScope.launch(Dispatchers.Main) {
            delay(100)
            if (deferred.isActive) {
                loading.value = true
                try {
                    val result = deferred.await()
                } finally {
                    loading.value = false
                }
            } else {
                val result = deferred.await()

            }
        }

    }

}