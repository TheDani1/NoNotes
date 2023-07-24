package com.danielgs.nonotes.notes.presentation.add_util_note.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization

/**
 * Esta función [Composable] renderiza un [BasicTextField] con los parámetros de entrada.
 *
 * @property text Texto del [BasicTextField]
 * @property hint La pista del [BasicTextField]
 * @property modifier El modificador del [BasicTextField]
 * @property isHintVisible Visibilidad de la pista
 * @property onValueChange Función que se ejecuta cada vez que cambia su valor
 * @property textStyle El estilo del texto
 * @property singleLine Si se quiere que se muestre una sola línea
 * @property onFocusChange Si cambia el focus
 * @property readOnly Solo lectura
 *
 */
@Composable
fun TransparentHintTextField(
    text : String,
    hint : String,
    modifier : Modifier = Modifier,
    isHintVisible: Boolean = true,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    onFocusChange: (FocusState) -> Unit,
    readOnly : Boolean = false
    ){

    Box(modifier = modifier) {

        BasicTextField(
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = text,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = textStyle,
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    onFocusChange(it)
                }
        )
        if(isHintVisible) {
            Text(text = hint, style = textStyle, color = Color.DarkGray)
        }

    }

}