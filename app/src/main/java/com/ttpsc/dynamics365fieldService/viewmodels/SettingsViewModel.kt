package com.ttpsc.dynamics365fieldService.viewmodels

import androidx.lifecycle.ViewModel
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import javax.inject.Inject

class SettingsViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var authorizationManager: AuthorizationManager

    fun logout() {
        authorizationManager.signOut()
    }
}