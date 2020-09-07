package com.ttpsc.dynamics365fieldService.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetUserInfoOperation
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.LogInOperation
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.SaveEnvironmentDataOperation
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import com.ttpsc.dynamics365fieldService.core.managers.DynamicsAuthorizationManager
import javax.inject.Inject

open class LoginViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var authorizationManager: AuthorizationManager

    @Inject
    lateinit var logInOperation: LogInOperation

    @Inject
    lateinit var saveEnvironmentDataOperation: SaveEnvironmentDataOperation
}