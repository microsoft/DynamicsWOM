package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.Procedure

interface CreateProcedureOperation : Operation<Procedure> {
    var title: String
    var subject: String?
}