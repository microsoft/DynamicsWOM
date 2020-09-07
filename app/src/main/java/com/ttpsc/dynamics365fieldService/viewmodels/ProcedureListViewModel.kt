package com.ttpsc.dynamics365fieldService.viewmodels

import androidx.lifecycle.MutableLiveData
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.*
import com.ttpsc.dynamics365fieldService.bll.models.*
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import com.ttpsc.dynamics365fieldService.helpers.models.SortingType
import com.ttpsc.dynamics365fieldService.viewmodels.base.ListPaginationBaseViewModel
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import java.net.URLDecoder
import javax.inject.Inject

class ProcedureListViewModel @Inject constructor(
    private val getBookableResourcesBookings: GetBookableResourcesBookingsOperation,
    private val getBookingStatuses: GetBookingStatusesOperation,
    private val getWorkOrders: GetWorkOrdersOperation,
    private val getBookableResources: GetBookableResourcesOperation,
    private val getAlertsOperation: GetAlertsOperation,
    private val getBookableResourcesBookingsCount: GetBookableResourceBookingsCountOperation,
    private val getUserInfo: GetUserInfoOperation,
    private val authorizationManager: AuthorizationManager
) : ListPaginationBaseViewModel<Procedure>() {

    var bookingStatuses: Array<BookingStatus> = arrayOf()

    var bookableResourcesBookings: List<BookableResourceBooking>? = null
    var listNeedsReload = false
    var status: Int = 0
    val sortBy by lazy { MutableLiveData<SortingType>() }
    val sortAscending by lazy { MutableLiveData<Boolean>() }

    override fun fetchData(
        from: Int,
        to: Int,
        nextPageUrl: String?
    ): Observable<List<Procedure>> {
        if (sortBy.value != null) {
            var sortByValue = ""
            when (sortBy.value) {
                SortingType.WORK_ORDER -> sortByValue = "_msdyn_workorder_value"
                SortingType.STATUS -> sortByValue = "_bookingstatus_value"
                SortingType.END_TIME -> sortByValue = "endtime"
            }

            query.put(
                "\$orderby",
                (sortByValue + (if (sortAscending.value == true) " asc" else " desc"))
            )
        }

        val startOperation = if (authorizationManager.getUserResourceId().isNullOrEmpty()) {
            setUserInfo()
        } else {
            Observable.create<Unit> { observer ->
                observer.onNext(Unit)
            }
        }

        return startOperation.flatMap {
            getBookingStatuses.execute()
        }
            .flatMap { downloadedStatuses ->
                bookingStatuses = downloadedStatuses.toTypedArray()

                getBookableResources.execute()
            }.flatMap { _bookableResource ->
                if (nextPageUrl == null || nextPageUrl.isEmpty()) {
                    configureStatusFilter()
                } else {
                    configureNextPageQuery(nextPageUrl)
                }

                getBookableResourcesBookingsCount
                    .apply {
                        bookableResource = _bookableResource
                        query = this@ProcedureListViewModel.query
                    }
                    .execute()
                    .map { count ->
                        setDataCount(count)
                        _bookableResource
                    }
            }
            .flatMap { _bookableResource ->
                getBookableResourcesBookings
                    .apply {
                        bookableResource = _bookableResource
                        query = this@ProcedureListViewModel.query
                        itemsPerPage = getItemsPerPage()
                    }
                    .execute()
            }
            .flatMap { _pageableModel ->
                fetchWorkOrders(_pageableModel)

            }.flatMap {
                fetchAlerts()
            }
    }

    private fun setUserInfo(): Observable<Unit> {
        return getUserInfo.apply {
            email = "'" + (authorizationManager.getUserEmail() ?: "") + "'"
        }.execute()
            .flatMap { userInfo ->
                authorizationManager.setUserData(userInfo.firstOrNull())
                Observable.just(Unit)
            }
    }

    private fun configureStatusFilter() {
        query.put(
            "\$filter",
            "statecode eq 0" // Typically in Dynamics statecode 0 is "Active" bookings, 1 is "Inactive" bookings
        )

        // TODO Use below filter if filtering based on status
//        query.put(
//            "\$filter",
//            "_bookingstatus_value ne ${bookingStatuses[2].bookingStatusId}"
//        )
    }

    private fun configureNextPageQuery(nextPageUrl: String?) {
        var nextPageQuery: String? = null

        if (nextPageUrl != null && nextPageUrl.isNotEmpty()) {
            nextPageQuery =
                nextPageLink!!.substring(nextPageLink!!.indexOf('?'), nextPageLink!!.length)
        }
        query.clear()
        nextPageQuery = URLDecoder.decode(nextPageQuery, "UTF-8");

        val queryParts = nextPageQuery.replace("?", "").split("&")

        for (part in queryParts) {
            val nameValueQuery =
                part.split(delimiters = *arrayOf("="), ignoreCase = true, limit = 2)
            query.put(nameValueQuery[0], nameValueQuery[1])
        }
    }

    private fun fetchWorkOrders(_pageableModel: PageableModel<List<BookableResourceBooking>>): @NonNull Observable<List<BookableResourceBooking>>? {
        bookableResourcesBookings = _pageableModel.model
        nextPageLink = _pageableModel.nextPageLink

        val workOrderOperations = mutableListOf<Observable<Unit>>()

        for (bookableResourceBooking in bookableResourcesBookings!!) {
            if (bookableResourceBooking.workOrderId != null) {
                val localQueryMap =
                    mapOf<String, String>("\$filter" to "msdyn_workorderid eq ${bookableResourceBooking.workOrderId}")
                workOrderOperations.add(getWorkOrders.apply { queryMap = localQueryMap }
                    .execute()
                    .flatMap { workOrders ->
                        bookableResourceBooking.workOrder = workOrders.first()
                        Observable.just(Unit)
                    })
            }
        }

        return if (workOrderOperations.count() > 0) {
            Observable.zip(workOrderOperations) {}.map {
                bookableResourcesBookings
            }
        } else {
            Observable.just(bookableResourcesBookings)
        }
    }

    private fun fetchAlerts(): @NonNull Observable<List<Procedure>>? {
        val alertOperations = mutableListOf<Observable<Unit>>()

        for (bookableResourceBooking in bookableResourcesBookings!!) {
            if (bookableResourceBooking.workOrderId != null) {
                val localQueryMap =
                    mapOf<String, String>("\$filter" to "_msdyn_workorder_value eq ${bookableResourceBooking.workOrderId}")
                alertOperations.add(getAlertsOperation.apply { queryMap = localQueryMap }
                    .execute()
                    .map { alerts ->
                        if (alerts != null && alerts.count() > 0) {
                            bookableResourceBooking.workOrder?.iotAlerts?.addAll(alerts)
                        }
                        Unit
                    })
            }
        }

        val procedures = mutableListOf<Procedure>()

        return if (alertOperations.count() > 0) {
            Observable.zip(alertOperations) {}.map {

                for (bookableResourceBooking in bookableResourcesBookings!!) {
                    procedures.add(bookableResourceBooking.toProcedure())
                }

                procedures
            }
        } else {
            return Observable.just(procedures)
        }
    }
}
