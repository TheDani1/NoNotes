package com.danielgs.nonotes.notes.presentation.notes

import android.content.Context
import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.model.NoteDatabaseData
import com.danielgs.nonotes.notes.domain.util.NoteOrder

sealed class NotesEvent{
    data class Order(val noteOrder: NoteOrder): NotesEvent()
    data class DeleteNote(val note: NoteDatabaseData, val context: Context): NotesEvent()
    object RestoreNote: NotesEvent()
    data class ChangeFavourite(val note: Note) : NotesEvent()
}
