package com.danielgs.nonotes.notes.domain.use_case

import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.domain.repository.NoteRepository
import com.danielgs.nonotes.notes.domain.util.NoteOrder
import com.danielgs.nonotes.notes.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Uso de caso para obtener todas las notas por orden
 *
 * @property repository Repositorio para el manejo de datos
 */
class GetNotes(
    private val repository: NoteRepository
) {

    /**
     * Uso de caso para obtener una nota con un id.
     *
     * @property noteOrder [NoteOrder] para obtener las listas en ese orden.
     * @return Devuelve un [Flow] de una [List] de [Note]
     */
    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending)): Flow<List<Note>>{

        return repository.getNotes().map {
            notes ->
            when(noteOrder.orderType){
                is OrderType.Ascending -> {

                    when(noteOrder){
                        is NoteOrder.Title ->
                            notes.sortedBy {
                                it.title.lowercase()
                            }

                        is NoteOrder.Date ->
                            notes.sortedBy {
                                it.timestamp
                            }

                        is NoteOrder.Color ->
                            notes.sortedBy {
                                it.color
                            }
                    }

                }

                is OrderType.Descending -> {

                    when(noteOrder){
                        is NoteOrder.Title ->
                            notes.sortedByDescending {
                                it.title.lowercase()
                            }

                        is NoteOrder.Date ->
                            notes.sortedByDescending {
                                it.timestamp
                            }

                        is NoteOrder.Color ->
                            notes.sortedByDescending {
                                it.color
                            }
                    }

                }
            }
        }

    }
}