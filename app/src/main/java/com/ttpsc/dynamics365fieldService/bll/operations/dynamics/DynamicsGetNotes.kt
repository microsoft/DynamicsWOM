package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetNotesOperation
import com.ttpsc.dynamics365fieldService.bll.models.Note
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsGetNotes   @Inject constructor(
    repository: DynamicsApiRepository
) :
    GetNotesOperation {
    private val _repository = repository
    override var queryMap: Map<String, String>? = null

    override fun execute(): Observable<List<Note>> =
        _repository.getNotes(queryMap)
            .executeOnBackground()
            .map { it ->
                it.value.map { dalNote -> dalNote.toNote() }
            }.map {
                it.sortedByDescending { note -> note.date }
            }
}
