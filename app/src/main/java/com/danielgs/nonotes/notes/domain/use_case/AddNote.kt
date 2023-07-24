package com.danielgs.nonotes.notes.domain.use_case

import com.danielgs.nonotes.notes.domain.model.InvalidNoteException
import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.repository.NoteRepository

/**
 * Uso de caso para añadir una nota
 *
 * @property repository Repositorio para el manejo de datos
 */
class AddNote(
    private val repository: NoteRepository
) {

    /**
     * Uso de caso para añadir una nota. Se comprueba si no está en blanco ni el titulo ni el contenido.
     * Si lo está, se lanza una [InvalidNoteException] y si no, se añade a la base de datos.
     *
     * @property note Nota para insertar
     */
    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note : Note){

        if(note.title.isBlank()){
            throw InvalidNoteException("The title of the note can't be empty")
        }

        if(note.content.isBlank()){
            throw InvalidNoteException("The content of the note can't be empty")
        }

        repository.insertNote(note)
    }
}