package com.jerry.shapes.extensions

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

// TODO place in android kmp module
private fun Context.fileIntent(
    path: String,
    export: Boolean,
): Intent =
    Intent(
        when (export) {
            true -> Intent.ACTION_VIEW
            else -> Intent.ACTION_SEND
        },
    ).apply {
        val file = File(path)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val photoURI =
            FileProvider.getUriForFile(
                this@fileIntent,
                this@fileIntent.applicationContext.packageName + ".provider",
                file,
            )
        putExtra(Intent.EXTRA_STREAM, photoURI)
        setDataAndType(photoURI, "image/png")
    }

fun Context.openImage(path: String) {
    startActivity(fileIntent(path, true))
}

fun Context.openShareSheet(path: String) {
    startActivity(Intent.createChooser(fileIntent(path, false), null))
}

val Context.thumbnailLocation get() = File(filesDir, "pixels")
