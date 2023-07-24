package com.danielgs.nonotes.notes.data.repository

import com.danielgs.nonotes.notes.data.NoteDao
import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementación de la base de datos
 *
 * @property dao Data Access Object de la base de datos
 */
class NoteRepositoryImpl(
    private val dao: NoteDao
) : NoteRepository{

    /**
     * Devuelve todas las notas
     *
     * @return Flow con una lista de tipo [Note]
     */
    override fun getNotes(): Flow<List<Note>> {
        return dao.getNotes()
    }

    /**
     * Devuelve la nota con ese ID
     *
     * @param id [Int] que identifica cada nota de forma única e inequívoca
     * @return Flow con una lista de tipo [Note]
     */
    override suspend fun getNoteById(id: Int): Note? {
        return dao.getNoteById(id)
    }

    /**
     * Inserta una nota en la base de datos
     *
     * @param note [Note] a insertar
     */
    override suspend fun insertNote(note: Note) {
        dao.insertNote(note)
    }

    /**
     * Elimina una nota
     *
     * @param note [Note] a eliminar
     */
    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }

}