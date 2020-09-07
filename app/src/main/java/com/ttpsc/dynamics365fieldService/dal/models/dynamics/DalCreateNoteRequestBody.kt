package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName

class DalCreateNoteRequestBody {
    @SerializedName("objectid_msdyn_workorder@odata.bind") var workOrderBindPath: String? = null
    @SerializedName("filename") var filename: String? = null
    @SerializedName("subject") var subject: String = ""
    @SerializedName("isdocument") var isdocument: Boolean = false
    @SerializedName("objecttypecode") var objecttypecode: String = ""
    @SerializedName("documentbody") var base64documentbody: String? = null
}