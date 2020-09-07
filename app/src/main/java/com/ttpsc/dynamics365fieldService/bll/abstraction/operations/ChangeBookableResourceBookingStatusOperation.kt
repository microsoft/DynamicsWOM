package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation

interface ChangeBookableResourceBookingStatusOperation: Operation<Pair<Boolean, String?>> {
    var bookingStatusId: String
    var bookableResourceBookingId: String
}