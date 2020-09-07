package com.ttpsc.dynamics365fieldService.views.components

import com.ttpsc.dynamics365fieldService.views.enums.ListItemStatus
import java.util.*

class GuidesListItem(
    var itemId: String,
    var title: String,
    var workOrder: String?,
    var endTime: Date?,
    var status: String,
    var listItemStatus: ListItemStatus
)