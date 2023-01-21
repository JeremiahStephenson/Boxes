package com.jerry.boxes.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

fun Bitmap.storeImage(
    context: Context,
    export: Boolean,
    name: String? = null
): String? {
    val pictureFile = getOutputMediaFile(context, export, name)
    if (pictureFile == null) {
        Timber.d(
            "Error creating media file, check storage permissions: "
        ) // e.getMessage());
        return null
    }
    try {
        val fos = FileOutputStream(pictureFile)
        compress(Bitmap.CompressFormat.PNG, 90, fos)
        fos.close()
        return pictureFile.path
    } catch (e: FileNotFoundException) {
        Timber.d("File not found: ${e.message}")
        return null
    } catch (e: IOException) {
        Timber.d("Error accessing file: ${e.message}")
        return null
    } catch (e : java.lang.Exception) {
        Timber.d("Error accessing file: ${e.message}")
        return null
    }
}

/** Create a File for saving an image or video  */
private fun getOutputMediaFile(
    context: Context,
    export: Boolean,
    name: String? = null
): File? {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    val mediaStorageDir = when (export) {
        true -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Pixels")
        else -> File(context.filesDir, "pixels")
    }

    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.mkdirs()) {
            return null
        }
    }
    // Create a media file name
    val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(Date())
    val mediaFile: File
    val mImageName = if (export) "${if (name != null) name + "_" else ""}MI_$timeStamp.png" else (if (name != null) "$name.png" else "MI_$timeStamp.png")
    mediaFile = File(mediaStorageDir.getPath() + File.separator.toString() + mImageName)
    return mediaFile
}

fun Context.openImage(path: String) {
    startActivity(Intent(Intent.ACTION_VIEW).apply {
        val file = File(path)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val photoURI = FileProvider.getUriForFile(
            this@openImage,
            this@openImage.applicationContext.packageName + ".provider",
            file
        )
        setDataAndType(photoURI, "image/*")
    })
}

val Context.thumbnailLocation get() = File(filesDir, "pixels")