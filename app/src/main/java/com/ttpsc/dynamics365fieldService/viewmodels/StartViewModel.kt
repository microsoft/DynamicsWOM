package com.ttpsc.dynamics365fieldService.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.ttpsc.dynamics365fieldService.AppConfiguration
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import javax.inject.Inject

class StartViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var authorizationManager: AuthorizationManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    fun checkIfUserIsLoggedIn() = authorizationManager.getAuthorizationHeader() != null

    fun loadEnvironmentUrl() {
        AppConfiguration.endpoint = sharedPreferences.getString(AppConfiguration.StorageKeys.environmentKey,"")!!
    }
}
