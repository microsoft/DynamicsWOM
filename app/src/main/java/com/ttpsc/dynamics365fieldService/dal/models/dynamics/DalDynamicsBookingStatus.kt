package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName
import com.ttpsc.dynamics365fieldService.bll.models.BookingStatus
import com.ttpsc.dynamics365fieldService.bll.models.Procedure

class DalDynamicsBookingStatus(

    @SerializedName("@odata.etag") val tag: String,
    @SerializedName("msdyn_statuscolor") val msdyn_statuscolor: String,
    @SerializedName("status") val status: Int,
    @SerializedName("_owningbusinessunit_value") val _owningbusinessunit_value: String,
    @SerializedName("_modifiedonbehalfby_value") val _modifiedonbehalfby_value: String,
    @SerializedName("_createdonbehalfby_value") val _createdonbehalfby_value: String,
    @SerializedName("statecode") val statecode: Int,
    @SerializedName("statuscode") val statuscode: Int,
    @SerializedName("_createdby_value") val _createdby_value: String,
    @SerializedName("_ownerid_value") val _ownerid_value: String,
    @SerializedName("modifiedon") val modifiedon: String,
    @SerializedName("msdyn_imageurl") val msdyn_imageurl: String,
    @SerializedName("_owninguser_value") val _owninguser_value: String,
    @SerializedName("_modifiedby_value") val _modifiedby_value: String,
    @SerializedName("versionnumber") val versionnumber: Int,
    @SerializedName("msdyn_fieldservicestatus") val msdyn_fieldservicestatus: Int,
    @SerializedName("createdon") val createdon: String,
    @SerializedName("name") val name: String,
    @SerializedName("bookingstatusid") val bookingstatusid: String,
    @SerializedName("timezoneruleversionnumber") val timezoneruleversionnumber: String,
    @SerializedName("utcconversiontimezonecode") val utcconversiontimezonecode: String,
    @SerializedName("overriddencreatedon") val overriddencreatedon: String,
    @SerializedName("msdyn_internalflags") val msdyn_internalflags: String,
    @SerializedName("_transactioncurrencyid_value") val _transactioncurrencyid_value: String,
    @SerializedName("importsequencenumber") val importsequencenumber: String,
    @SerializedName("description") val description: String,
    @SerializedName("_owningteam_value") val _owningteam_value: String,
    @SerializedName("exchangerate") val exchangerate: String
) {
    fun toBookingStatus(): BookingStatus {
        return BookingStatus(name, bookingstatusid)
    }
}