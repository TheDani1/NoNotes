package com.danielgs.nonotes.notes.presentation.add_util_note

import androidx.compose.ui.focus.FocusState

sealed class AddEditNoteEvent {
    data class EnteredTitle(val value: String): AddEditNoteEvent()
    data class ChangeTitleFocus(val focusState: FocusState): AddEditNoteEvent()
    data class EnteredContent(val value: String): AddEditNoteEvent()
    data class ChangeContentFocus(val focusState: FocusState): AddEditNoteEvent()
    data class ChangeColor(val color: Int): AddEditNoteEvent()
    data class ChangeFavourite(val favourite: Boolean): AddEditNoteEvent()
    object SaveNote: AddEditNoteEvent()
}
