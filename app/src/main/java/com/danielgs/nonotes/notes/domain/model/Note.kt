package com.danielgs.nonotes.notes.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.compose.md_theme_dark_primary
import com.example.compose.md_theme_dark_secondary
import com.example.compose.md_theme_dark_tertiary
import com.example.compose.md_theme_light_onTertiary

/**
 * Clase de datos que especifica el tipo de dato [Note]
 *
 * @property title El titulo de la nota
 * @property content El contenido de la nota
 * @property favourite Si la nota es favorita o no
 * @property timestamp Marca de tiempo de la edición/adición de la nota
 * @property color Color de la nota
 * @property id [Int] que indentifica de forma única e inequívoca a la nota
 *
 */
@Entity
data class Note(
    @ColumnInfo(name = "title")val title: String,
    @ColumnInfo(name = "content")val content: String,
    @ColumnInfo(name = "favourite")val favourite: Boolean,
    @ColumnInfo(name = "timestamp")val timestamp: Long,
    @ColumnInfo(name = "color")val color: Int,
    @PrimaryKey val id : Int? = null
){
    /**
     * Companion object que contiene la lista de colores disponibles para las notas
     */
    companion object{
        val noteColors = listOf(md_theme_dark_tertiary, md_theme_dark_secondary, md_theme_light_onTertiary, md_theme_dark_primary)
    }

}

class InvalidNoteException(message: String) : Exception(message)