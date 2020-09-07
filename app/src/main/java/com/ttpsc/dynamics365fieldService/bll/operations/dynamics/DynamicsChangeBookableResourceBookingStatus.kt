package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.google.gson.Gson
import com.ttpsc.dynamics365fieldService.AppConfiguration
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.ChangeBookableResourceBookingStatusOperation
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.models.dynamics.DalChangeStatusError
import com.ttpsc.dynamics365fieldService.dal.models.dynamics.DalChangeStatusErrorBody
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import javax.inject.Inject


class DynamicsChangeBookableResourceBookingStatus @Inject constructor(
    repository: DynamicsApiRepository
) :
    ChangeBookableResourceBookingStatusOperation {

    private val _repository = repository

    override lateinit var bookingStatusId: String
    override lateinit var bookableResourceBookingId: String

    override fun execute(): Observable<Pair<Boolean, String?>> {
        return _repository.changeBookableResourceBookingStatus(
            bookableResourceBookingId,
            mapOf("@odata.id" to "${AppConfiguration.endpoint}${AppConfiguration.ENDPOINT_SUFFIX}bookingstatuses($bookingStatusId)")
        ).executeOnBackground().map { response ->
            if (response.isSuccessful == false) {
                val error = Gson().fromJson(
                    response.errorBody()?.string(),
                    DalChangeStatusErrorBody::class.java
                )
                return@map Pair<Boolean, String?>(true, error.error?.message)
            }
            return@map Pair<Boolean, String?>(false, null)
        }
    }
}