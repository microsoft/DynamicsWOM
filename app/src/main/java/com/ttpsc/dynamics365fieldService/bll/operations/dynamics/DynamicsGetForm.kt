package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetFormOperation
import com.ttpsc.dynamics365fieldService.bll.models.CustomField
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsGetForm @Inject constructor(
    repository: DynamicsApiRepository
) : GetFormOperation {

    private val _repository = repository
    override var query: Map<String, String>? = null
    override fun execute(): Observable<List<CustomField>> {
        return _repository.downloadForm(query)
            .executeOnBackground()
            .map { it ->
                if (it.value.count() > 0) {
                    it.value.first().parseForm().map { it.toCustomField() }
                } else {
                    mutableListOf()
                }
            }
    }
}