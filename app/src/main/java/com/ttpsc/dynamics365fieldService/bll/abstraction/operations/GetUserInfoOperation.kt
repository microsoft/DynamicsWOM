package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.bll.models.UserInfo

interface GetUserInfoOperation : Operation<List<UserInfo>>{
    var email: String
}