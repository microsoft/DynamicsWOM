package com.ttpsc.dynamics365fieldService.helpers.models

import com.ttpsc.dynamics365fieldService.R
import java.io.Serializable

enum class SortingType(val stringReosurceId: Int) {
    WORK_ORDER(R.string.work_order),
    STATUS(R.string.status),
    END_TIME(R.string.end_time)
}