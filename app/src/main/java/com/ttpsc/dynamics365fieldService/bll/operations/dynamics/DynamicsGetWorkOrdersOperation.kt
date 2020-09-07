package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetBookingStatusesOperation
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetWorkOrdersOperation
import com.ttpsc.dynamics365fieldService.bll.models.BookingStatus
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.bll.models.WorkOrder
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsGetWorkOrdersOperation  @Inject constructor(
    repository: DynamicsApiRepository
) :
    GetWorkOrdersOperation {
    private val _repository = repository
    override var queryMap: Map<String, String>? = null

    override fun execute(): Observable<List<WorkOrder>> =
        _repository.getWorkOrders(queryMap)
            .executeOnBackground()
            .map { it ->
                it.value.map { dalWorkOrder -> dalWorkOrder.toWorkOrder() }
            }
}
