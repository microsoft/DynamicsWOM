package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName

data class DalChangeStatusErrorBody
    (

    @SerializedName("error") val error : DalChangeStatusError?
)