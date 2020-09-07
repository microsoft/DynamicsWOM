package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetUserInfoOperation
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetWorkOrdersOperation
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.bll.models.UserInfo
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsGetUserInfo @Inject constructor(
    repository: DynamicsApiRepository
) :
    GetUserInfoOperation {
    override lateinit var email: String

    private val _repository = repository

    override fun execute(): Observable<List<UserInfo>> {
        val query = mapOf<String, String>(
            "\$select" to "systemuserid, fullname, windowsliveid, domainname, firstname, lastname",
            "\$filter" to "domainname eq $email"
        )

        return _repository.getUserInfo(query)
            .executeOnBackground()
            .map { it ->
                it.value.map { dalUserInfo -> dalUserInfo.toUserInfo() }
            }
    }
}
