package com.ttpsc.dynamics365fieldService.bll.abstraction

import io.reactivex.rxjava3.core.Observable

interface Operation<TResult> {

    fun execute(): Observable<TResult>
}