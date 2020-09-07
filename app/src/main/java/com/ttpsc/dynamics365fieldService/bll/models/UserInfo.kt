package com.ttpsc.dynamics365fieldService.bll.models

import java.io.Serializable

class UserInfo(
    val resourceId: String,
    val email: String,
    val fullName: String?,
    val firstName: String?,
    val lastName: String?
) : Serializable {
    override fun toString() = "$resourceId   |   $email"
}