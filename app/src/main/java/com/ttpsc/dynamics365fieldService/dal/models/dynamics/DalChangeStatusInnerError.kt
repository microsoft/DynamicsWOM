package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName

data class DalChangeStatusInnerError(
    @SerializedName("message") val message: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("stacktrace") val stacktrace: String?
) {
}