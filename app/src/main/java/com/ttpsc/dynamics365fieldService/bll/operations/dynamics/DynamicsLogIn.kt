package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import android.app.Activity
import android.content.Context
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalException
import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.LogInOperation
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.SaveEnvironmentDataOperation
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlin.math.log

class DynamicsLogIn @Inject constructor(
    private val authorizationManager: AuthorizationManager,
    private val clientApplication: ISingleAccountPublicClientApplication,
    private val saveEnvironmentDataOperation: SaveEnvironmentDataOperation
) : LogInOperation {

    private val subject: PublishSubject<Boolean> = PublishSubject.create()
    override var userName: String? = null
    override var environmentUrl: String? = null
    override var activity: Activity? = null

    override fun execute(): Observable<Boolean> {
        if (activity == null) {
            return Observable.empty()
        }
        val login = if (userName != null) userName!! else ""
        val environment =  if (environmentUrl != null) environmentUrl!! else ""

        clientApplication.signIn(activity!!, login, arrayOf("$environment//user_impersonation"), getAuthInteractiveCallback(userName, environmentUrl))
        return subject
    }

    private fun getAuthInteractiveCallback(email:String?, environment: String?): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                println("Successfully authenticated")
                saveEnvironmentDataOperation.apply {
                    environmentUrl = environment
                    userEmail = email
                }.execute().subscribe()

                authorizationManager.setAuthorizationToken(authenticationResult.accessToken)
                authorizationManager.saveTokenExpirationDate(authenticationResult.expiresOn)
                subject.onNext(true)
            }

            override fun onError(exception: MsalException) {
                println("Authentication failed: $exception")
                subject.onError(exception)
            }

            override fun onCancel() {
                subject.onNext(false)
            }
        }
    }
}