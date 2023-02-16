package com.jerry.shapes.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Environment
import androidx.core.content.FileProvider
import com.jerry.shapes.R
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.ui.boxes.data.LayerUi
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun Context?.exportCanvas(
    name: String,
    exportType: ExportType,
    rows: Int,
    columns: Int,
    imageSize: Int,
    layers: Collection<LayerUi>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>
): String? {
    val context = this ?: return null
    val bitmap = generateBitmap(rows, columns, imageSize, layers, selections)
    return bitmap.storeImage(context, exportType, name)
}

fun Bitmap.storeImage(
    context: Context,
    exportType: ExportType,
    name: String? = null
): String? {
    val pictureFile = getOutputMediaFile(context, exportType, name)
        ?: throw (Throwable(context.getString(R.string.error_export)))

    val fos = FileOutputStream(pictureFile)
    compress(Bitmap.CompressFormat.PNG, 90, fos)
    fos.close()

    return pictureFile.path
}

/** Create a File for saving an image or video  */
private fun getOutputMediaFile(
    context: Context,
    exportType: ExportType,
    name: String? = null
): File? {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    val mediaStorageDir = when (exportType) {
        ExportType.FILE -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Pixels")
        ExportType.SHARE -> File(context.cacheDir, "pixels")
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
    val mImageName =
        when (exportType == ExportType.FILE) {
            true -> "${if (name != null) name + "_" else ""}MI_$timeStamp.png"
            else -> (if (name != null) "$name.png" else "MI_$timeStamp.png")
        }
    mediaFile = File(mediaStorageDir.getPath() + File.separator.toString() + mImageName)
    return mediaFile
}

private fun Context.fileIntent(
    path: String,
    export: Boolean
): Intent {
    return Intent(
        when (export) {
            true -> Intent.ACTION_VIEW
            else -> Intent.ACTION_SEND
        }
    ).apply {
        val file = File(path)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val photoURI = FileProvider.getUriForFile(
            this@fileIntent,
            this@fileIntent.applicationContext.packageName + ".provider",
            file
        )
        putExtra(Intent.EXTRA_STREAM, photoURI)
        setDataAndType(photoURI, "image/png")
    }
}

fun Context.openImage(path: String) {
    startActivity(fileIntent(path, true))
}

fun Context.openShareSheet(path: String) {
    startActivity(Intent.createChooser(fileIntent(path, false), null))
}

val Context.thumbnailLocation get() = File(filesDir, "pixels")
