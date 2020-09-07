package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName
import com.ttpsc.dynamics365fieldService.bll.models.Note
import com.ttpsc.dynamics365fieldService.bll.models.WorkOrder
import java.text.SimpleDateFormat
import java.util.*

class DalNote(
    @SerializedName("@odata.etag") val odata_etag: String,
    @SerializedName("_owningbusinessunit_value") val _owningbusinessunit_value: String,
    @SerializedName("_objectid_value") val _objectid_value: String,
    @SerializedName("annotationid") val annotationid: String,
    @SerializedName("isdocument") val isdocument: Boolean,
    @SerializedName("filename") val filename: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("objecttypecode") val objecttypecode: String,
    @SerializedName("filesize") val filesize: Int,
    @SerializedName("_ownerid_value") val _ownerid_value: String,
    @SerializedName("modifiedon") val modifiedon: String,
    @SerializedName("_owninguser_value") val _owninguser_value: String,
    @SerializedName("_modifiedby_value") val _modifiedby_value: String,
    @SerializedName("versionnumber") val versionnumber: Int,
    @SerializedName("mimetype") val mimetype: String,
    @SerializedName("createdon") val createdon: String,
    @SerializedName("documentbody") val documentbody: String?,
    @SerializedName("_createdby_value") val _createdby_value: String,
    @SerializedName("stepid") val stepid: String,
    @SerializedName("_modifiedonbehalfby_value") val _modifiedonbehalfby_value: String,
    @SerializedName("prefix") val prefix: String,
    @SerializedName("overriddencreatedon") val overriddencreatedon: String,
    @SerializedName("langid") val langid: String,
    @SerializedName("_createdonbehalfby_value") val _createdonbehalfby_value: String,
    @SerializedName("notetext") val notetext: String,
    @SerializedName("_owningteam_value") val _owningteam_value: String,
    @SerializedName("importsequencenumber") val importsequencenumber: String,
    @SerializedName("owninguser") val owninguser: DalDynamicsUserInfo
){
    fun toNote(): Note {
        var dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        val note = Note(
            annotationid,
            filename,
            subject,
            isdocument,
            _objectid_value,
            createdon.let {
                if (it.isNotBlank())
                    dateFormat.parse(it)
                else
                    null
            }
        )

        if (documentbody != null) {
            note.documentBody = Base64.getDecoder().decode(documentbody).toTypedArray()
        }

        if (owninguser != null) {
            note.userInfo = owninguser.toUserInfo()
        }

        return note
    }
}