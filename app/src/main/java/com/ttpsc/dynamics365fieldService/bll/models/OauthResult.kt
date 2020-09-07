package com.ttpsc.dynamics365fieldService.bll.models

import com.google.gson.annotations.SerializedName

data class OauthResult(
    @SerializedName("token_type")
    val tokenType:String,
    @SerializedName("scope")
    val scope: String,
    @SerializedName("expires_in")
    val expirationSeconds: Int,
    @SerializedName("access_token")
    val access_token: String,
    @SerializedName("refresh_token")
    val refresh_token: String,
    @SerializedName("id_token")
    val id_token: String
)