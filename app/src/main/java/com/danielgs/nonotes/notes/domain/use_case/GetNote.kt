package com.danielgs.nonotes.notes.domain.use_case

import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.repository.NoteRepository

/**
 * Uso de caso para obtener una nota con un id
 *
 * @property repository Repositorio para el manejo de datos
 */
class GetNote(
    private val repository: NoteRepository
) {

    /**
     * Uso de caso para obtener una nota con un id.
     *
     * @property id [Int] de la nota a obtener
     */
    suspend operator fun invoke (id: Int): Note? {
        return repository.getNoteById(id)
    }

}