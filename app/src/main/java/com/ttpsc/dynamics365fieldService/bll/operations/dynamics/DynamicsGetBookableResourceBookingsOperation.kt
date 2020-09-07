package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetBookableResourcesBookingsOperation
import com.ttpsc.dynamics365fieldService.bll.models.BookableResource
import com.ttpsc.dynamics365fieldService.bll.models.BookableResourceBooking
import com.ttpsc.dynamics365fieldService.bll.models.PageableModel
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import javax.inject.Inject

class DynamicsGetBookableResourceBookingsOperation @Inject constructor(
    repository: DynamicsApiRepository) :
    GetBookableResourcesBookingsOperation {

    private val _repository = repository
    override lateinit var bookableResource: BookableResource
    override var query: Map<String, String>? = null
    override var itemsPerPage: Int? = null

    override fun execute(): Observable<PageableModel<List<BookableResourceBooking>>> {
        val queryMap =
            mutableMapOf("\$filter" to "_resource_value eq ${bookableResource.bookableResourceId}")
        query?.let {
            for (map in it){
                if(queryMap[map.key] != null) {
                    queryMap[map.key] += " and " + map.value
                }else {
                    queryMap[map.key] = map.value
                }
            }
        }

        var header: String? = null

        if (itemsPerPage != null) {
            header = "odata.maxpagesize=$itemsPerPage"
        }

        return _repository.getBookableResourceBookings(queryMap, header)
            .executeOnBackground()
            .map {
                PageableModel(
                    it.value.map { dalBookableResourceBooking -> dalBookableResourceBooking.toBookableResourceBooking() },
                    it.nextPageLink
                )
            }
    }
}
