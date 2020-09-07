package com.ttpsc.dynamics365fieldService.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.DownloadFileOperation
import com.ttpsc.dynamics365fieldService.bll.models.AttachmentType
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.helpers.files.FileUtility
import io.reactivex.rxjava3.core.Observable
import java.io.File
import javax.inject.Inject

class GalleryViewModel @Inject constructor() : ViewModel() {
    val currentStep by lazy { MutableLiveData<Procedure>() }
    val currentAttachmentIndex by lazy { MutableLiveData<Int>() }

    @Inject
    lateinit var downloadFile: DownloadFileOperation

    fun setNextAttachment() {
        val attachmentsCount = currentStep.value?.attachments?.count()

        if (attachmentsCount != null && currentAttachmentIndex.value!! < attachmentsCount - 1) {
            currentAttachmentIndex.value = currentAttachmentIndex.value!! + 1
        }
    }

    fun setPreviousAttachment() {
        if (currentAttachmentIndex.value!! > 0) {
            currentAttachmentIndex.value = currentAttachmentIndex.value!! - 1
        }
    }

    fun openAttachment(context: Context) {
        downloadAndOpenFile(context).subscribe({},
            {
                Log.e("DOWNLOAD ATTACHMENT", it.toString())
            })
    }

    private fun downloadAndOpenFile(context: Context): Observable<Unit> {
        val currentAttachment =
            currentStep.value?.attachments?.elementAt(currentAttachmentIndex.value ?: 0)

        if (currentAttachment?.fileUrl.isNullOrEmpty() == false) {
            downloadFile.fileUrl = currentAttachment?.fileUrl!!
            return downloadFile.execute()
                .map {
                    val fileName = when (currentAttachment.attachmentType) {
                        AttachmentType.IMAGE -> "temporaryFile.jpg"
                        AttachmentType.VIDEO -> "temporaryFile.mp4"
                        else -> ""
                    }

                    val file = File(context.getExternalFilesDir(null).toString(), fileName)
                    file.writeBytes(it)
                    FileUtility.openWithDefaultApp(context, file)
                }
        }
        return Observable.just(Unit)
    }
}