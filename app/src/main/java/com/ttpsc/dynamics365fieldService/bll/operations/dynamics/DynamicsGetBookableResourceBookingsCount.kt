package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetBookableResourceBookingsCountOperation
import com.ttpsc.dynamics365fieldService.bll.models.BookableResource
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsGetBookableResourceBookingsCount @Inject constructor(
    repository: DynamicsApiRepository
) :
    GetBookableResourceBookingsCountOperation {

    private val _repository = repository
    override lateinit var bookableResource: BookableResource
    override var query: Map<String, String>? = null

    override fun execute(): Observable<Int> {
        val queryMap =
            mutableMapOf(
                "\$filter" to "_resource_value eq ${bookableResource.bookableResourceId}",
                "\$select" to "bookableresourcebookingid",
                "\$count" to "true"
            )
        query?.let {
            for (map in it){
                if(queryMap[map.key] != null) {
                    queryMap[map.key] += " and " + map.value
                }else {
                    queryMap[map.key] = map.value
                }
            }
        }

        return _repository.getBookableResourceBookings(queryMap, null)
            .executeOnBackground()
            .map { it ->
                it.count
            }
    }
}
