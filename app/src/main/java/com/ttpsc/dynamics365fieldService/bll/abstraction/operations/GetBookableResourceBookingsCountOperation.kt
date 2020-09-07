package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.BookableResource

interface GetBookableResourceBookingsCountOperation : Operation<Int> {
    var bookableResource: BookableResource
    var query: Map<String, String>?
}