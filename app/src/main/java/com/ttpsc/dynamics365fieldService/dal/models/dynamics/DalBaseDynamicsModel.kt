package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName

class DalBaseDynamicsModel <DataType>(
    @SerializedName("@odata.context")
    val dataContext: String,
    val value : List<DataType>,
    @SerializedName("@odata.count")
    val count: Int?,
    @SerializedName("@odata.nextLink")
    val nextPageLink: String?

)