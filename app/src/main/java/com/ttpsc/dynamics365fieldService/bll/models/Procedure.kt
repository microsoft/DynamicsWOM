package com.ttpsc.dynamics365fieldService.bll.models

import java.io.Serializable
import java.util.*

data class Procedure(
    val id: String,
    val key: String,
    val guid: String?,
    var title: String?,
    val categoryName: String,
    val descriptionLines: List<DescriptionLine>,
    val labels: List<String>?,
    val template: Procedure?,
    val parent: Procedure?,
    var children: List<Procedure>?,
    val timeSpentMillis: Int = 0,
    val timeEstimateMillis: Int = 0,
    val attachments: MutableList<Attachment>?,
    val isTemplate: Boolean = true,
    val isSynchronizable: Boolean = false,
    val level: String,
    val alerts: MutableList<Alert> = mutableListOf(),
    val startTime: Date?,
    val endTime: Date?,
    val status: String?,
    val workOrderId: String?,
    var workOrder: WorkOrder?
) : Serializable {
    var customFields: MutableList<CustomField> = mutableListOf()
    override fun toString() = "$id   |   $title"
}