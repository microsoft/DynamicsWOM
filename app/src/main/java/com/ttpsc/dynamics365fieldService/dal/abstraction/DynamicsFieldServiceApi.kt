package com.ttpsc.dynamics365fieldService.dal.abstraction

import com.ttpsc.dynamics365fieldService.AppConfiguration
import com.ttpsc.dynamics365fieldService.dal.models.dynamics.*
import com.ttpsc.dynamics365fieldService.dal.models.dynamics.customFields.FormWrapper
import io.reactivex.rxjava3.core.Observable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface DynamicsFieldServiceApi {
    @GET("bookableresources")
    fun getBookableResources(@QueryMap options: Map<String, String>?): Observable<DalBaseDynamicsModel<DalBookableResource>>

    @GET("bookableresourcebookings")
    fun getBookableResourceBookings(@Header("Prefer") itemsPerPageCountHeader: String?, @QueryMap options: Map<String, String>?): Observable<DalBaseDynamicsModel<DalBookableResourceBooking>>

    @GET("bookingstatuses")
    fun getBookingStatuses(): Observable<DalBaseDynamicsModel<DalDynamicsBookingStatus>>

    @GET("msdyn_workorders")
    fun getWorkOrders(@QueryMap options: Map<String, String>?): Observable<DalBaseDynamicsModel<DalDynamicsWorkOrder>>

    @GET("systemusers")
    fun getUserInfo(@QueryMap options: Map<String, String>?): Observable<DalBaseDynamicsModel<DalDynamicsUserInfo>>

    @GET("msdyn_iotalerts")
    fun getAlerts(@QueryMap options: Map<String, String>?): Observable<DalBaseDynamicsModel<DalDynamicsAlert>>

    @PUT("bookableresourcebookings({bookableResourceBookingId})/BookingStatus/\$ref")
    fun changeBookableResourceBookingStatus(@Path("bookableResourceBookingId") bookableResourceBookingId: String, @Body bookingStatusBody: Map<String, String>): Observable<Response<Void>>

    @GET("annotations")
    fun getNotes(@QueryMap options: Map<String, String>?): Observable<DalBaseDynamicsModel<DalNote>>

    @POST("annotations")
    fun createNote(@Body note: DalCreateNoteRequestBody): Observable<Response<Void>>

    @GET("systemforms")
    fun downloadForm(@QueryMap options: Map<String, String>?): Observable<DalBaseDynamicsModel<FormWrapper>>

    @GET("msdyn_workorders")
    fun getRawWorkOrders(@QueryMap options: Map<String, String>?): Observable<Response<String>>

    @GET
    fun downloadFile(
        @Url fileUrl: String?, @Tag leaveUrl: String = AppConfiguration.RequestTags.leaveUrl
    ): Observable<ResponseBody?>
}
