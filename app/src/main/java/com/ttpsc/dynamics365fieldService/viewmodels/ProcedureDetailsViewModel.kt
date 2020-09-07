package com.ttpsc.dynamics365fieldService.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.*
import com.ttpsc.dynamics365fieldService.bll.models.*
import com.ttpsc.dynamics365fieldService.helpers.RefreshWorkOrdersCallback
import com.ttpsc.dynamics365fieldService.helpers.files.FileUtility
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import java.io.File
import java.lang.Exception
import javax.annotation.Resource
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.min

open class ProcedureDetailsViewModel @Inject constructor(
    private val getBookableResourceBookingDetails: GetBookableResourceBookingDetailsOperation,
    private val getBookingStatuses: GetBookingStatusesOperation,
    private val getWorkOrders: GetWorkOrdersOperation,
    private val getAlertsOperation: GetAlertsOperation,
    private val getNotesOperation: GetNotesOperation,
    private val downloadFile: DownloadFileOperation,
    private val getFormOperation: GetFormOperation,
    private val changeBookableResourceBookingStatus: ChangeBookableResourceBookingStatusOperation,
    private val getRawWorkOrdersOperation: GetRawWorkOrdersOperation,
    val createNoteOperation: CreateNoteOperation
) : ViewModel() {

    var bookingStatuses: List<BookingStatus> = listOf()
    var errorMessage: String = ""

    val loadingIndicatorVisible by lazy { MutableLiveData<Boolean>() }
    val bookableResourceBooking by lazy { MutableLiveData<Procedure>() }
    val currentPageIndex by lazy { MutableLiveData<Int>() }
    val currentPageList by lazy { MutableLiveData<List<Note>>() }
    val nextPageVisible by lazy { MutableLiveData<Boolean>() }
    val previousPageVisible by lazy { MutableLiveData<Boolean>() }
    var refreshWorkOrdersCallback: RefreshWorkOrdersCallback? = null
    val errorPopupVisible by lazy { MutableLiveData<Boolean>() }

    private val _itemsPerPage = 3
    private var _pagesCount = 0
    private var _allDataCount = 0
    private var _lastItemOnPage: Int = 0
    private val _allData: MutableList<Note> = mutableListOf()
    private var _notes: MutableList<Note> = mutableListOf()
    var notes: MutableList<Note>
        get() = _notes
        set(value) {
            setDataCount(value.count())
            _notes = value
        }

    fun getNotes(): GetNotesOperation {
        return getNotesOperation.apply {
            queryMap = mapOf(
                "\$filter" to "_objectid_value eq ${bookableResourceBooking.value?.workOrderId}",
                "\$select" to "annotationid,filename,subject,isdocument, _objectid_value, createdon",
                "\$expand" to "owninguser"
            )
        }
    }

    fun getProcedure(
        procedureIdParameter: String? = null
    ): Observable<Procedure> {
        loadingIndicatorVisible.value = true
        var _procedure: Procedure? = null

        return getBookableResourceBookingDetails
            .apply {
                bookableResourceBookingId = procedureIdParameter!!
            }
            .execute()
            .flatMap { bookableResourceBooking ->
                fetchStatuses().subscribe {
                    bookingStatuses = it
                }
                fetchWorkOrders(bookableResourceBooking)
            }.flatMap {
                fetchAlerts(it)
            }.flatMap { procedure ->
                _procedure = procedure
                val queryMap =
                    mapOf("\$select" to "formjson,formxml", "\$filter" to "name eq 'WorkOrderHMT'")
                getFormOperation
                    .apply {
                        query = queryMap
                    }.execute()

            }.flatMap { customFields ->
                if (_procedure != null) {
                    _procedure!!.customFields = customFields.toMutableList()
                }
                val query =
                    mutableMapOf("\$filter" to "msdyn_workorderid eq ${_procedure?.workOrderId}")

                if (customFields.count() > 0) {
                    query.put("\$select", customFields.map { it.fieldName }.joinToString())
                }
                return@flatMap getRawWorkOrdersOperation
                    .apply {
                        queryMap = query
                    }.execute()
            }
            .flatMap {
                if (it.second.isNullOrEmpty() == true) {
                    return@flatMap Observable.just(it)
                } else {
                    it.second?.let { it2 ->
                        errorMessage = it2
                        errorPopupVisible.value = true
                    }
                    return@flatMap getRawWorkOrdersOperation.apply {
                        queryMap = mapOf()
                    }.execute()
                }
            }
            .flatMap{
                if(it.first.count() > 0) {
                    if (_procedure?.customFields?.count() ?: 0 > 0) {
                        for (nameValue in it.first) {
                            val customField =
                                _procedure?.customFields?.find { customField -> customField.fieldName == nameValue.key }
                            if (customField != null) {
                                customField.fieldValue = nameValue.value
                            }
                        }
                    } else {
                        for (nameValue in it.first) {
                            _procedure?.customFields?.add(
                                CustomField(
                                    nameValue.key,
                                    nameValue.key.replace("msdyn", "").replace("_", "")
                                        .replace("value", ""),
                                    nameValue.value
                                )
                            )
                        }
                    }
                }

                Observable.just(Unit)
            }
            .flatMap {
                loadingIndicatorVisible.value = false
                Observable.just(_procedure)
            }
    }

    fun fetchStatuses(): Observable<List<BookingStatus>> {
        loadingIndicatorVisible.value = true
        return getBookingStatuses
            .execute()
    }

    fun openNoteAttachment(noteId: String, context: Context) {
        loadingIndicatorVisible.value = true
        val query = mapOf(
            "\$filter" to "annotationid eq $noteId",
            "\$select" to "filename,createdon,documentbody",
            "\$expand" to "owninguser"
        )
        getNotesOperation
            .apply {
                queryMap = query
            }.execute()
            .map {
                it.first()

            }.subscribe({
                val file =
                    File(context.getExternalFilesDir(null).toString(), it.fileName ?: "")
                file.writeBytes(it.documentBody.toByteArray())
                FileUtility.openWithDefaultApp(context, file)
                loadingIndicatorVisible.value = false
            }, { ex ->
                loadingIndicatorVisible.value = false
                errorMessage = context.getString(R.string.download_attachment_error)
                errorPopupVisible.value = true
                Log.e("FETCH ATTACHMENT", ex.message!!)
            })
    }

    fun changeStatus(bookingStatus: BookingStatus, bookingId: String, context: Context) {
        loadingIndicatorVisible.value = true

        changeBookableResourceBookingStatus
            .apply {
                bookingStatusId = bookingStatus.bookingStatusId!!
                bookableResourceBookingId = bookingId
            }.execute()
            .flatMap { errorPair ->
                if (errorPair.first == false) {
                    getProcedure(bookableResourceBooking.value?.id)
                } else {
                    errorMessage = errorPair.second ?: ""
                    errorPopupVisible.value = true
                    Observable.just(bookableResourceBooking.value)
                }

                getProcedure(bookableResourceBooking.value?.id)
            }
            .flatMap {
                if (it != null) {
                    bookableResourceBooking.value = it
                }
                Observable.just(Unit)
            }
            .subscribe(
                {
                    loadingIndicatorVisible.value = false
                    refreshWorkOrdersCallback?.refreshList?.onNext(Unit)
                },
                { ex ->
                    loadingIndicatorVisible.value = false
                    errorMessage = context.getString(R.string.change_status_error)
                    errorPopupVisible.value = true
                    Log.e("CHANGE STATUS ERROR", ex.message)
                }
            )
    }

    fun openImageAttachment(context: Context) {
        loadingIndicatorVisible.value = true
        openAttachment(
            bookableResourceBooking.value?.workOrder?.imageAttachmentUrl,
            "temporaryFile.jpg",
            context
        )
            .subscribe({
                loadingIndicatorVisible.value = false
            }, { ex ->
                loadingIndicatorVisible.value = false
                errorMessage = context.getString(R.string.download_image_error)
                errorPopupVisible.value = true
                Log.e("FETCH ATTACHMENT", ex.message!!)
            })
    }

    fun openVideoAttachment(context: Context) {
        loadingIndicatorVisible.value = true
        openAttachment(
            bookableResourceBooking.value?.workOrder?.videoAttachmentUrl,
            "temporaryFile.mp4",
            context
        )
            .subscribe({
                loadingIndicatorVisible.value = false
            }, { ex ->
                loadingIndicatorVisible.value = false
                errorMessage = context.getString(R.string.download_video_error)
                errorPopupVisible.value = true
                Log.e("FETCH ATTACHMENT", ex.message!!)
            })
    }

    fun openDocumentAttachment(context: Context) {
        loadingIndicatorVisible.value = true
        openAttachment(
            bookableResourceBooking.value?.workOrder?.documentAttachmentUrl,
            "temporaryFile.pdf",
            context
        )
            .subscribe({
                loadingIndicatorVisible.value = false
            }, { ex ->
                loadingIndicatorVisible.value = false
                errorMessage = context.getString(R.string.download_document_error)
                errorPopupVisible.value = true
                Log.e("FETCH ATTACHMENT", ex.message!!)
            })
    }

    fun initializeList() {
        loadingIndicatorVisible.value = true
        currentPageIndex.value = 0
        _lastItemOnPage = 0
        val itemsCountToFetch = kotlin.math.min(notes.count(), _itemsPerPage)
        val items = fetchData(0, itemsCountToFetch)
        _allData.clear()
        _allData.addAll(items)
        setCurrentDataPage()
        setButtonsVisibility()
        currentPageIndex.value = 0
        loadingIndicatorVisible.value = false
    }

    private fun setCurrentDataPage(): Boolean {
        val data: List<Note>

        _lastItemOnPage = currentPageIndex.value!! * _itemsPerPage + _itemsPerPage

        data = _allData.subList(
            currentPageIndex.value!! * _itemsPerPage,
            if (_lastItemOnPage > _allData.count() - 1) _allData.count() else _lastItemOnPage
        )

        if (data.count() > 0) {
            currentPageList.value = data
        }

        return data.count() > 0
    }

    private fun setButtonsVisibility() {
        nextPageVisible.value = currentPageIndex.value!! < _pagesCount - 1
        previousPageVisible.value = currentPageIndex.value!! > 0
    }

    fun fetchData(
        from: Int,
        to: Int
    ): List<Note> {
        return notes.subList(from, to)
    }

    fun setPreviousPage() {
        currentPageIndex.value =
            currentPageIndex.value!! - if (currentPageIndex.value!! > 0) 1 else 0

        setCurrentDataPage()
        setButtonsVisibility()
    }

    fun setDataCount(dataCount: Int) {
        _allDataCount = dataCount
        _pagesCount = ceil(dataCount.toDouble() / _itemsPerPage.toDouble()).toInt()
    }

    fun setNextPage() {
        currentPageIndex.value =
            currentPageIndex.value!! + if (currentPageIndex.value!! < _pagesCount - 1) 1 else 0

        if (setCurrentDataPage() == false) {
            val result = fetchData(
                currentPageIndex.value!! * _itemsPerPage,
                if (_lastItemOnPage > _allDataCount - 1) _allDataCount else _lastItemOnPage
            )

            _allData.addAll(result)
            setCurrentDataPage()
            setButtonsVisibility()

        } else {
            setButtonsVisibility()
        }
    }

    fun getFirstIndexOnCurrentPage(): Int {
        return currentPageIndex.value?.let { (it * _itemsPerPage) + 1 } ?: 0
    }

    fun getLastIndexOnCurrentPage(): Int {
        return currentPageIndex.value?.let { min(_allDataCount, ((it + 1) * _itemsPerPage)) } ?: 0
    }

    private fun fetchWorkOrders(bookableResourcesBooking: BookableResourceBooking): @NonNull Observable<BookableResourceBooking> {

        return getWorkOrders.apply {
            queryMap =
                mapOf<String, String>(
                    "\$filter" to "msdyn_workorderid eq ${bookableResourcesBooking.workOrderId}",
                    "\$expand" to "msdyn_serviceterritory"
                )
        }
            .execute()
            .flatMap { workOrders ->
                bookableResourcesBooking.workOrder = workOrders.first()
                getNotesOperation
                    .apply {
                        queryMap = mapOf(
                            "\$filter" to "_objectid_value eq ${bookableResourcesBooking.workOrderId}",
                            "\$select" to "annotationid,filename,subject,isdocument, _objectid_value, createdon",
                            "\$expand" to "owninguser"
                        )
                    }.execute()
            }.map {
                if (bookableResourcesBooking.workOrder != null) {
                    bookableResourcesBooking.workOrder!!.notes = it
                }
                bookableResourcesBooking
            }
    }

    private fun fetchAlerts(bookableResourcesBooking: BookableResourceBooking): @NonNull Observable<Procedure>? {
        return getAlertsOperation.apply {
            queryMap =
                mapOf<String, String>("\$filter" to "_msdyn_workorder_value eq ${bookableResourcesBooking.workOrderId}")
        }
            .execute()
            .map { alerts ->
                bookableResourcesBooking.workOrder?.iotAlerts?.addAll(alerts)
                Unit
            }.map {
                bookableResourcesBooking.toProcedure()
            }

    }

    private fun openAttachment(
        url: String?,
        temporaryFileName: String,
        context: Context
    ): Observable<Unit> {
        downloadFile.fileUrl = url!!
        return downloadFile.execute()
            .map {
                val file = File(context.getExternalFilesDir(null).toString(), temporaryFileName)
                file.writeBytes(it)
                FileUtility.openWithDefaultApp(context, file)
            }
    }
}