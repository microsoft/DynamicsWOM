package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation

interface SaveEnvironmentDataOperation: Operation<Unit> {
    var environmentUrl: String?
    var userEmail: String?
}