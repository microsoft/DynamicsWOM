package com.ttpsc.dynamics365fieldService.core.di.api

import android.content.SharedPreferences
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.LogInOperation
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.SaveEnvironmentDataOperation
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.UpdateUserSessionOperation
import com.ttpsc.dynamics365fieldService.bll.operations.dynamics.DynamicsLogIn
import com.ttpsc.dynamics365fieldService.bll.operations.dynamics.DynamicsSaveEnvironmentDataOperation
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import dagger.Module
import dagger.Provides

@Module
class AuthenticationModule {

    @Provides
    fun provideLogInOperation(
        authorizationManager: AuthorizationManager,
        clientApplication: ISingleAccountPublicClientApplication,
        saveEnvironmentOperation: SaveEnvironmentDataOperation
    ): LogInOperation = DynamicsLogIn(authorizationManager, clientApplication, saveEnvironmentOperation)

    @Provides
    fun provideSaveEnvironmentOperation(sharedPreferences: SharedPreferences): SaveEnvironmentDataOperation =
        DynamicsSaveEnvironmentDataOperation(sharedPreferences)
}