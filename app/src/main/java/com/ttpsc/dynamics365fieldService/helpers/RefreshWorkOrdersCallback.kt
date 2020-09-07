package com.ttpsc.dynamics365fieldService.helpers

import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.Serializable

class RefreshWorkOrdersCallback: Serializable {
    @Transient
    var refreshList: PublishSubject<Unit> = PublishSubject.create()
}