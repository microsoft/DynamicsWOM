package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetBookableResourceBookingDetailsOperation
import com.ttpsc.dynamics365fieldService.bll.models.BookableResourceBooking
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsGetBookableResourceBookingDetails  @Inject constructor(
    repository: DynamicsApiRepository
): GetBookableResourceBookingDetailsOperation {
    private val _repository = repository

    override var bookableResourceBookingId: String = ""

    override fun execute(): Observable<BookableResourceBooking> {
        val queryMap =
            mutableMapOf("\$filter" to "bookableresourcebookingid eq $bookableResourceBookingId")

        return _repository.getBookableResourceBookingDetails(queryMap)
            .executeOnBackground()
            .map { it ->
                    it.value.first().toBookableResourceBooking()
            }
    }
}