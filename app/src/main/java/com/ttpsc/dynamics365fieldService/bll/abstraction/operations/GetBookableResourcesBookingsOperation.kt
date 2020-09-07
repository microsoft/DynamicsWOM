package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.BookableResource
import com.ttpsc.dynamics365fieldService.bll.models.BookableResourceBooking
import com.ttpsc.dynamics365fieldService.bll.models.PageableModel

interface GetBookableResourcesBookingsOperation :
    Operation<PageableModel<List<BookableResourceBooking>>> {
    var bookableResource: BookableResource
    var query: Map<String, String>?
    var itemsPerPage: Int?
}