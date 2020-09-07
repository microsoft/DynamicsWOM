package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import android.app.Activity
import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation

interface UpdateUserSessionOperation: Operation<Unit> {
    var activity: Activity?
}