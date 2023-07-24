package com.danielgs.nonotes.notes.presentation.add_util_note

/**
 * Clase de datos que contiene el estado del [TextField] personalizado.
 *
 */
data class NoteTextFieldState(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true,
    val isFavorite: Boolean = true
)
