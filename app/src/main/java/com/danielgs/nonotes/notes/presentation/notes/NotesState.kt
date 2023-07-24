package com.danielgs.nonotes.notes.presentation.notes

import com.danielgs.nonotes.notes.domain.model.NoteDatabaseData
import com.danielgs.nonotes.notes.domain.util.NoteOrder
import com.danielgs.nonotes.notes.domain.util.OrderType

/**
 * Clase de dato que contiene el estado de la nota
 *
 * @property notes Lista de [NoteDatabaseData]
 * @property noteOrder Orden de las notas
 *
 */
data class NotesState(
    val notes: List<NoteDatabaseData> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending)
)
