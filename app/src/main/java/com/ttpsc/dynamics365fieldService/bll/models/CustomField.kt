package com.ttpsc.dynamics365fieldService.bll.models

import java.io.Serializable

class CustomField(
    var fieldName: String,
    var fieldDescription: String,
    var fieldValue: String
) : Serializable {
}