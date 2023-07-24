package com.danielgs.nonotes.notes.domain.use_case

/**
 * Clase de datos con todos los usos de caso
 *
 * @property getNotes Uso de caso para obtener todas las notas
 * @property deleteNote Uso de caso para eliminar una nota
 * @property addNote Uso de caso para añadir una nota
 * @property getNote Uso de caso para obtener una nota
 *
 */
data class NoteUseCases(
    val getNotes: GetNotes,
    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote: GetNote
)
