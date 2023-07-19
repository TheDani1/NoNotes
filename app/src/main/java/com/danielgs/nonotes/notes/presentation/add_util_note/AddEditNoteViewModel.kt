package com.danielgs.nonotes.notes.presentation.add_util_note

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielgs.nonotes.notes.domain.model.InvalidNoteException
import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.model.NoteDatabaseData
import com.danielgs.nonotes.notes.domain.use_case.NoteUseCases
import com.danielgs.nonotes.notes.presentation.notes.USER_NAME_KEY
import com.danielgs.nonotes.notes.presentation.notes.dataStore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var currentNoteId: String? = null

    fun getUserData(userNameFlow : Flow<String>) : String {

        var user = ""

        viewModelScope.launch {
            userNameFlow.collect { userName ->
                user = userName
            }
        }

        return user
    }

    suspend fun storeUserName(name: String, context: Context) {
        context.dataStore.edit {
                preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    init {
        Log.d("INCIADO", "HOLA")
        savedStateHandle.get<String>("noteId")?.let { noteId ->
            if (noteId != "") {
                viewModelScope.launch {

                    val user = Firebase.auth.currentUser
                    val db = FirebaseDatabase.getInstance()
                    var ref = db.getReference("notes/${user?.uid}/${noteId}")

                    savedStateHandle.get<String>("userId")?.let { userId ->
                        // Aquí puedes utilizar el valor de userId
                        Log.d("MyViewModel", "userId: $userId")

                        ref = db.getReference("notes/${userId}/${noteId}")
                    }

                    val deferred = viewModelScope.launch {

                        try {
                            val dataSnapshot =
                                ref.get().await()

                            val title =
                                dataSnapshot.child("title").getValue(String::class.java)
                            val content =
                                dataSnapshot.child("content").getValue(String::class.java)
                            val favourite =
                                dataSnapshot.child("favourite").getValue(Boolean::class.java)
                            val timestamp =
                                dataSnapshot.child("timestamp").getValue(Long::class.java)
                            val color = dataSnapshot.child("color").getValue(Int::class.java)
                            val id = dataSnapshot.key

                            val note = NoteDatabaseData(
                                title ?: "",
                                content ?: "",
                                favourite ?: false,
                                timestamp ?: 0L,
                                color ?: 0,
                                id ?: "",
                            )

                            currentNoteId = noteId
                            _noteTitle.value = note.title?.let {
                                noteTitle.value.copy(
                                    text = it,
                                    isHintVisible = false
                                )
                            }!!
                            _noteContent.value = note.content?.let {
                                noteContent.value.copy(
                                    text = it,
                                    isHintVisible = false
                                )
                            }!!
                            _noteColor.value = note.color!!


                        } catch (e: Exception) {
                            // Manejo de errores
                        }
                    }
                }
            }
        }
    }

    private val _noteTitle = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter title..."
        )
    )
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter some content..."
        )
    )
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _noteColor = mutableStateOf(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    //private val _favourite = mutableStateOf(false)
    //val favourite : State<Boolean> = _favourite

    private val _favourite = mutableStateOf(
        NoteTextFieldState(
            isFavorite = false
        )
    )
    val favourite: State<NoteTextFieldState> = _favourite

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {

            is AddEditNoteEvent.ChangeFavourite -> {
                _favourite.value = favourite.value.copy(
                    isFavorite = event.favourite
                )
            }

            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(
                    text = event.value
                )
            }

            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            noteTitle.value.text.isBlank()
                )
            }

            is AddEditNoteEvent.EnteredContent -> {
                _noteContent.value = noteContent.value.copy(
                    text = event.value
                )
            }

            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContent.value = noteContent.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            _noteContent.value.text.isBlank()
                )
            }

            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
            }

            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {

                        val noteda = NoteDatabaseData(
                            title = noteTitle.value.text,
                            content = noteContent.value.text,
                            timestamp = System.currentTimeMillis(),
                            color = noteColor.value,
                            favourite = favourite.value.isFavorite
                        )

                        val db = FirebaseDatabase.getInstance()

                        val userNameFlow: Flow<String> = event.context.dataStore.data.map { preferences ->
                            preferences[USER_NAME_KEY] ?: ""
                        }

                        userNameFlow.collect { userName ->

                            val refSelfNotes = db.getReference("notes/${userName}")

                            if(currentNoteId == null){

                                val newData = refSelfNotes.push()
                                newData.setValue(noteda)

                            }else{

                                val notesValues = noteda.toMap()

                                val childUpdates = hashMapOf<String, Any>(
                                    "$currentNoteId" to notesValues,
                                )

                                refSelfNotes.updateChildren(childUpdates)

                            }
                            _eventFlow.emit(UiEvent.SaveNote)

                        }

                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.showSnackbar(
                                message = e.message ?: "Couldn't save note"
                            )
                        )

                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class showSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }

}