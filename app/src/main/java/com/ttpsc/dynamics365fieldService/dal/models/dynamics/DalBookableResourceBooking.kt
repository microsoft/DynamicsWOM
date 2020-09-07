package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName
import com.ttpsc.dynamics365fieldService.bll.models.BookableResourceBooking
import com.ttpsc.dynamics365fieldService.bll.models.WorkOrder
import java.text.SimpleDateFormat
import java.util.*

class DalBookableResourceBooking(
    @SerializedName("@odata.etag") val etag: String,
    @SerializedName("statecode") val statecode: Int,
    @SerializedName("_bookingstatus_value") val _bookingstatus_value: String,
    @SerializedName("msdyn_bookingmethod") val msdyn_bookingmethod: Int,
    @SerializedName("statuscode") val statuscode: Int,
    @SerializedName("createdon") val createdon: String,
    @SerializedName("importsequencenumber") val importsequencenumber: Int,
    @SerializedName("msdyn_worklocation") val msdyn_worklocation: Int,
    @SerializedName("_msdyn_bookingsetupmetadataid_value") val _msdyn_bookingsetupmetadataid_value: String,
    @SerializedName("msdyn_estimatedtravelduration") val msdyn_estimatedtravelduration: Int,
    @SerializedName("_ownerid_value") val _ownerid_value: String?,
    @SerializedName("_msdyn_resourcerequirement_value") val _msdyn_resourcerequirement_value: String,
    @SerializedName("name") val name: String,
    @SerializedName("msdyn_cascadecrewchanges") val msdyn_cascadecrewchanges: Boolean,
    @SerializedName("bookingtype") val bookingtype: Int,
    @SerializedName("versionnumber") val versionnumber: Int,
    @SerializedName("_msdyn_workorder_value") val _msdyn_workorder_value: String?,
    @SerializedName("msdyn_preventtimestampcreation") val msdyn_preventtimestampcreation: Boolean,
    @SerializedName("_transactioncurrencyid_value") val _transactioncurrencyid_value: String?,
    @SerializedName("exchangerate") val exchangerate: Int,
    @SerializedName("timezoneruleversionnumber") val timezoneruleversionnumber: Int,
    @SerializedName("duration") val duration: Int,
    @SerializedName("msdyn_effort") val msdyn_effort: Int,
    @SerializedName("endtime") val endtime: String,
    @SerializedName("bookableresourcebookingid") val bookableresourcebookingid: String?,
    @SerializedName("_modifiedby_value") val _modifiedby_value: String,
    @SerializedName("_resource_value") val _resource_value: String,
    @SerializedName("modifiedon") val modifiedon: String,
    @SerializedName("_owningbusinessunit_value") val _owningbusinessunit_value: String,
    @SerializedName("msdyn_allowoverlapping") val msdyn_allowoverlapping: Boolean,
    @SerializedName("msdyn_acceptcascadecrewchanges") val msdyn_acceptcascadecrewchanges: Boolean,
    @SerializedName("starttime") val starttime: String,
    @SerializedName("_createdby_value") val _createdby_value: String,
    @SerializedName("msdyn_estimatedarrivaltime") val msdyn_estimatedarrivaltime: String,
    @SerializedName("_owninguser_value") val _owninguser_value: String,
    @SerializedName("msdyn_traveltimerescheduling") val msdyn_traveltimerescheduling: Boolean,
    @SerializedName("msdyn_slottext") val msdyn_slottext: String,
    @SerializedName("msdyn_totalcost_base") val msdyn_totalcost_base: String,
    @SerializedName("msdyn_ursinternalflags") val msdyn_ursinternalflags: String,
    @SerializedName("msdyn_totalbreakduration") val msdyn_totalbreakduration: String,
    @SerializedName("msdyn_longitude") val msdyn_longitude: String,
    @SerializedName("msdyn_latitude") val msdyn_latitude: String,
    @SerializedName("msdyn_actualtravelduration") val msdyn_actualtravelduration: String,
    @SerializedName("msdyn_signature") val msdyn_signature: String,
    @SerializedName("_msdyn_crew_value") val _msdyn_crew_value: String,
    @SerializedName("_createdonbehalfby_value") val _createdonbehalfby_value: String,
    @SerializedName("_owningteam_value") val _owningteam_value: String,
    @SerializedName("msdyn_crewmembertype") val msdyn_crewmembertype: String,
    @SerializedName("utcconversiontimezonecode") val utcconversiontimezonecode: String,
    @SerializedName("_msdyn_resourcegroup_value") val _msdyn_resourcegroup_value: String,
    @SerializedName("processid") val processid: String?,
    @SerializedName("_msdyn_appointmentbookingid_value") val _msdyn_appointmentbookingid_value: String,
    @SerializedName("msdyn_internalflags") val msdyn_internalflags: String,
    @SerializedName("msdyn_totalcost") val msdyn_totalcost: String,
    @SerializedName("_msdyn_agreementbookingdate_value") val _msdyn_agreementbookingdate_value: String,
    @SerializedName("msdyn_totalbillableduration") val msdyn_totalbillableduration: String,
    @SerializedName("_msdyn_timegroupdetailselected_value") val _msdyn_timegroupdetailselected_value: String,
    @SerializedName("stageid") val stageid: String?,
    @SerializedName("msdyn_milestraveled") val msdyn_milestraveled: String,
    @SerializedName("overriddencreatedon") val overriddencreatedon: String,
    @SerializedName("_header_value") val _header_value: String,
    @SerializedName("traversedpath") val traversedpath: String,
    @SerializedName("msdyn_offlinetimestamp") val msdyn_offlinetimestamp: String,
    @SerializedName("msdyn_totaldurationinprogress") val msdyn_totaldurationinprogress: String,
    @SerializedName("msdyn_actualarrivaltime") val msdyn_actualarrivaltime: String,
    @SerializedName("_msdyn_requirementgroupid_value") val _msdyn_requirementgroupid_value: String?,
    @SerializedName("_modifiedonbehalfby_value") val _modifiedonbehalfby_value: String
) {
    fun toBookableResourceBooking(): BookableResourceBooking {

        val bookableResourceBooking = BookableResourceBooking(
            id = bookableresourcebookingid,
                    workOrder = _msdyn_workorder_value?.let { WorkOrder(it) },
                    workOrderId = _msdyn_workorder_value,
                    startTime = starttime.let {
                        if (it.isNotBlank())
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(it)
                        else
                            null
                    },
                    endTime = endtime.let {
                        if (it.isNotBlank())
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(it)
                        else
                            null
                    },
                    resource = _resource_value,
                    bookingStatus = _bookingstatus_value,
                    name = this.name,
                    stateCode = this.statecode,
                    statusCode = this.statuscode,
                    latitude = msdyn_latitude,
                    longitude = msdyn_longitude
        )

        return bookableResourceBooking
    }
}