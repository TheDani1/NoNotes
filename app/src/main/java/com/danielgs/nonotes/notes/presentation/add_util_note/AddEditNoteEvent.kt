package com.danielgs.nonotes.notes.presentation.add_util_note

import android.content.Context
import androidx.compose.ui.focus.FocusState

/**
 * Clase sellada para los eventos de Añadir o editar una nota
 *
 */
sealed class AddEditNoteEvent {

    /**
     * Clase de datos que detecta la introducción del título
     * @param value Valor del titulo nuevo
     *
     */
    data class EnteredTitle(val value: String): AddEditNoteEvent()

    /**
     * Clase de datos que detecta el cambio de focus
     * @param focusState Estado del focus
     *
     */
    data class ChangeTitleFocus(val focusState: FocusState): AddEditNoteEvent()

    /**
     * Clase de datos que detecta la introducción del contenido
     * @param value Valor del contenido nuevo
     *
     */
    data class EnteredContent(val value: String): AddEditNoteEvent()

    /**
     * Clase de datos que detecta el cambio de focus
     * @param focusState Estado del focus
     *
     */
    data class ChangeContentFocus(val focusState: FocusState): AddEditNoteEvent()

    /**
     * Clase de datos que detecta el cambio de color
     * @param color Color al que cambia
     *
     */
    data class ChangeColor(val color: Int): AddEditNoteEvent()

    /**
     * Clase de datos que detecta el cambio de favorita
     * @param favourite Si es favorita o no
     *
     */
    data class ChangeFavourite(val favourite: Boolean): AddEditNoteEvent()

    /**
     * Clase de datos que detecta el guardado de la nota
     * @param context Contexto en el que se guarda la nota
     *
     */
    data class SaveNote(val context: Context): AddEditNoteEvent()
}
