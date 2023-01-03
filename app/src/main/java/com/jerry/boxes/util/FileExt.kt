package com.jerry.boxes.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

fun Bitmap.storeImage(
    context: Context
) {
    val pictureFile = getOutputMediaFile(context)
    if (pictureFile == null) {
        Timber.d(
            "Error creating media file, check storage permissions: "
        ) // e.getMessage());
        return
    }
    try {
        val fos = FileOutputStream(pictureFile)
        compress(Bitmap.CompressFormat.PNG, 90, fos)
        fos.close()
    } catch (e: FileNotFoundException) {
        Timber.d("File not found: ${e.message}")
    } catch (e: IOException) {
        Timber.d("Error accessing file: ${e.message}")
    } catch (e : java.lang.Exception) {
        Timber.d("Error accessing file: ${e.message}")
    }
}

/** Create a File for saving an image or video  */
private fun getOutputMediaFile(context: Context): File? {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    val mediaStorageDir = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
    )

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
    val mImageName = "MI_$timeStamp.png"
    mediaFile = File(mediaStorageDir.getPath() + File.separator.toString() + mImageName)
    return mediaFile
}