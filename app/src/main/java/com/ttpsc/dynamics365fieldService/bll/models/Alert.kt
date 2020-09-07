package com.ttpsc.dynamics365fieldService.bll.models

import java.io.Serializable

class Alert (
    val alertId: String?,
    val building: String?,
    val caseOwner: String?,
    val caseOwnerId: String?,
    val description: String?,
    val deviceName: String?,
    val equipment: String?,
    val faultSavings: Float?,
    val floor: String?,
    val fullAssetPath: String?,
    val stateCode: Int?,
    val ioTHubDeviceId: String?,
    val latitude: Float?,
    val likelihood: Float?,
    val longitude: Float?,
    val subject: String?,
    val unit: String?,
    val equipmentClass: String?,
    val workOrderId: String?
): Serializable
