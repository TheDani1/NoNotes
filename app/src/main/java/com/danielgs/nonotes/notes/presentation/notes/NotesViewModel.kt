package com.danielgs.nonotes.notes.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.use_case.NoteUseCases
import com.danielgs.nonotes.notes.domain.util.NoteOrder
import com.danielgs.nonotes.notes.domain.util.OrderType
import com.danielgs.nonotes.notes.presentation.notes.NotesEvent
import com.danielgs.nonotes.notes.presentation.notes.NotesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel(){

    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private var recentlyDeletedNote : Note? = null

    private var getNotesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent){
        when(event){

            is NotesEvent.ChangeFavourite -> {

                viewModelScope.launch {
                    noteUseCases.addNote(event.note)
                }

            }

            is NotesEvent.Order -> {

                if (state.value.noteOrder::class == event.noteOrder::class &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }

                getNotes(event.noteOrder)

            }
            is NotesEvent.DeleteNote ->{

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


    private fun getNotes(noteOrder: NoteOrder){

        getNotesJob?.cancel()

        getNotesJob = noteUseCases.getNotes(noteOrder)
            .onEach { notes ->
                _state.value = state.value.copy(
                    notes = notes,
                    noteOrder = noteOrder
                )
            }
            .launchIn(viewModelScope)
    }

}