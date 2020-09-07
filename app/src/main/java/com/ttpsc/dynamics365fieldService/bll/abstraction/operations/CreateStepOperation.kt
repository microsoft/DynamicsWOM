package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation
import com.ttpsc.dynamics365fieldService.bll.models.DescriptionLine
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import java.io.File

interface CreateStepOperation : Operation<Procedure> {
    var guideId: String
    var stepNumber: Int
    var descriptionLines: List<DescriptionLine>
    var title: String
    var images: List<File>?
    var video: File?
}