package com.danielgs.nonotes.notes.presentation.notes

import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.util.NoteOrder
import com.danielgs.nonotes.notes.domain.util.OrderType

data class NotesState(
    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending)
)
