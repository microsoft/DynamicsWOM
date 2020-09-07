package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import android.content.SharedPreferences
import com.ttpsc.dynamics365fieldService.AppConfiguration
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.SaveEnvironmentDataOperation
import com.ttpsc.dynamics365fieldService.core.managers.DynamicsAuthorizationManager
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsSaveEnvironmentDataOperation @Inject constructor(val sharedPreferences: SharedPreferences) :
    SaveEnvironmentDataOperation {
    override var environmentUrl: String? = null
    override var userEmail: String? = null
    override fun execute(): Observable<Unit> {
        return Observable.create { observer ->

            if (environmentUrl == null || userEmail == null) {
                observer.onNext(Unit)
            }

            sharedPreferences
                .edit()
                .putString(AppConfiguration.StorageKeys.environmentKey, environmentUrl)
                .putString(AppConfiguration.StorageKeys.userAccountKey, userEmail)
                .apply()

            AppConfiguration.endpoint = environmentUrl!!
            observer.onNext(Unit)
        }
    }
}