package com.danielgs.nonotes.notes.presentation.notes

import android.content.Context
import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.model.NoteDatabaseData
import com.danielgs.nonotes.notes.domain.util.NoteOrder

/**
 * Clase de datos que tiene todos los eventos de las Notas
 *
 */
sealed class NotesEvent{

    /**
     * Clase de datos del orden de las notas
     *
     * @param noteOrder Orden de las notas
     */
    data class Order(val noteOrder: NoteOrder): NotesEvent()

    /**
     * Clase de datos para eliminar una nota
     *
     * @param note Nota para borrar
     * @param context Contexto
     *
     */
    data class DeleteNote(val note: NoteDatabaseData, val context: Context): NotesEvent()

    /**
     * Objecto de evento de nota para recuperarla
     *
     */
    object RestoreNote: NotesEvent()

    /**
     * Clase de dato para cambiar el favorito
     *
     * @property note Nota para cambiar favorito
     */
    data class ChangeFavourite(val note: Note) : NotesEvent()
}
