package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.Alert
import com.ttpsc.dynamics365fieldService.bll.models.BookableResource

interface GetAlertsOperation : Operation<List<Alert>>{
    var queryMap: Map<String, String>?
}