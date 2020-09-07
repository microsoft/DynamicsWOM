package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetBookableResourcesOperation
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetProceduresOperation
import com.ttpsc.dynamics365fieldService.bll.models.BookableResource
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsGetBookableResource @Inject constructor(
    repository: DynamicsApiRepository,
    authorizationManager: AuthorizationManager
) :
    GetBookableResourcesOperation {

    private val _repository = repository
    private val _authorizationManager = authorizationManager

    override fun execute(): Observable<BookableResource> {
        val queryMap =
            mapOf("\$filter" to "_userid_value eq ${_authorizationManager.getUserResourceId()}")

        return _repository.getBookableResources(queryMap)
            .executeOnBackground()
            .map { it ->
                it.value.first().toBookableResource()
                //TODO remove and throw exception if there is no resource
                /*if(it.value.count() > 0) {
                    it.value.first().toBookableResource()
                }else{

                    BookableResource("", "6ff1d0b1-32a0-ea11-a812-000d3a33fb78")
                }*/
            }
    }
}