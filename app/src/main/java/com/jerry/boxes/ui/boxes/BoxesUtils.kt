package com.jerry.boxes.ui.boxes

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.RectF
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.boxes.cache.data.LayerAndPixel
import com.jerry.boxes.cache.data.Pixel
import com.jerry.boxes.ui.boxes.data.ColorAndShape
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.shapes.*
import timber.log.Timber
import kotlin.math.floor
import kotlin.math.roundToInt

fun generateSelections(
    pixels: List<Pixel>
): SnapshotStateMap<Long, SnapshotStateMap<Point, SnapshotStateMap<Point, ColorAndShape>>> {
    return SnapshotStateMap<Long, SnapshotStateMap<Point, SnapshotStateMap<Point, ColorAndShape>>>().apply {
        putAll(
            pixels
                .groupBy { it.layerId }
                .mapValues {
                    SnapshotStateMap<Point, SnapshotStateMap<Point, ColorAndShape>>().apply {
                        putAll(
                            it.value.groupBy {
                                Point(
                                    floor(it.x.toFloat() / QUADRANT_SIZE).toInt(),
                                    floor(it.y.toFloat() / QUADRANT_SIZE).toInt()
                                )
                            }.mapValues {
                                it.value.associateTo(SnapshotStateMap()) {
                                    Point(it.x, it.y) to it.asColorAndShape
                                }
                            }
                        )
                    }
                }
        )
    }
}

fun generateSelectionsMap(layers: List<LayerAndPixel>): Map<Long, Map<Point, ColorAndShape>> =
    layers.flatMap {
        it.pixels
    }.groupBy {
        it.layerId
    }.mapValues {
        it.value.associateTo(SnapshotStateMap()) { pixel ->
            Point(pixel.x, pixel.y) to pixel.asColorAndShape
        }
    }

fun generateBoxes(
    numX: Int,
    numY: Int,
    size: Float,
    xOffSet: Float,
    yOffSet: Float
) = mutableMapOf<Point, RectF>().apply {
    for (y in 0 until numY) {
        for (x in 0 until numX) {
            val topLeft = Offset(
                (size * x) + xOffSet,
                (size * y) + yOffSet
            )
            put(
                Point(x, y),
                RectF(
                    topLeft.x,
                    topLeft.y,
                    (topLeft.x + size),
                    (topLeft.y + size)
                )
            )
        }
    }
}

fun Canvas.drawShapes(
    layers: Collection<LayerUi>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
    boxes: Map<Point, RectF>
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

fun DrawScope.drawShapes(
    layers: List<LayerUi>,
    selections: Map<Long, Map<Point, ColorAndShape>>,
    boxes: Map<Point, RectF>
) {
    if (boxes.isEmpty()) return
    val layerIds = layers.filter { it.on }.sortedBy { it.index }.map { it.id }
    layerIds.forEach { layerId ->
        selections[layerId]?.forEach {
            val position = boxes[it.key]
            position?.let { pos ->
                drawCustomShape(pos, it.value)
            }
        }
    }
}

fun DrawScope.drawShapes(
    layerId: Long,
    selections: Map<Point, ColorAndShape>?,
    boxes: Map<Point, RectF>
) {
    if (boxes.isEmpty() || selections.isNullOrEmpty()) return
    Timber.d("DrawTest - drawing: ${selections.size}")
    selections.forEach {
        val position = boxes[it.key]
        position?.let { pos ->
            drawCustomShape(pos, it.value)
        }
    }
}

fun DrawScope.pngBackground(size: Float) {
    val columns = (this.size.width / size).roundToInt()
    val rows = (this.size.height / size).roundToInt()
    for (r in 0..rows) {
        for (c in 0..columns) {
            drawRect(
                color = Color.Gray,
                topLeft = Offset(c * size, r * size),
                size = Size(size, size),
                alpha = when (r % 2 == 0) {
                    true -> when (c % 2 == 0) {
                        true -> 1F
                        else -> GRID_ODD_ALPHA
                    }
                    else -> when (c % 2 == 0) {
                        true -> GRID_ODD_ALPHA
                        else -> 1F
                    }
                }
            )
        }
    }
}

fun DrawScope.drawCustomShape(
    pos: RectF,
    color: ColorAndShape
) {
    (color.shape as ShapersInterface).draw(this, pos, color)
}

fun Canvas.drawCustomShape(
    pos: RectF,
    color: ColorAndShape
) {
    (color.shape as ShapersInterface).draw(this, pos, color)
}

const val QUADRANT_SIZE = 50F
private const val GRID_ODD_ALPHA = 0.5F
