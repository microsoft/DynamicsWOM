package com.ttpsc.dynamics365fieldService.viewmodels

import androidx.lifecycle.MutableLiveData
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.viewmodels.base.ListPaginationBaseViewModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ProcedureStepsViewModel @Inject constructor() : ListPaginationBaseViewModel<Procedure>() {
    val procedure by lazy { MutableLiveData<Procedure>() }
    override fun fetchData(from: Int, to: Int, itemsPerPage: String?): Observable<List<Procedure>> {
        TODO("Not yet implemented")
    }
}