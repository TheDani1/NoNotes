package com.danielgs.nonotes.notes.domain.model

import com.google.firebase.database.Exclude

data class NoteDatabaseData (
    val title: String? = null,
    val content: String? = null,
    val favourite: Boolean? = null,
    val timestamp: Long? = null,
    val color: Int? = null,
    val id: String? = null
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "content" to content,
            "favourite" to favourite,
            "timestamp" to timestamp,
            "color" to color,
        )
    }
}