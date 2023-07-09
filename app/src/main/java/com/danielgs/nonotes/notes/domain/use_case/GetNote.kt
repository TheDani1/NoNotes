package com.danielgs.nonotes.notes.domain.use_case

import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.repository.NoteRepository

class GetNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke (id: Int): Note? {
        return repository.getNoteById(id)
    }

}