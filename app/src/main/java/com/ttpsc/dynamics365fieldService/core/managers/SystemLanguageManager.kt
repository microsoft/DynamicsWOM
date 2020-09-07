package com.ttpsc.dynamics365fieldService.core.managers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ttpsc.dynamics365fieldService.core.abstraction.LanguageManager
import com.ttpsc.dynamics365fieldService.core.abstraction.LifecycleProvidingActivity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.*

class SystemLanguageManager : LanguageManager {

    private val _languageChangeSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private var lifecycleActivity: LifecycleProvidingActivity? = null
    private var _languageReceiverRegistered = false
    override val languageChanged: Observable<String>
        get() = _languageChangeSubject

    override fun getLanguage(): String =
        Locale.getDefault().language.toLowerCase(Locale.ROOT)

    override fun registerForLanguageChange(activity: Activity) {
        lifecycleActivity = activity as LifecycleProvidingActivity
        val filter = IntentFilter(Intent.ACTION_LOCALE_CHANGED)

        val languageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                _languageChangeSubject.onNext(getLanguage())
            }
        }

        lifecycleActivity?.onStop?.subscribe {
            unregisterReceiver(activity, languageReceiver)
        }

        lifecycleActivity?.onDestroy?.subscribe {
            unregisterReceiver(activity, languageReceiver)
        }

        lifecycleActivity?.onResume?.subscribe {
            registerReceiver(activity, languageReceiver, filter)
        }
    }

    private fun registerReceiver(
        activity: Activity,
        languageReceiver: BroadcastReceiver,
        filter: IntentFilter
    ) {
        if (_languageReceiverRegistered == false) {
            activity.registerReceiver(languageReceiver, filter)
            _languageReceiverRegistered = true
        }
    }

    private fun unregisterReceiver(
        activity: Activity,
        languageReceiver: BroadcastReceiver
    ) {
        if (_languageReceiverRegistered) {
            activity.unregisterReceiver(languageReceiver)
            _languageReceiverRegistered = false
        }
    }
}