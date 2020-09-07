package com.ttpsc.dynamics365fieldService.dal.models.dynamics.customFields

import com.google.gson.annotations.SerializedName
import com.ttpsc.dynamics365fieldService.helpers.XMLHMTFormParser

data class FormWrapper(

    @SerializedName("@odata.etag") val odata_etag: String?,
    @SerializedName("formjson") val formjson: String?,
    @SerializedName("formxml") val formxml: String?,
    @SerializedName("objecttypecode") val objecttypecode: String?,
    @SerializedName("type") val type: Int?,
    @SerializedName("formid") val formid: String?
) {
    fun parseForm(): List<DalCustomField> {
        var customFields: List<DalCustomField> = listOf()
        if(formxml != null) {
            val xmlParser = XMLHMTFormParser()
            val document = xmlParser.readXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + formxml)
            customFields = xmlParser.getFieldNameAndDescriptionMape(document)
        }
        return customFields
    }
}