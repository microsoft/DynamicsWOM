package com.ttpsc.dynamics365fieldService.bll.models

import java.io.Serializable

data class Category(
    val id: String,
    val key: String,
    val guid: String?,
    val title: String,
    val description: String,
    val labels: List<String>?,
    var procedures: List<Procedure>?,
    val timeSpentMillis: Int = 0,
    val timeEstimateMillis: Int = 0,
    val attachments: List<Attachment>?,
    val isSynchronizable: Boolean = false
) : Serializable