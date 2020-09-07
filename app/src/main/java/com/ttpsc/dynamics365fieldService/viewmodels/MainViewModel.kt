package com.ttpsc.dynamics365fieldService.viewmodels

import androidx.lifecycle.ViewModel
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.GetUserInfoOperation
import com.ttpsc.dynamics365fieldService.bll.models.UserInfo
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.fragment_main_menu.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getUserInfo: GetUserInfoOperation
) : ViewModel() {
    @Inject
    lateinit var authorizationManager: AuthorizationManager

    fun setUserInfo(): Observable<UserInfo?> {
        return getUserInfo.apply {
            email = "'" + (authorizationManager.getUserEmail() ?: "") + "'"
        }.execute()
            .map { userInfo ->
                authorizationManager.setUserData(userInfo.firstOrNull())
                userInfo.firstOrNull()
            }
    }

    fun getUserFullName(): String {
        if (authorizationManager.getUserFirstName()
                .isNullOrEmpty() == false
            && authorizationManager.getUserLastName()
                .isNullOrEmpty() == false
        ) {
            return (authorizationManager.getUserFirstName()?: "") + " " + (authorizationManager.getUserLastName() ?: "")
        } else {
            return ""
        }
    }

    fun hasUserInfo(): Boolean {
        return authorizationManager.getUserResourceId().isNullOrBlank() == false
    }
}