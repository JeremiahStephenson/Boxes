package com.jerry.shapes.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.ui.boxes.data.LayerState
import com.jerry.shapes.ui.shapes.ShapersInterface
import kotlin.math.roundToInt

fun generateBoxes(
    numX: Int,
    numY: Int,
    size: Float,
    xOffSet: Float,
    yOffSet: Float,
) = mutableMapOf<Point, Rect>().apply {
    for (y in 0 until numY) {
        for (x in 0 until numX) {
            val topLeft =
                Offset(
                    (size * x) + xOffSet,
                    (size * y) + yOffSet,
                )
            put(
                Point(x, y),
                Rect(
                    topLeft.x,
                    topLeft.y,
                    (topLeft.x + size),
                    (topLeft.y + size),
                ),
            )
        }
    }
}

fun DrawScope.drawShapes(
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

fun DrawScope.drawShapes(
    layers: List<LayerState>,
    selections: Map<Long, Map<Point, ColorAndShape>>,
    boxes: Map<Point, Rect>,
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
    selections: Map<Point, ColorAndShape>?,
    findCoordinate: (Point) -> Rect?,
) {
    if (selections.isNullOrEmpty()) return
    selections.forEach {
        val position = findCoordinate(it.key)
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
                alpha =
                    when (r % 2 == 0) {
                        true ->
                            when (c % 2 == 0) {
                                true -> 1F
                                else -> 0.4F
                            }
                        else ->
                            when (c % 2 == 0) {
                                true -> 0.4F
                                else -> 1F
                            }
                    },
            )
        }
    }
}

fun DrawScope.drawCustomShape(
    pos: Rect,
    color: ColorAndShape,
) {
    (color.shape as ShapersInterface).draw(this, pos, color)
}
