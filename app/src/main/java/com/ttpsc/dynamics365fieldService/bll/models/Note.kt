package com.ttpsc.dynamics365fieldService.bll.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
class Note (
    val id: String?,
    val fileName: String?,
    val subject: String?,
    val isDocument:Boolean,
    val associatedTo: String?,
    val date: Date?
): Parcelable, Serializable {
    lateinit var documentBody: Array<Byte>
    var userInfo: UserInfo? = null
}