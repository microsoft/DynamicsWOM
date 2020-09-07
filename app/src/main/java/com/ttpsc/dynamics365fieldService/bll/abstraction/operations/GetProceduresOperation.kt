package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.BookableResource
import com.ttpsc.dynamics365fieldService.bll.models.Procedure

interface GetProceduresOperation : Operation<List<Procedure>>{
    var bookableResource: BookableResource
    var query: Map<String, String>?
}