package com.danielgs.nonotes.di

import android.app.Application
import androidx.room.Room
import com.danielgs.nonotes.notes.data.NoteDatabase
import com.danielgs.nonotes.notes.data.repository.NoteRepositoryImpl
import com.danielgs.nonotes.notes.domain.repository.NoteRepository
import com.danielgs.nonotes.notes.domain.use_case.AddNote
import com.danielgs.nonotes.notes.domain.use_case.DeleteNote
import com.danielgs.nonotes.notes.domain.use_case.GetNote
import com.danielgs.nonotes.notes.domain.use_case.GetNotes
import com.danielgs.nonotes.notes.domain.use_case.NoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Encargado de la inyección de dependencias (Dagger Hilt)
 *
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Inyecta la base de datos de la aplicación
     *
     * @param app Contexto de aplicación
     */
    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase{
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    /**
     * Inyecta el repositorio de la base de datos
     *
     * @param db Base de datos
     * @return Devuelve el repositorio
     */
    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository{
        return NoteRepositoryImpl(db.noteDao)
    }

    /**
     * Inyecta los usos de caso de nuestra aplicación
     *
     * @param repository Repositorio para proveer de datos a los usos de caso
     * @return Devuelve los usos de caso
     */
    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases{
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository)
        )
    }

}