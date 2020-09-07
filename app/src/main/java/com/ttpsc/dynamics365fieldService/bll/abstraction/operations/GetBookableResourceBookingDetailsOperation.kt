package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.BookableResourceBooking

interface GetBookableResourceBookingDetailsOperation: Operation<BookableResourceBooking> {
    var bookableResourceBookingId: String
}