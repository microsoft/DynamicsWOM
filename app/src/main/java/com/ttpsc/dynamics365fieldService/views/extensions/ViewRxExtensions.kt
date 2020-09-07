package com.ttpsc.dynamics365fieldService.views.extensions

import android.view.View
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.annotations.CheckReturnValue
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

@CheckReturnValue
fun View.clicks(): Observable<Unit> {
    return ViewClickObservable(this)
}

private class ViewClickObservable(
    private val view: View
) : Observable<Unit>() {

    override fun subscribeActual(observer: Observer<in Unit>) {
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.setOnClickListener(listener)
    }

    private class Listener(
        private val view: View,
        private val observer: Observer<in Unit>
    ) : MainThreadDisposable(), View.OnClickListener {

        override fun onClick(v: View) {
            if (!isDisposed) {
                observer.onNext(Unit)
            }
        }

        override fun onDispose() {
            view.setOnClickListener(null)
        }
    }
}