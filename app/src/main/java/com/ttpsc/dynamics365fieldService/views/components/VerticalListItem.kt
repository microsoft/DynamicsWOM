package com.ttpsc.dynamics365fieldService.views.components

import com.ttpsc.dynamics365fieldService.views.enums.ListItemStatus

class VerticalListItem(
    var id: String,
    var key: String,
    var title: String,
    var status: ListItemStatus = ListItemStatus.NONE
)