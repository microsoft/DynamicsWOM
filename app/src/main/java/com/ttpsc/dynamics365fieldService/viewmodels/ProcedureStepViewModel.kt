package com.ttpsc.dynamics365fieldService.viewmodels

import androidx.lifecycle.MutableLiveData
import com.ttpsc.dynamics365fieldService.bll.models.DescriptionLine
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.viewmodels.base.ListPaginationBaseViewModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ProcedureStepViewModel @Inject constructor() :
    ListPaginationBaseViewModel<DescriptionLine>() {
    val procedure by lazy { MutableLiveData<Procedure>() }
    val step by lazy { MutableLiveData<Procedure>() }
    var visitedStepsIds = mutableListOf<String>()

    fun goToPreviousStep() {
        val currentStepIndex = procedure.value?.children?.indexOf(step.value)
        step.value = procedure.value?.children?.elementAt(currentStepIndex!! - 1)
    }

    fun goToNextStep() {
        val currentStepIndex = procedure.value?.children?.indexOf(step.value)
        step.value = procedure.value?.children?.elementAt(currentStepIndex!! + 1)
    }

    fun setSelectedStep(navigationSelectedStep: Procedure?) {
        if (navigationSelectedStep != null) {
            step.value = navigationSelectedStep
        } else if (procedure.value?.children?.count() ?: 0 > 0 && step.value == null) {

            step.value =
                procedure.value?.children?.first()
        }
    }

    override fun getItemsPerPage(): Int {
        return 3
    }

    override fun fetchData(from: Int, to: Int, itemsPerPage: String?): Observable<List<DescriptionLine>> {
        TODO("Not yet implemented")
    }
}