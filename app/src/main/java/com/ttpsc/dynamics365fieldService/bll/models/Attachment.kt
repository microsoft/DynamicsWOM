package com.ttpsc.dynamics365fieldService.bll.models

import java.io.Serializable

data class Attachment(
    val fileUrl: String?,
    val filePath: String?,
    val customFileName: String?
) : Serializable {
    var thumbnailUrl: String? = ""
    var mediumUrl: String? = ""
    var attachmentType: AttachmentType = AttachmentType.NONE

    @Suppress("unused")
    val fileName: String?
        get() {
            val fileUri = filePath ?: fileUrl
            return fileUri?.split('/')?.last() ?: customFileName
        }
}

enum class AttachmentType : Serializable {
    NONE, IMAGE, VIDEO
}