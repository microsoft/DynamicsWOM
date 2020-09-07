package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.bll.models.UserInfo

class DalDynamicsUserInfo(

    @SerializedName("@odata.etag") val etag: String,
    @SerializedName("systemuserid") val systemuserid: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("windowsliveid") val windowsliveid: String,
    @SerializedName("domainname") val domainname: String,
    @SerializedName("ownerid") val ownerid: String,
    @SerializedName("firstname") val firstname : String,
    @SerializedName("lastname") val lastname : String
){

    fun toUserInfo(): UserInfo {

        val userInfo = UserInfo(
            resourceId = systemuserid,
            email = domainname,
            fullName = fullname,
            firstName = firstname,
            lastName = lastname
        )

        return userInfo
    }
}