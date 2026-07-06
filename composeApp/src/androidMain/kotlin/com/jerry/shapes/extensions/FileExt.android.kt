package com.jerry.shapes.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Environment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.jerry.shapes.R
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.cache.data.Project
import com.jerry.shapes.ui.boxes.data.LayerState
import com.jerry.shapes.ui.shapes.ShapersInterface
import com.jerry.shapes.util.AndroidPlatformCanvasExport
import com.jerry.shapes.util.AndroidPlatformContext
import com.jerry.shapes.util.CanvasExport
import com.jerry.shapes.util.ExportType
import com.jerry.shapes.util.PlatformContext
import com.jerry.shapes.util.Point
import com.jerry.shapes.util.generateBoxes
import kotlinx.io.asOutputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator
import kotlin.math.ceil
import kotlin.math.min

actual fun PlatformContext?.exportCanvas(
    name: String,
    exportType: ExportType,
    rows: Int,
    columns: Int,
    imageSize: Int,
    layers: Collection<LayerState>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
): String? {
    val context = (this as? AndroidPlatformContext)?.context ?: return null
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

fun generateBitmap(
    rows: Int,
    columns: Int,
    imageSize: Int,
    layers: Collection<LayerState>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
): Bitmap {
    val boxSize = ceil(min(imageSize / columns.toFloat(), imageSize / rows.toFloat())).toInt()
    val newBoxes = generateBoxes(columns, rows, boxSize.toFloat(), 0F, 0F)
    val bitmap = createBitmap(columns * boxSize, rows * boxSize)
    return bitmap.applyCanvas {
        AndroidPlatformCanvasExport(this).drawShapes(layers, selections, newBoxes)
    }
}

fun generateBitmap(
    rows: Int,
    columns: Int,
    imageSize: Int,
    layerId: Long,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
): Bitmap =
    generateBitmap(
        rows,
        columns,
        imageSize,
        listOf(
            LayerState(
                layerId,
                0L,
                1,
                "",
                on = true,
                selected = true,
                visibilityEnabled = true,
                showControls = true,
            )
        ),
        selections,
    )

fun CanvasExport.drawShapes(
    layers: Collection<LayerState>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
    boxes: Map<Point, Rect>,
) {
    if (boxes.isEmpty()) return
    val layerIds = layers.filter { it.on }.sortedBy { it.index }.map { it.id }
    layerIds.forEach { layerId ->
        selections[layerId]?.forEach { (_, list) ->
            list.forEach { (point, color) ->
                val position = boxes[point]
                position?.let { pos ->
                    drawCustomShape(pos, color)
                }
            }
        }
    }
}

fun CanvasExport.drawCustomShape(
    pos: Rect,
    color: ColorAndShape,
) {
    (color.shape as ShapersInterface).draw(this, pos, color)
}

actual fun Project.thumbnailUrl(context: coil3.PlatformContext): String {
    return context.thumbnailLocation.path + SystemPathSeparator + "${id}.png"
}