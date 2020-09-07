package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.WorkOrder

interface GetWorkOrdersOperation : Operation<List<WorkOrder>>{
    var queryMap: Map<String, String>?
}