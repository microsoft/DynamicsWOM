package com.ttpsc.dynamics365fieldService.dal.models.dynamics.customFields

import com.ttpsc.dynamics365fieldService.bll.models.CustomField

class DalCustomField(
    val fieldName: String,
    val fieldDescription: String
) {
    fun toCustomField(): CustomField {
        return  CustomField(
            fieldName = this.fieldName,
            fieldDescription = this.fieldDescription,
            fieldValue = ""
        )
    }
}