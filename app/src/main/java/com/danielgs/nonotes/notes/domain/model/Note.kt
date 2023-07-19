package com.danielgs.nonotes.notes.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.compose.md_theme_dark_primary
import com.example.compose.md_theme_dark_secondary
import com.example.compose.md_theme_dark_tertiary
import com.example.compose.md_theme_light_onTertiary
import com.google.firebase.database.Exclude

@Entity
data class Note(
    @ColumnInfo(name = "title")val title: String,
    @ColumnInfo(name = "content")val content: String,
    @ColumnInfo(name = "favourite")val favourite: Boolean,
    @ColumnInfo(name = "timestamp")val timestamp: Long,
    @ColumnInfo(name = "color")val color: Int,
    @PrimaryKey val id : Int? = null
){
    companion object{
        val noteColors = listOf(md_theme_dark_tertiary, md_theme_dark_secondary, md_theme_light_onTertiary, md_theme_dark_primary)
    }

}

class InvalidNoteException(message: String) : Exception(message)