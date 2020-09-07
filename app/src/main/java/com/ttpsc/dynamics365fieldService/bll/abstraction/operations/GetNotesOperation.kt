package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.Note
import com.ttpsc.dynamics365fieldService.bll.models.WorkOrder

interface GetNotesOperation : Operation<List<Note>> {
    var queryMap: Map<String, String>?
}