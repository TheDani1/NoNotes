package com.danielgs.nonotes.notes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.danielgs.nonotes.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que especifica el Data Acces Object de las notas
 *
 */
@Dao
interface NoteDao {

    /**
     * Devuelve todas las notas
     *
     * @return Flow con una lista de tipo [Note]
     */
    @Query("SELECT * FROM note")
    fun getNotes() : Flow<List<Note>>

    /**
     * Devuelve la nota con ese ID
     *
     * @param id [Int] que identifica cada nota de forma única e inequívoca
     * @return Flow con una lista de tipo [Note]
     */
    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    /**
     * Inserta una nota en la base de datos
     *
     * @param note [Note] a insertar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note : Note)

    /**
     * Elimina una nota
     *
     * @param note [Note] a eliminar
     */
    @Delete
    suspend fun deleteNote(note: Note)
}