package com.danielgs.nonotes.notes.domain.repository

import com.danielgs.nonotes.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para facilitar y fortalecer el manejo de la base de datos.
 *
 */
interface NoteRepository {

    /**
     * Devuelve todas las notas
     *
     * @return Flow con una lista de tipo [Note]
     */
    fun getNotes(): Flow<List<Note>>

    /**
     * Devuelve la nota con ese ID
     *
     * @param id [Int] que identifica cada nota de forma única e inequívoca
     * @return Flow con una lista de tipo [Note]
     */
    suspend fun getNoteById(id: Int): Note?

    /**
     * Inserta una nota en la base de datos
     *
     * @param note [Note] a insertar
     */
    suspend fun insertNote(note: Note)

    /**
     * Elimina una nota
     *
     * @param note [Note] a eliminar
     */
    suspend fun deleteNote(note : Note)

}