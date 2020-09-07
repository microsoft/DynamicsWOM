package com.ttpsc.dynamics365fieldService.core.abstraction

import io.reactivex.rxjava3.core.Observable

interface LifecycleProvidingActivity {
    val onCreate: Observable<Unit>
    val onStart: Observable<Unit>
    val onResume: Observable<Unit>
    val onPause: Observable<Unit>
    val onStop: Observable<Unit>
    val onDestroy: Observable<Unit>
}