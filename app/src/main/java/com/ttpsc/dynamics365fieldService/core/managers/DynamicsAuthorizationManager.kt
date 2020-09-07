package com.ttpsc.dynamics365fieldService.core.managers

import android.content.SharedPreferences
import android.util.Log
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import com.google.gson.Gson
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.ttpsc.dynamics365fieldService.AppConfiguration
import com.ttpsc.dynamics365fieldService.bll.models.UserInfo
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class DynamicsAuthorizationManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val clientApplication: ISingleAccountPublicClientApplication
) :
    AuthorizationManager {

    private var _token: String? = null
    private var _expirationDate: Date? = null
    private var _userResourceId: String? = null
    private var _userFirstName: String? = null
    private var _userLastName: String? = null
    private var _environment: String? = null
    private var userNeedsLoginSubject: PublishSubject<Unit> = PublishSubject.create()

    override var userNeedsLogin: Observable<Unit> =
        userNeedsLoginSubject.throttleFirst(30, TimeUnit.SECONDS)

    init {
        _token = sharedPreferences.getString(AppConfiguration.StorageKeys.accessTokenKey, null)
        _userResourceId =
            sharedPreferences.getString(AppConfiguration.StorageKeys.userResourceIdKey, null)
        _userFirstName =
            sharedPreferences.getString(AppConfiguration.StorageKeys.userFirstNameKey, null)
        _userLastName =
            sharedPreferences.getString(AppConfiguration.StorageKeys.userLastNameKey, null)

        val serializedDate = sharedPreferences.getString(
            AppConfiguration.StorageKeys.accessTokenExpirationDateKey,
            null
        )
        if (serializedDate != null) {
            _expirationDate = Gson().fromJson(serializedDate, Date::class.java)
        }

        _environment = sharedPreferences.getString(
            AppConfiguration.StorageKeys.environmentKey,
            null
        )

        if (_token == null || _environment == null) {
            try {
                signOut()
            } catch (ex: Exception) {
                Log.w("AUTOMATIC SIGN OUT EXCEPTION", ex.message)
            }
        }
    }

    override fun setAuthorizationToken(authToken: String) {
        _token = authToken
        sharedPreferences
            .edit()
            .putString(AppConfiguration.StorageKeys.accessTokenKey, _token)
            .apply()
    }

    override fun getAuthorizationHeader(): String? {
        if (_token == null) {
            return null
        }

        return "Bearer $_token"
    }

    override fun saveTokenExpirationDate(date: Date) {
        _expirationDate = date
        sharedPreferences
            .edit()
            .putString(
                AppConfiguration.StorageKeys.accessTokenExpirationDateKey,
                Gson().toJson(date)
            )
            .apply()
    }

    override fun signOut() {
        _token = null
        _userResourceId = null
        AppConfiguration.endpoint = ""

        sharedPreferences
            .edit()
            .remove(AppConfiguration.StorageKeys.accessTokenKey)
            .remove(AppConfiguration.StorageKeys.environmentKey)
            .remove(AppConfiguration.StorageKeys.userResourceIdKey)
            .remove(AppConfiguration.StorageKeys.userFirstNameKey)
            .remove(AppConfiguration.StorageKeys.userLastNameKey)
            .apply()
        GlobalScope.async {
            clientApplication.signOut()
        }
    }

    override fun setUserData(userInfo: UserInfo?) {
        if (userInfo != null) {
            _userResourceId = userInfo.resourceId
            _userFirstName = userInfo.firstName
            _userLastName = userInfo.lastName

            sharedPreferences
                .edit()
                .putString(AppConfiguration.StorageKeys.userResourceIdKey, userInfo.resourceId)
                .apply()
            sharedPreferences
                .edit()
                .putString(AppConfiguration.StorageKeys.userFirstNameKey, userInfo.firstName)
                .apply()
            sharedPreferences
                .edit()
                .putString(AppConfiguration.StorageKeys.userLastNameKey, userInfo.lastName)
                .apply()
        }
    }

    override fun getUserEmail(): String? =
        sharedPreferences.getString(AppConfiguration.StorageKeys.userAccountKey, null)

    override fun getUserFirstName(): String? =
        sharedPreferences.getString(AppConfiguration.StorageKeys.userFirstNameKey, null)

    override fun getUserLastName(): String? =
        sharedPreferences.getString(AppConfiguration.StorageKeys.userLastNameKey, null)

    override fun getUserResourceId(): String? = _userResourceId

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .build()
        return chain.proceed(request)
    }

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        var requestAvailable: Request? = null
        try {
            if (_environment.isNullOrEmpty()) {
                _environment =
                    sharedPreferences.getString(AppConfiguration.StorageKeys.environmentKey, null)
            }

            val now = LocalDateTime.now()
            val expirationDate =
                _expirationDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()

            if (_token == null || (_expirationDate != null && now.isAfter(expirationDate))) {
                println("TRYING TO GET ANOTHER TOKEN")

                val authority =
                    clientApplication.getConfiguration().getDefaultAuthority().getAuthorityURL()
                        .toString()
                val result = clientApplication.acquireTokenSilent(
                    arrayOf("$_environment//user_impersonation"),
                    authority
                )
                saveTokenExpirationDate(result.expiresOn)
                if (result.accessToken != _token) {
                    setAuthorizationToken(result.accessToken)
                }
            }

            println("Access token: $_token")
            requestAvailable = response.request.newBuilder().addHeader(
                "Authorization",
                getAuthorizationHeader()!!
            ).build()
            return requestAvailable
        } catch (ex: Exception) {
            signOut()
            userNeedsLoginSubject.onNext(Unit)
        }
        return requestAvailable
    }
}
