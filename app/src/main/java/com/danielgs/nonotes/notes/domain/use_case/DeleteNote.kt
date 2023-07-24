package com.danielgs.nonotes.notes.domain.use_case

import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.repository.NoteRepository

/**
 * Uso de caso para eliminar una nota
 *
 * @property repository Repositorio para el manejo de datos
 */
class DeleteNote(
    private val repository: NoteRepository
) {
    /**
     * Uso de caso para eliminar una nota.
     *
     * @property note Nota para eliminar
     */
    suspend operator fun invoke(note: Note){
        repository.deleteNote(note)
    }

}