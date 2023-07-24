package com.danielgs.nonotes.notes.presentation.util

/**
 * Clase sellada donde residen las pantallas disponibles de la aplicación
 *
 * @property route Ruta de cada una de las pantallas
 */
sealed class Screen(
    val route: String
){
    /**
     * Objeto de tipo [Screen] que especifica la ruta de la pantalla de las notas
     *
     */
    object NotesScreen: Screen("notes_screen")

    /**
     * Objeto de tipo [Screen] que especifica la ruta de la pantalla de editar o añadir una nota.
     *
     */
    object AddEditNoteScreen: Screen("add_edit_note_screen")
}
