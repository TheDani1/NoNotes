package com.danielgs.nonotes.notes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.danielgs.nonotes.notes.domain.model.Note

/**
 * La base de datos en sí de la aplicación.
 *
 */
@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    /**
     * Variable que contiene el DAO [NoteDao] de las Notas
     */
    abstract val noteDao : NoteDao

    /**
     * Companion object que contiene el nombre de la base de datos
     */
    companion object{
        /**
         * Variable constante que contiene el nombre de la base de datos
         */
        const val DATABASE_NAME = "notes_db"
    }

}