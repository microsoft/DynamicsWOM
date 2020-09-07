package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName
import com.ttpsc.dynamics365fieldService.bll.models.ServiceTerritory

data class DalServiceterritory(

    @SerializedName("@odata.etag") val odata_etag: String?,
    @SerializedName("entityimageid") val entityimageid: String?,
    @SerializedName("timezoneruleversionnumber") val timezoneruleversionnumber: String?,
    @SerializedName("modifiedon") val modifiedon: String?,
    @SerializedName("importsequencenumber") val importsequencenumber: String?,
    @SerializedName("_parentterritoryid_value") val _parentterritoryid_value: String?,
    @SerializedName("_managerid_value") val _managerid_value: String?,
    @SerializedName("_createdonbehalfby_value") val _createdonbehalfby_value: String?,
    @SerializedName("_createdby_value") val _createdby_value: String?,
    @SerializedName("_modifiedonbehalfby_value") val _modifiedonbehalfby_value: String?,
    @SerializedName("entityimage_timestamp") val entityimage_timestamp: String?,
    @SerializedName("exchangerate") val exchangerate: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("utcconversiontimezonecode") val utcconversiontimezonecode: String?,
    @SerializedName("versionnumber") val versionnumber: Int?,
    @SerializedName("_modifiedby_value") val _modifiedby_value: String?,
    @SerializedName("_transactioncurrencyid_value") val _transactioncurrencyid_value: String?,
    @SerializedName("entityimage") val entityimage: String?,
    @SerializedName("entityimage_url") val entityimage_url: String?,
    @SerializedName("createdon") val createdon: String?,
    @SerializedName("overriddencreatedon") val overriddencreatedon: String?,
    @SerializedName("territoryid") val territoryid: String?,
    @SerializedName("_organizationid_value") val _organizationid_value: String?,
    @SerializedName("name") val name: String?
){
    fun toServiceTerritory(): ServiceTerritory{
        return ServiceTerritory(
            odata_etag,
            entityimageid,
            timezoneruleversionnumber,
            modifiedon,
            importsequencenumber,
            _parentterritoryid_value,
            _managerid_value,
            _createdonbehalfby_value,
            _createdby_value,
            _modifiedonbehalfby_value,
            entityimage_timestamp,
            exchangerate,
            description,
            utcconversiontimezonecode,
            versionnumber,
            _modifiedby_value,
            _transactioncurrencyid_value,
            entityimage,
            entityimage_url,
            createdon,
            overriddencreatedon,
            territoryid,
            _organizationid_value,
            name
        )
    }
}