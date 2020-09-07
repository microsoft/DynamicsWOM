package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.Note

interface GetRawWorkOrdersOperation  : Operation<Pair<Map<String, String>, String?>> {
    var queryMap: Map<String, String>?
}