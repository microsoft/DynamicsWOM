package com.ttpsc.dynamics365fieldService.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.*
import com.ttpsc.dynamics365fieldService.bll.models.Note
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class NoteDetailsViewModel  @Inject constructor(
    private val getNotesOperation: GetNotesOperation
) : ViewModel() {

    val loadingIndicatorVisible by lazy { MutableLiveData<Boolean>() }
    val note by lazy { MutableLiveData<Note>() }

    fun fetchAttachment(noteId: String): Observable<Note> {
        val query = mapOf("\$filter" to "annotationid eq $noteId", "\$select" to "filename,createdon,documentbody", "\$expand" to "owninguser")
        return getNotesOperation
            .apply {
                queryMap = query
            }.execute()
            .map {
                it.first()
            }
    }
}