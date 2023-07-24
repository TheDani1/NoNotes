package com.danielgs.nonotes.notes.domain.model

import com.google.firebase.database.Exclude

/**
 * Clase de datos que especifica el tipo de dato [Note] pero de forma accesible para la base de datos de FireBase
 *
 * @property title El titulo de la nota
 * @property content El contenido de la nota
 * @property favourite Si la nota es favorita o no
 * @property timestamp Marca de tiempo de la edición/adición de la nota
 * @property color Color de la nota
 * @property id [Int] que indentifica de forma única e inequívoca a la nota
 *
 */
data class NoteDatabaseData (
    val title: String? = null,
    val content: String? = null,
    val favourite: Boolean? = null,
    val timestamp: Long? = null,
    val color: Int? = null,
    val id: String? = null
){
    /**
     * Función que convierte un objeto [NoteDatabaseData] a un [Map]<[String], [Any]?>
     *
     */
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