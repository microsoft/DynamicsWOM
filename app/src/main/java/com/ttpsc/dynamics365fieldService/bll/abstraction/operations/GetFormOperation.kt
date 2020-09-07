package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.CustomField

interface GetFormOperation: Operation<List<CustomField>> {
    var query: Map<String, String>?
}