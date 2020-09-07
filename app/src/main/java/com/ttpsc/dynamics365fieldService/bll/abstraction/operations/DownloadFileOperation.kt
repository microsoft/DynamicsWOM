package com.ttpsc.dynamics365fieldService.bll.abstraction.operations

import com.ttpsc.dynamics365fieldService.bll.abstraction.Operation

interface DownloadFileOperation : Operation<ByteArray> {
    var fileUrl: String
}