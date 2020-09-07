package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName

data class DalChangeStatusError(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("innererror") val innererror: DalChangeStatusInnerError?
) {
}