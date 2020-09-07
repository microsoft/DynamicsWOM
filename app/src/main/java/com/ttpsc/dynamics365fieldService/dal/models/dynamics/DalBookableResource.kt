package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName
import com.ttpsc.dynamics365fieldService.bll.models.BookableResource
import com.ttpsc.dynamics365fieldService.bll.models.Procedure

class DalBookableResource(

    @SerializedName("@odata.etag") val etag: String,
    @SerializedName("_userid_value") val _userid_value: String,
    @SerializedName("bookableresourceid") val bookableresourceid: String
){
    fun toBookableResource(): BookableResource {

        val bookableResource = BookableResource(
            userId = _userid_value,
            bookableResourceId = bookableresourceid
        )

        return bookableResource
    }
}