package com.danielgs.nonotes.notes.presentation.notes

import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.util.NoteOrder

sealed class NotesEvent{
    data class Order(val noteOrder: NoteOrder): NotesEvent()
    data class DeleteNote(val note: Note): NotesEvent()
    object RestoreNote: NotesEvent()
    data class ChangeFavourite(val note: Note) : NotesEvent()
}
