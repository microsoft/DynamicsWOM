package com.ttpsc.dynamics365fieldService.dal.repository

import com.ttpsc.dynamics365fieldService.dal.abstraction.DynamicsFieldServiceApi
import com.ttpsc.dynamics365fieldService.dal.models.dynamics.*
import com.ttpsc.dynamics365fieldService.dal.models.dynamics.customFields.FormWrapper
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class DynamicsApiRepository @Inject constructor(
    retrofit: Retrofit
) {
    private val _api = retrofit.create(DynamicsFieldServiceApi::class.java)

    fun getUserInfo(queryMap: Map<String, String>?) : Observable<DalBaseDynamicsModel<DalDynamicsUserInfo>> =
        _api.getUserInfo(queryMap)

    fun getBookableResources(queryMap: Map<String, String>?) : Observable<DalBaseDynamicsModel<DalBookableResource>> =
        _api.getBookableResources(queryMap)

    fun getBookableResourceBookings(queryMap: Map<String, String>?, itemsPerPageHeader: String?) : Observable<DalBaseDynamicsModel<DalBookableResourceBooking>> =
        _api.getBookableResourceBookings(itemsPerPageHeader, queryMap)

    fun getBookableResourceBookingDetails(queryMap: Map<String, String>?) : Observable<DalBaseDynamicsModel<DalBookableResourceBooking>> =
        _api.getBookableResourceBookings(null, queryMap)

    fun getBookingStatues() : Observable<DalBaseDynamicsModel<DalDynamicsBookingStatus>> =
        _api.getBookingStatuses()

    fun getWorkOrders(queryMap: Map<String, String>?) : Observable<DalBaseDynamicsModel<DalDynamicsWorkOrder>> =
        _api.getWorkOrders(queryMap)

    fun getAlerts(queryMap: Map<String, String>?) : Observable<DalBaseDynamicsModel<DalDynamicsAlert>> =
        _api.getAlerts(queryMap)

    fun changeBookableResourceBookingStatus(bookableResourceBookingId: String, bookingStatusBody: Map<String, String>): Observable<Response<Void>> =
        _api.changeBookableResourceBookingStatus(bookableResourceBookingId, bookingStatusBody)

    fun getNotes(queryMap: Map<String, String>?) : Observable<DalBaseDynamicsModel<DalNote>> =
        _api.getNotes(queryMap)

    fun createNote(note: DalCreateNoteRequestBody) : Observable<Response<Void>> =
        _api.createNote(note)

    fun downloadFile(url: String) =
        _api.downloadFile(url)

    fun downloadForm(queryMap: Map<String, String>?): Observable<DalBaseDynamicsModel<FormWrapper>>  =
        _api.downloadForm(queryMap)

    fun getRawWorkOrders(queryMap: Map<String, String>?) : Observable<Response<String>> =
        _api.getRawWorkOrders(queryMap)

    fun getCategories() = NotImplementedError()
    fun getCategory(categoryName: String) =
        NotImplementedError()
}