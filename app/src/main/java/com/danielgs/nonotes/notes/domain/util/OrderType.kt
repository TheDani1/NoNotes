package com.danielgs.nonotes.notes.domain.util

sealed class OrderType {
    object Ascending: OrderType()
    object Descending: OrderType()
}
