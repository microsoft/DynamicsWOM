package com.ttpsc.dynamics365fieldService.dal.models.dynamics

import com.google.gson.annotations.SerializedName
import com.ttpsc.dynamics365fieldService.bll.models.WorkOrder

class DalDynamicsWorkOrder(
    @SerializedName("@odata.etag") val etag: String,
    @SerializedName("statecode") val statecode: Int,
    @SerializedName("msdyn_longitude") val msdyn_longitude: Double,
    @SerializedName("statuscode") val statuscode: Int,
    @SerializedName("msdyn_estimatesubtotalamount") val msdyn_estimatesubtotalamount: Int,
    @SerializedName("msdyn_postalcode") val msdyn_postalcode: String,
    @SerializedName("_msdyn_workordertype_value") val _msdyn_workordertype_value: String,
    @SerializedName("createdon") val createdon: String,
    @SerializedName("msdyn_latitude") val msdyn_latitude: Double,
    @SerializedName("msdyn_primaryincidentdescription") val msdyn_primaryincidentdescription: String?,
    @SerializedName("msdyn_totalamount") val msdyn_totalamount: Int,
    @SerializedName("msdyn_worklocation") val msdyn_worklocation: Int,
    @SerializedName("msdyn_workorderid") val msdyn_workorderid: String,
    @SerializedName("_msdyn_primaryincidenttype_value") val _msdyn_primaryincidenttype_value: String,
    @SerializedName("msdyn_taxable") val msdyn_taxable: Boolean,
    @SerializedName("_ownerid_value") val _ownerid_value: String,
    @SerializedName("msdyn_primaryincidentestimatedduration") val msdyn_primaryincidentestimatedduration: Int,
    @SerializedName("_msdyn_pricelist_value") val _msdyn_pricelist_value: String,
    @SerializedName("modifiedon") val modifiedon: String,
    @SerializedName("msdyn_stateorprovince") val msdyn_stateorprovince: String,
    @SerializedName("msdyn_address1") val msdyn_address1: String,
    @SerializedName("_msdyn_taxcode_value") val _msdyn_taxcode_value: String,
    @SerializedName("_msdyn_billingaccount_value") val _msdyn_billingaccount_value: String,
    @SerializedName("versionnumber") val versionnumber: Int,
    @SerializedName("_transactioncurrencyid_value") val _transactioncurrencyid_value: String,
    @SerializedName("msdyn_name") val msdyn_name: String,
    @SerializedName("exchangerate") val exchangerate: Int,
    @SerializedName("msdyn_isfollowup") val msdyn_isfollowup: Boolean,
    @SerializedName("_msdyn_serviceaccount_value") val _msdyn_serviceaccount_value: String,
    @SerializedName("_modifiedby_value") val _modifiedby_value: String,
    @SerializedName("msdyn_followuprequired") val msdyn_followuprequired: Boolean,
    @SerializedName("_owningbusinessunit_value") val _owningbusinessunit_value: String,
    @SerializedName("msdyn_totalsalestax") val msdyn_totalsalestax: Int,
    @SerializedName("msdyn_systemstatus") val msdyn_systemstatus: Int,
    @SerializedName("msdyn_estimatesubtotalamount_base") val msdyn_estimatesubtotalamount_base: Int,
    @SerializedName("msdyn_city") val msdyn_city: String,
    @SerializedName("msdyn_subtotalamount") val msdyn_subtotalamount: Int,
    @SerializedName("_createdby_value") val _createdby_value: String,
    @SerializedName("msdyn_totalsalestax_base") val msdyn_totalsalestax_base: Int,
    @SerializedName("msdyn_country") val msdyn_country: String,
    @SerializedName("processid") val processid: String,
    @SerializedName("_owninguser_value") val _owninguser_value: String,
    @SerializedName("msdyn_subtotalamount_base") val msdyn_subtotalamount_base: Int,
    @SerializedName("msdyn_totalamount_base") val msdyn_totalamount_base: Int,
    @SerializedName("msdyn_ismobile") val msdyn_ismobile: Boolean,
    @SerializedName("msdyn_internalflags") val msdyn_internalflags: String,
    @SerializedName("_msdyn_servicerequest_value") val _msdyn_servicerequest_value: String,
    @SerializedName("msdyn_mapcontrol") val msdyn_mapcontrol: String,
    @SerializedName("msdyn_timewindowend") val msdyn_timewindowend: String,
    @SerializedName("msdyn_addressname") val msdyn_addressname: String,
    @SerializedName("msdyn_timewindowstart") val msdyn_timewindowstart: String,
    @SerializedName("_modifiedonbehalfby_value") val _modifiedonbehalfby_value: String,
    @SerializedName("msdyn_bookingsummary") val msdyn_bookingsummary: String,
    @SerializedName("_msdyn_priority_value") val _msdyn_priority_value: String,
    @SerializedName("msdyn_childindex") val msdyn_childindex: String,
    @SerializedName("msdyn_instructions") val msdyn_instructions: String,
    @SerializedName("msdyn_address3") val msdyn_address3: String,
    @SerializedName("_msdyn_timegroupdetailselected_value") val _msdyn_timegroupdetailselected_value: String,
    @SerializedName("msdyn_datewindowstart") val msdyn_datewindowstart: String,
    @SerializedName("_msdyn_serviceterritory_value") val _msdyn_serviceterritory_value: String,
    @SerializedName("_msdyn_preferredresource_value") val _msdyn_preferredresource_value: String,
    @SerializedName("timezoneruleversionnumber") val timezoneruleversionnumber: String,
    @SerializedName("overriddencreatedon") val overriddencreatedon: String,
    @SerializedName("_msdyn_workorderresolutionkpiid_value") val _msdyn_workorderresolutionkpiid_value: String,
    @SerializedName("_msdyn_supportcontact_value") val _msdyn_supportcontact_value: String,
    @SerializedName("msdyn_address2") val msdyn_address2: String,
    @SerializedName("msdyn_datewindowend") val msdyn_datewindowend: String,
    @SerializedName("_msdyn_substatus_value") val _msdyn_substatus_value: String,
    @SerializedName("importsequencenumber") val importsequencenumber: String,
    @SerializedName("msdyn_timefrompromised") val msdyn_timefrompromised: String,
    @SerializedName("_msdyn_parentworkorder_value") val _msdyn_parentworkorder_value: String,
    @SerializedName("_owningteam_value") val _owningteam_value: String,
    @SerializedName("_msdyn_closedby_value") val _msdyn_closedby_value: String,
    @SerializedName("_msdyn_workhourtemplate_value") val _msdyn_workhourtemplate_value: String,
    @SerializedName("_stageid_value") val _stageid_value: String,
    @SerializedName("msdyn_timetopromised") val msdyn_timetopromised: String,
    @SerializedName("_msdyn_reportedbycontact_value") val _msdyn_reportedbycontact_value: String,
    @SerializedName("_msdyn_customerasset_value") val _msdyn_customerasset_value: String,
    @SerializedName("msdyn_timeclosed") val msdyn_timeclosed: String,
    @SerializedName("_createdonbehalfby_value") val _createdonbehalfby_value: String,
    @SerializedName("_msdyn_agreement_value") val _msdyn_agreement_value: String,
    @SerializedName("utcconversiontimezonecode") val utcconversiontimezonecode: String,
    @SerializedName("msdyn_autonumbering") val msdyn_autonumbering: String,
    @SerializedName("_msdyn_iotalert_value") val _msdyn_iotalert_value: String,
    @SerializedName("traversedpath") val traversedpath: String,
    @SerializedName("msdyn_workordersummary") val msdyn_workordersummary: String,
    @SerializedName("_msdyn_workorderarrivaltimekpiid_value") val _msdyn_workorderarrivaltimekpiid_value: String,
    @SerializedName("_msdyn_timegroup_value") val _msdyn_timegroup_value: String,
    @SerializedName("_msdyn_opportunityid_value") val _msdyn_opportunityid_value: String,
    @SerializedName("msdyn_followupnote") val msdyn_followupnote: String,
    @SerializedName("hmt_video") val hmt_attachment_video: String?,
    @SerializedName("hmt_image") val hmt_attachment_image: String?,
    @SerializedName("hmt_document") val hmt_attachment_document: String?,
    @SerializedName("msdyn_serviceterritory") val msdyn_serviceterritory: DalServiceterritory?,
    @SerializedName("hmt_iconics") val hmt_iconics: String?
) {

    fun toWorkOrder(): WorkOrder {
        return WorkOrder(
            msdyn_workorderid,
            msdyn_name,
            msdyn_workordersummary,
            msdyn_primaryincidentdescription,
            msdyn_address1,
            msdyn_address2,
            msdyn_address3,
            msdyn_city,
            msdyn_stateorprovince,
            msdyn_postalcode,
            msdyn_country,
            _msdyn_serviceterritory_value,
            _msdyn_iotalert_value,
            mutableListOf(),
            listOf(),
            hmt_attachment_video,
            hmt_attachment_image,
            hmt_attachment_document,
            msdyn_serviceterritory?.toServiceTerritory(),
            hmt_iconics
        )
    }
}