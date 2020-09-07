package com.ttpsc.dynamics365fieldService.core.abstraction

import com.ttpsc.dynamics365fieldService.bll.models.UserInfo
import io.reactivex.rxjava3.core.Observable
import okhttp3.Authenticator
import okhttp3.Interceptor
import java.util.*

interface AuthorizationManager: Interceptor, Authenticator {
    fun setAuthorizationToken(authToken: String)
    fun getAuthorizationHeader(): String?
    fun signOut()
    fun setUserData(userInfo: UserInfo?)
    fun getUserEmail(): String?
    fun getUserResourceId(): String?
    val userNeedsLogin: Observable<Unit>
    fun saveTokenExpirationDate(date: Date)
    fun getUserFirstName(): String?
    fun getUserLastName(): String?
}