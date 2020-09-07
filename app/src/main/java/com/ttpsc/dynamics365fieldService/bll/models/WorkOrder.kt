package com.ttpsc.dynamics365fieldService.bll.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class WorkOrder(
    var id: String?,
    var name: String? = null,
    var summary: String? = null,
    var description: String? = null,
    var address1: String? = null,
    var address2: String? = null,
    var address3: String? = null,
    var city: String? = null,
    var stateOrProvince: String? = null,
    var postalCode: String? = null,
    var country: String? = null,
    var serviceTerritoryId: String? = null,
    var iotAlertId: String? = null,
    var iotAlerts: MutableList<Alert> = mutableListOf(),
    var notes: List<Note> = listOf(),
    var videoAttachmentUrl: String? = null,
    var imageAttachmentUrl: String? = null,
    var documentAttachmentUrl: String? = null,
    var serviceTerritory: ServiceTerritory? = null,
    var iconicsLink: String? = null
) : Serializable