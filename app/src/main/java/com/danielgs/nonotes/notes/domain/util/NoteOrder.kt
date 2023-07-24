package com.danielgs.nonotes.notes.domain.util

/**
 * Clase sellada con los tipos de orden con los que se pueden obtener las notas.
 *
 * @property orderType Tipo de orden con el que se obtienen las notas
 *
 */
sealed class NoteOrder(val orderType: OrderType){
    class Title(orderType: OrderType): NoteOrder(orderType)
    class Date(orderType: OrderType): NoteOrder(orderType)
    class Color(orderType: OrderType): NoteOrder(orderType)

    fun copy(orderType: OrderType): NoteOrder{
        return when(this){
            is Title ->
                Title(orderType)
            is Date ->
                Date(orderType)
            is Color ->
                Color(orderType)
        }
    }

}
