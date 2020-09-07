package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetBookingStatusesOperation
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetProceduresOperation
import com.ttpsc.dynamics365fieldService.bll.models.BookingStatus
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsGetBookingStatusesOperation @Inject constructor(
    repository: DynamicsApiRepository
) :
    GetBookingStatusesOperation {
    private val _repository = repository

    override fun execute(): Observable<List<BookingStatus>> =
        _repository.getBookingStatues()
            .executeOnBackground()
            .map { it ->
                it.value.map { dalBookingStatus -> dalBookingStatus.toBookingStatus() }
            }
}
