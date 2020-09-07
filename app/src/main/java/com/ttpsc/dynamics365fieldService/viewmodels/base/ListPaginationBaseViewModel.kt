package com.ttpsc.dynamics365fieldService.viewmodels.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import kotlin.math.ceil
import kotlin.math.min

abstract class ListPaginationBaseViewModel<ItemType> : ViewModel() {
    private lateinit var _currentData: List<ItemType>
    private var _pagesCount = 0
    private var _allDataCount: Int = 0
    private val _allData: MutableList<ItemType> = mutableListOf()
    protected val query = mutableMapOf<String, String>()
    protected var nextPageLink: String? = null

    val currentPageIndex by lazy { MutableLiveData<Int>() }
    val currentPageList by lazy { MutableLiveData<List<ItemType>>() }
    val nextPageVisible by lazy { MutableLiveData<Boolean>() }
    val previousPageVisible by lazy { MutableLiveData<Boolean>() }
    val loadingIndicatorVisible by lazy { MutableLiveData<Boolean>() }

    protected open fun getItemsPerPage(): Int {
        return 4
    }

    fun initializeList(existingList : List<ItemType>? = null) {
        if (existingList == null) {
            loadingIndicatorVisible.value = true
            currentPageIndex.value = 0
            _pagesCount = 0
            _allDataCount = 0
            nextPageLink = null
            query.clear()

            fetchData(0, getItemsPerPage(), null)
                .subscribe(
                    { data ->
                        _allData.clear()
                        _allData.addAll(data)
                        setCurrentDataPage()
                        setButtonsVisibility()
                        currentPageIndex.value = 0
                        loadingIndicatorVisible.value = false
                    },
                    { ex ->
                        Log.e("FETCH DATA", ex.message!!)
                        loadingIndicatorVisible.value = false
                    })
        }
        else {
            _allData.clear()
            _allData.addAll(existingList)
            _pagesCount = 0 // This should be also intialized via parameter?
            _allDataCount = 0 // This should be also intialized via parameter?
            currentPageIndex.value = 0 // This should be also intialized via parameter?
            setCurrentDataPage()
            setButtonsVisibility()
        }
    }

    fun setDataCount(dataCount: Int) {
        _allDataCount = dataCount
        _pagesCount = ceil(dataCount.toDouble() / getItemsPerPage().toDouble()).toInt()
    }

    fun setNextPage() {
        currentPageIndex.value =
            currentPageIndex.value!! + if (currentPageIndex.value!! < _pagesCount - 1) 1 else 0

        if (setCurrentDataPage()) {
            setButtonsVisibility()
            return
        }

        loadingIndicatorVisible.value = true

        fetchData(getFirstIndexOnCurrentPage(), getLastIndexOnCurrentPage(), nextPageLink)
            .subscribe(
                { data ->
                    _allData.addAll(data)

                    setCurrentDataPage()
                    setButtonsVisibility()
                    loadingIndicatorVisible.value = false
                },
                { ex ->
                    Log.e("FETCH DATA", ex.message!!)
                    loadingIndicatorVisible.value = false
                })
    }

    fun setPreviousPage() {
        //removeLastPageData()
        currentPageIndex.value =
            currentPageIndex.value!! - if (currentPageIndex.value!! > 0) 1 else 0

        setCurrentDataPage()
        setButtonsVisibility()
    }

    private fun setButtonsVisibility() {
        nextPageVisible.value = currentPageIndex.value!! < _pagesCount - 1
        previousPageVisible.value = currentPageIndex.value!! > 0
    }

    private fun setCurrentDataPage(): Boolean {
        val firstSubListIndex = getFirstIndexOnCurrentPage()

        // This condition requires that any data will be available between first and last index
        if (firstSubListIndex > _allData.lastIndex) {
            currentPageList.value = mutableListOf()
            return false
        }

        // subList toIndex parameter is exclusive, that is why we add 1
        currentPageList.value = _allData.subList(
            firstSubListIndex,
            min(getLastIndexOnCurrentPage(), _allData.lastIndex) + 1
        )
        return true
    }

    private fun removeLastPageData() {
        try {
            if (currentPageIndex.value!! + 1 == _pagesCount) {
                for (item in currentPageList.value!!) {
                    _allData.remove(_allData.last())
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getFirstIndexOnCurrentPage(): Int {
        return currentPageIndex.value?.let { (it * getItemsPerPage())} ?: 0
    }

    fun getLastIndexOnCurrentPage(): Int {
        return min(getFirstIndexOnCurrentPage() + getItemsPerPage() - 1, _allData.size - 1)
    }

    abstract fun fetchData(
        from: Int,
        to: Int,
        nextPageUrl: String?
    ): Observable<List<ItemType>>
}