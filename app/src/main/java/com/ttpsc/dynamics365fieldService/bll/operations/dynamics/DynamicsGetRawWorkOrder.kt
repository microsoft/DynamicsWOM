package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.google.gson.Gson
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetRawWorkOrdersOperation
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.models.dynamics.DalChangeStatusErrorBody
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsGetRawWorkOrder  @Inject
constructor(
    repository: DynamicsApiRepository
) :
    GetRawWorkOrdersOperation {
    private val _repository = repository
    override var queryMap: Map<String, String>? = null

    override fun execute(): Observable<Pair<Map<String, String>, String?>> =
        _repository.getRawWorkOrders(queryMap)
            .executeOnBackground()
            .map {responseBody ->

                val bodyString = responseBody.body()

                val fieldValueMap: MutableMap<String, String> = mutableMapOf()
                if(responseBody.isSuccessful &&  bodyString != null) {
                    val responseString = bodyString.substringAfter("value\":")
                        .replace("{", "")
                        .replace("}", "")
                        .replace("]", "")
                        .replace("[", "")
                        .replace("\"", "")

                    val fieldValues = responseString.split(",")

                    for (fieldValue in fieldValues) {
                        val splitted = fieldValue.split(
                            delimiters = *arrayOf(":"),
                            ignoreCase = true,
                            limit = 2
                        )
                        if (splitted.count() > 1 && splitted[1].contains("null") == false) {
                            fieldValueMap.put(splitted[0], splitted[1])
                        }
                    }
                }else
                {
                    if (responseBody.isSuccessful == false) {
                        val error = Gson().fromJson(
                            responseBody.errorBody()?.string(),
                            DalChangeStatusErrorBody::class.java
                        )
                        return@map Pair<Map<String, String>, String?>(fieldValueMap, error.error?.message)
                    }
                }
                return@map Pair<Map<String, String>, String?>(fieldValueMap, null)
            }
}