package com.jerry.shapes.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.cache.data.Project
import com.jerry.shapes.ui.boxes.data.LayerState
import kotlin.math.ceil
import kotlin.math.min

actual fun exportCanvas(
    project: Project,
    fileName: String,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
    layers: Collection<LayerState>,
    imageSize: Int,
    exportType: ExportType,
): String? {
    // We'll need a context here, but Repository doesn't have it.
    // For now, we'll return null or use a global context if available.
    return null 
}

fun generateAndroidBoxes(
    numX: Int,
    numY: Int,
    size: Float,
    xOffSet: Float,
    yOffSet: Float,
) = mutableMapOf<Point, RectF>().apply {
    for (y in 0 until numY) {
        for (x in 0 until numX) {
            val left = (size * x) + xOffSet
            val top = (size * y) + yOffSet
            put(
                Point(x, y),
                RectF(left, top, left + size, top + size),
            )
        }
    }
}

fun Canvas.drawShapesAndroid(
    layers: Collection<LayerState>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
    boxes: Map<Point, RectF>,
) {
    if (boxes.isEmpty()) return
    val layerIds = layers.filter { it.on }.sortedBy { it.index }.map { it.id }
    layerIds.forEach { layerId ->
        selections[layerId]?.forEach { (_, list) ->
            list.forEach { (point, color) ->
                val position = boxes[point]
                position?.let { pos ->
                    drawCustomShapeAndroid(pos, color)
                }
            }
        }
    }
}

fun Canvas.drawCustomShapeAndroid(
    pos: RectF,
    color: ColorAndShape,
) {
    drawRect(pos, Paint().apply { this.color = color.colorValue.toInt() })
}

fun generateBitmap(
    rows: Int,
    columns: Int,
    imageSize: Int,
    layers: Collection<LayerState>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
): Bitmap {
    val boxSize = ceil(min(imageSize / columns.toFloat(), imageSize / rows.toFloat())).toInt()
    val newBoxes = generateAndroidBoxes(columns, rows, boxSize.toFloat(), 0F, 0F)
    val bitmap = createBitmap(columns * boxSize, rows * boxSize)
    return bitmap.applyCanvas {
        drawShapesAndroid(layers, selections, newBoxes)
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
