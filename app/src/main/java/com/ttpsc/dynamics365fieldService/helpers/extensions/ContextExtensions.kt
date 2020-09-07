package com.ttpsc.dynamics365fieldService.helpers.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

@Suppress("unused")
fun Context.getActivity(): Activity? {
    return when (this) {
        !is ContextWrapper -> null
        is Activity -> this
        else -> this.baseContext.getActivity()
    }
}