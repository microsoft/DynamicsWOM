package com.ttpsc.dynamics365fieldService.bll.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QrCodeScan (
    @SerializedName("ResourceId")
    var environmentUrl: String,
    @SerializedName("UserId")
    val login: String
): Serializable {}