package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetAlertsOperation
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetProceduresOperation
import com.ttpsc.dynamics365fieldService.bll.models.Alert
import com.ttpsc.dynamics365fieldService.bll.models.BookableResource
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsGetAlerts @Inject constructor(
    repository: DynamicsApiRepository
) :
    GetAlertsOperation {

    private val _repository = repository

    override var queryMap: Map<String, String>? = null

    override fun execute(): Observable<List<Alert>> {
        return _repository.getAlerts(queryMap)
            .executeOnBackground()
            .map { it ->
                it.value.map { dalAlert -> dalAlert.toAlert() }
            }
    }
}