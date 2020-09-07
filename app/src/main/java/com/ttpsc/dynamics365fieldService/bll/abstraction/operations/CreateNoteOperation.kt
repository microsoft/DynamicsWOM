package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation

interface CreateNoteOperation: Operation<Pair<Boolean, String?>> {
    var filename: String?
    var subject: String?
    var isDocument: Boolean
    var objecttypecode: String
    var workOrderId: String?
    var documentBody: Array<Byte>?
}
