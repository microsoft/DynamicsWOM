package com.ttpsc.dynamics365fieldService.bll.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class BookingStatus(
    val name: String,
    var bookingStatusId: String? = null
) : Parcelable, Serializable