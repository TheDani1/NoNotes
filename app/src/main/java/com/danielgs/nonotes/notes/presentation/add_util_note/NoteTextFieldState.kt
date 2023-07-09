package com.danielgs.nonotes.notes.presentation.add_util_note

data class NoteTextFieldState(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true,
    val isFavorite: Boolean = true
)
