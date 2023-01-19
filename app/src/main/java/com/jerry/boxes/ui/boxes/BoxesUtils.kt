package com.jerry.boxes.ui.boxes

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.boxes.cache.data.LayerAndPixel
import com.jerry.boxes.cache.data.Pixel
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.shapes.*
import timber.log.Timber
import kotlin.math.roundToInt

fun generateSelections(pixels: List<Pixel>): SnapshotStateMap<Long, SnapshotStateMap<Point, ColorAndShape>> {
    return SnapshotStateMap<Long, SnapshotStateMap<Point, ColorAndShape>>().apply {
        putAll(
            pixels.groupBy { it.layerId }
                .mapValues {
                    it.value.associateTo(SnapshotStateMap()) {
                        Point(it.x, it.y) to it.asColorAndShape
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

fun DrawScope.drawShapes(
    layers: List<LayerUi>,
    selections: Map<Long, Map<Point, ColorAndShape>>,
    boxes: Map<Point, RectF>
) {
    if (boxes.isEmpty()) return
    val layerIds = layers.filter { it.on }.sortedBy { it.index }.map { it.id }
    Timber.d("DrawTest - drawing start: ${System.currentTimeMillis()}, ${selections.size}, ${layers.size}, ${boxes.size}")
    layerIds.forEach { layerId ->
        selections[layerId]?.forEach {
            val position = boxes[it.key]
            position?.let { pos ->
                drawCustomShape(pos, it.value)
            }
        }
    }
    //Timber.d("DrawTest - drawing end: ${System.currentTimeMillis()}")
}

fun DrawScope.drawShapes(
    layerId: Long,
    selections: Map<Point, ColorAndShape>?,
    boxes: Map<Point, RectF>
) {
    Timber.d("DrawTest - drawing: ${selections?.size}, ${layerId}, ${boxes.size}")
    if (boxes.isEmpty() || selections.isNullOrEmpty()) return
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
    when (color.shape) {
        Shape.Box -> drawBox(pos, color)
        Shape.TriangleBottomLeft -> drawTriangleBottomLeft(pos, color)
        Shape.TriangleBottomRight -> drawTriangleBottomRight(pos, color)
        Shape.TriangleTopRight -> drawTriangleTopRight(pos, color)
        Shape.TriangleTopLeft -> drawTriangleTopLeft(pos, color)
        Shape.TriangleBottomLeftSmall -> drawTriangleBottomLeftSmall(pos, color)
        Shape.TriangleBottomRightSmall -> drawTriangleBottomRightSmall(pos, color)
        Shape.TriangleTopRightSmall -> drawTriangleTopRightSmall(pos, color)
        Shape.TriangleTopLeftSmall -> drawTriangleTopLeftSmall(pos, color)
        Shape.TriangleBottomLeftSmallest -> drawTriangleBottomLeftSmallest(pos, color)
        Shape.TriangleBottomRightSmallest -> drawTriangleBottomRightSmallest(pos, color)
        Shape.TriangleTopRightSmallest -> drawTriangleTopRightSmallest(pos, color)
        Shape.TriangleTopLeftSmallest -> drawTriangleTopLeftSmallest(pos, color)
        Shape.RectangleTop -> drawRectangleTop(pos, color)
        Shape.RectangleBottom -> drawRectangleBottom(pos, color)
        Shape.RectangleLeft -> drawRectangleLeft(pos, color)
        Shape.RectangleRight -> drawRectangleRight(pos, color)
        Shape.ArcCornerLeft -> drawCornerArcLeft(pos, color)
        Shape.ArcCornerLeftInverse -> drawCornerArcLeftInverse(pos, color)
        Shape.ArcCornerRight -> drawCornerArcRight(pos, color)
        Shape.ArcCornerRightInverse -> drawCornerArcRightInverse(pos, color)
        Shape.Circle -> drawCircle(pos, color)
        Shape.Star -> drawStar(pos, color)
        Shape.BoxBottomLeft -> drawBoxBottomLeft(pos, color)
        Shape.BoxBottomRight -> drawBoxBottomRight(pos, color)
        Shape.BoxTopLeft -> drawBoxTopLeft(pos, color)
        Shape.BoxTopRight -> drawBoxTopRight(pos, color)
        Shape.Diamond -> drawDiamond(pos, color)
        Shape.ArcRight -> drawArcRight(pos, color)
        Shape.ArcTop -> drawArcTop(pos, color)
        Shape.ArcLeft -> drawArcLeft(pos, color)
        Shape.ArcBottom -> drawArcBottom(pos, color)
        Shape.BottomLeftToTopRightLine -> drawBottomLeftToTopRightLine(pos, color)
        Shape.TopLeftToBottomRightLine -> drawTopLeftToBottomRightLine(pos, color)
        Shape.HorizontalLine -> drawHorizontalLine(pos, color)
        Shape.VerticalLine -> drawVerticalLine(pos, color)
        Shape.BottomLeftElbow -> drawBottomLeftElbow(pos, color)
        Shape.BottomRightElbow -> drawBottomRightElbow(pos, color)
        Shape.TopLeftElbow -> drawTopLeftElbow(pos, color)
        Shape.TopRightElbow -> drawTopRightElbow(pos, color)
        Shape.Lego -> drawLegoSquare(pos, color)
        Shape.LegoRound -> drawLegoRound(pos, color)
        else -> {}
    }
}

private const val GRID_ODD_ALPHA = 0.5F