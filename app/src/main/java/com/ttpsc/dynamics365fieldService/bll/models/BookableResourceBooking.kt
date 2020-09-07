package com.ttpsc.dynamics365fieldService.bll.models

import java.util.*

class BookableResourceBooking(
    var id: String?,
    var workOrderId: String?,
    var workOrder: WorkOrder?,
    var startTime: Date?,
    var endTime: Date?,
    var resource: String?,
    var bookingStatus: String?,
    var name: String?,
    var stateCode: Int?,
    var statusCode: Int?,
    var latitude: String?,
    var longitude: String?
)
{
    var nextPageLink: String? = null

    fun toProcedure(): Procedure{
        val procedure = Procedure(
            id = this.id!!,
            key = this.id!!,
            guid = "",
            title = workOrder?.summary,
            categoryName = "",
            descriptionLines = mutableListOf(DescriptionLine(
                textRaw = workOrder?.iotAlerts?.firstOrNull()?.description ?: "",
                bullet =  TextBullet.None,
                level = 0,
                lineId = 0
            )),
            labels = listOf(),
            template = null,
            parent = null,
            children = null,
            timeSpentMillis = 0,
            timeEstimateMillis = 0,
            attachments = mutableListOf(),
            isTemplate = false,
            isSynchronizable = false,
            level = "",
            alerts = workOrder?.iotAlerts ?: mutableListOf(),
            startTime = this.startTime,
            endTime = this.endTime,
            status = bookingStatus,
            workOrderId = workOrder?.id,
            workOrder = workOrder
        )

        return procedure
    }
}