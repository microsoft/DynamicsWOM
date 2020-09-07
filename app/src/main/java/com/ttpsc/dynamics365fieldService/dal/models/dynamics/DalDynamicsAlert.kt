package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName
import com.ttpsc.dynamics365fieldService.bll.models.Alert
import com.ttpsc.dynamics365fieldService.bll.models.Procedure

class DalDynamicsAlert(
    @SerializedName("@odata.etag") val etag: String,
    @SerializedName("_owningbusinessunit_value") val _owningbusinessunit_value: String,
    @SerializedName("processid") val processid: String,
    @SerializedName("statecode") val statecode: Int,
    @SerializedName("statuscode") val statuscode: Int,
    @SerializedName("_createdby_value") val _createdby_value: String,
    @SerializedName("_ownerid_value") val _ownerid_value: String,
    @SerializedName("msdyn_alerttype") val msdyn_alerttype: Int,
    @SerializedName("modifiedon") val modifiedon: String,
    @SerializedName("_owninguser_value") val _owninguser_value: String,
    @SerializedName("_modifiedby_value") val _modifiedby_value: String,
    @SerializedName("msdyn_iotalertid") val msdyn_iotalertid: String,
    @SerializedName("versionnumber") val versionnumber: Int,
    @SerializedName("msdyn_description") val msdyn_description: String,
    @SerializedName("createdon") val createdon: String,
    @SerializedName("_msdyn_workorder_value") val _msdyn_workorder_value: String,
    @SerializedName("_stageid_value") val _stageid_value: String,
    @SerializedName("msdyn_alerturl") val msdyn_alerturl: String,
    @SerializedName("traversedpath") val traversedpath: String,
    @SerializedName("msdyn_alertpriorityscore") val msdyn_alertpriorityscore: String,
    @SerializedName("msdyn_lastcommandsenttime") val msdyn_lastcommandsenttime: String,
    @SerializedName("_msdyn_parentalert_value") val _msdyn_parentalert_value: String,
    @SerializedName("importsequencenumber") val importsequencenumber: String,
    @SerializedName("msdyn_suggestedpriority") val msdyn_suggestedpriority: String,
    @SerializedName("timezoneruleversionnumber") val timezoneruleversionnumber: String,
    @SerializedName("msdyn_alerttoken") val msdyn_alerttoken: String,
    @SerializedName("msdyn_alerttime") val msdyn_alerttime: String,
    @SerializedName("_msdyn_suggestedincidenttype_value") val _msdyn_suggestedincidenttype_value: String,
    @SerializedName("_owningteam_value") val _owningteam_value: String,
    @SerializedName("_msdyn_lastcommandsent_value") val _msdyn_lastcommandsent_value: String,
    @SerializedName("msdyn_parentalerttoken") val msdyn_parentalerttoken: String,
    @SerializedName("utcconversiontimezonecode") val utcconversiontimezonecode: String,
    @SerializedName("_msdyn_customerasset_value") val _msdyn_customerasset_value: String,
    @SerializedName("msdyn_deviceid") val msdyn_deviceid: String,
    @SerializedName("_msdyn_device_value") val _msdyn_device_value: String,
    @SerializedName("_modifiedonbehalfby_value") val _modifiedonbehalfby_value: String,
    @SerializedName("_createdonbehalfby_value") val _createdonbehalfby_value: String,
    @SerializedName("overriddencreatedon") val overriddencreatedon: String,
    @SerializedName("_msdyn_case_value") val _msdyn_case_value: String,
    @SerializedName("msdyn_alertdata") val msdyn_alertdata: String
) {
    fun toAlert(): Alert {

        val alert = Alert(
            alertId = msdyn_iotalertid,
            building = null,
            caseOwner = null,
            caseOwnerId = null,
            description = msdyn_description,
            deviceName = null,
            equipment = null,
            faultSavings = null,
            floor = null,
            fullAssetPath = null,
            stateCode = null,
            ioTHubDeviceId = null,
            latitude = null,
            likelihood = null,
            longitude = null,
            subject = null,
            unit = null,
            equipmentClass = null,
            workOrderId = _msdyn_workorder_value
        )

        return alert
    }
}