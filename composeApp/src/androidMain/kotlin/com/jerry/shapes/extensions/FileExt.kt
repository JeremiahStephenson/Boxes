package com.jerry.shapes.extensions

import android.content.Context
import android.graphics.Bitmap
import com.jerry.shapes.util.Point
import android.os.Environment
import com.jerry.shapes.R
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.ui.boxes.data.LayerState
import com.jerry.shapes.util.ExportType
import com.jerry.shapes.util.generateBitmap
import kotlinx.io.asOutputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

fun Context?.exportCanvas(
    name: String,
    exportType: ExportType,
    rows: Int,
    columns: Int,
    imageSize: Int,
    layers: Collection<LayerState>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
): String? {
    val context = this ?: return null
    val bitmap = generateBitmap(rows, columns, imageSize, layers, selections)
    return bitmap.storeImage(context, exportType, name)
}

fun Bitmap.storeImage(
    context: Context,
    exportType: ExportType,
    name: String? = null,
): String {
    val pictureFile =
        getOutputMediaFile(context, exportType, name)
            ?: throw (Throwable(context.getString(R.string.error_export)))

    SystemFileSystem.sink(pictureFile).use { sink ->
        val outputStream = sink.buffered().asOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
    }

    return pictureFile.toString()
}

/** Create a File for saving an image or video  */
private fun getOutputMediaFile(
    context: Context,
    exportType: ExportType,
    name: String? = null,
): Path? {
    val mediaStorageDir =
        when (exportType) {
            ExportType.FILE -> Path(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Pixels").path)
            ExportType.SHARE -> Path(context.cacheDir.path, "pixels")
            else -> Path(context.filesDir.path, "pixels")
        }

    // Create the storage directory if it does not exist
    if (!SystemFileSystem.exists(mediaStorageDir)) {
        runCatching {
            SystemFileSystem.createDirectories(mediaStorageDir)
        }.onFailure {
            return null
        }
    }
    // Create a media file name
    val timeStamp = currentFileTimeStamp
    val mediaFile: Path
    val mImageName =
        when (exportType == ExportType.FILE) {
            true -> "${if (name != null) name + "_" else ""}MI_$timeStamp.png"
            else -> (if (name != null) "$name.png" else "MI_$timeStamp.png")
        }
    mediaFile = Path(mediaStorageDir.toString(), mImageName)
    return mediaFile
}
