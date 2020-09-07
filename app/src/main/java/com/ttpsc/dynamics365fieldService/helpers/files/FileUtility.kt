package com.ttpsc.dynamics365fieldService.helpers.files

import android.R.attr.path
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.net.URI


class FileUtility {
    companion object {
        fun openWithDefaultApp(context: Context, file: File) {
            if (file.exists() == false) {
                throw NoSuchFileException(file)
            }
            val intent = Intent(Intent.ACTION_VIEW)
            val data = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(data, type)
            context.startActivity(intent)
        }

    }
}