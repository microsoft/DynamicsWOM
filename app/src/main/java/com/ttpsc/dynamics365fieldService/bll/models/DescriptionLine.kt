package com.ttpsc.dynamics365fieldService.bll.models

import java.io.Serializable

data class DescriptionLine(
    val textRaw: String,
    val bullet: TextBullet?,
    val level: Int?,
    val lineId: Any?
) : Serializable {
    override fun toString() = textRaw
}

enum class TextBullet : Serializable {
    None,
    Black,
    Red,
    Orange,
    Yellow,
    LightBlue,
    Blue,
    Green,
    Violet,
    IconNote,
    IconCaution,
    IconReminder
}