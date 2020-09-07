package com.ttpsc.dynamics365fieldService.bll.models

class PageableModel<ModelType> (
    val model: ModelType,
    val nextPageLink: String?
)