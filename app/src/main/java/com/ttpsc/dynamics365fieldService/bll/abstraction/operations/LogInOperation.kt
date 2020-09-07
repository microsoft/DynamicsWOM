package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import android.app.Activity
import android.content.Context
import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation


interface LogInOperation : Operation<Boolean> {
    var userName: String?
    var environmentUrl: String?
    var activity: Activity?

}