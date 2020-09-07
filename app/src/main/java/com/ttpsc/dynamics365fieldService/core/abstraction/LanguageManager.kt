package com.ttpsc.dynamics365fieldService.core.abstraction

import android.app.Activity
import io.reactivex.rxjava3.core.Observable

interface LanguageManager {
    val languageChanged: Observable<String>
    fun getLanguage(): String
    fun registerForLanguageChange(activity: Activity)
}