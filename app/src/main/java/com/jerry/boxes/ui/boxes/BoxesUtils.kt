package com.jerry.boxes.ui.boxes

import android.graphics.Point
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.jerry.boxes.cache.data.Layer
import com.jerry.boxes.extensions.safeLet
import com.jerry.boxes.ui.boxes.shapes.Shape
import timber.log.Timber

fun generateBoxes(
    numX: Int,
    numY: Int,
    size: Float,
    xOffSet: Float,
    yOffSet: Float
) = mutableMapOf<Point, Offset>().apply {
    for (y in 0 until numY) {
        for (x in 0 until numX) {
            val topLeft = Offset(
                (size * x) + xOffSet,
                (size * y) + yOffSet
            )
            put(
                Point(x, y),
                Offset(topLeft.x, topLeft.y)
            )
        }
    }
}

fun DrawScope.drawShapes(
    layers: List<Layer>,
    size: Float,
    selections: Map<Point, Map<Long, SerializableColor?>?>,
    boxes: Map<Point, Offset>
) {
    Timber.d("DrawTest - drawing")
    val layerIds = layers.filter { it.on }.sortedBy { it.index }.map { it.id }
    selections.forEach { (point, pixels) ->
        val position = boxes[point]
        safeLet(position, pixels) { pos, selectedPixel ->
            layerIds.forEach {
                selectedPixel[it]?.let { color ->
                    drawCustomShape(pos, size, color)
                }
            }
        }
    }
}

fun DrawScope.drawCustomShape(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    when (color.shape) {
        Shape.Box -> drawBox(pos, size, color)
        Shape.TriangleBottomLeft -> drawTriangleBottomLeft(pos, size, color)
        Shape.TriangleBottomRight -> drawTriangleBottomRight(pos, size, color)
        Shape.TriangleTopRight -> drawTriangleTopRight(pos, size, color)
        Shape.TriangleTopLeft -> drawTriangleTopLeft(pos, size, color)
        Shape.RectangleTop -> drawRectangleTop(pos, size, color)
        Shape.RectangleBottom -> drawRectangleBottom(pos, size, color)
        Shape.RectangleLeft -> drawRectangleLeft(pos, size, color)
        Shape.RectangleRight -> drawRectangleRight(pos, size, color)
        Shape.ArcLeft -> drawArcLeft(pos, size, color)
        Shape.ArcLeftInverse -> drawArcLeftInverse(pos, size, color)
        Shape.ArcRight -> drawArcRight(pos, size, color)
        Shape.ArcRightInverse -> drawArcRightInverse(pos, size, color)
        Shape.Circle -> drawCircle(pos, size, color)
        Shape.Star -> drawStar(pos, size, color)
        Shape.BoxBottomLeft -> drawBoxBottomLeft(pos, size, color)
        Shape.BoxBottomRight -> drawBoxBottomRight(pos, size, color)
        Shape.BoxTopLeft -> drawBoxTopLeft(pos, size, color)
        Shape.BoxTopRight -> drawBoxTopRight(pos, size, color)
        else -> {}
    }
}

private fun DrawScope.drawBox(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.x, pos.y),
        size = Size(size, size),
        color = color.color
    )
}

private fun DrawScope.drawCircle(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.x, pos.y),
        size = Size(size, size),
        color = color.color
    )
}

private fun DrawScope.drawStar(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.x + (size / 2), pos.y)
            lineTo(pos.x + (size * 0.63F), pos.y + (size * 0.38F))
            lineTo(pos.x + size, pos.y + (size * 0.38F))
            lineTo(pos.x + (size * 0.72F), pos.y + (size * 0.61F))
            lineTo(pos.x + (size * 0.81F), pos.y + size)
            lineTo(pos.x + (size / 2), pos.y + (size * 0.76F))
            lineTo(pos.x + (size * 0.19F), pos.y + size)
            lineTo(pos.x + (size * 0.28F), pos.y + (size * 0.61F))
            lineTo(pos.x, pos.y + (size * 0.38F))
            lineTo(pos.x + (size * 0.37F), pos.y + (size * 0.38F))
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawTriangleBottomLeft(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.x, pos.y)
            lineTo(pos.x + size, pos.y + size)
            lineTo(pos.x, pos.y + size)
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawTriangleBottomRight(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.x, pos.y + size)
            lineTo(pos.x + size, pos.y)
            lineTo(pos.x + size, pos.y + size)
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawTriangleTopLeft(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.x, pos.y)
            lineTo(pos.x + size, pos.y)
            lineTo(pos.x, pos.y + size)
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawTriangleTopRight(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.x, pos.y)
            lineTo(pos.x + size, pos.y)
            lineTo(pos.x + size, pos.y + size)
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawRectangleLeft(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.x, pos.y),
        size = Size(size / 2, size),
        color = color.color
    )
}

private fun DrawScope.drawRectangleRight(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.x + (size / 2), pos.y),
        size = Size(size / 2, size),
        color = color.color
    )
}

private fun DrawScope.drawRectangleTop(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.x, pos.y),
        size = Size(size, size / 2F),
        color = color.color
    )
}

private fun DrawScope.drawRectangleBottom(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.x, pos.y + (size / 2)),
        size = Size(size, size / 2),
        color = color.color
    )
}

private fun DrawScope.drawArcRight(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.x - size, pos.y),
        size = Size(size * 2, size * 2),
        color = color.color
    )
}

private fun DrawScope.drawArcLeft(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -90F,
        useCenter = true,
        topLeft = Offset(pos.x, pos.y),
        size = Size(size * 2, size * 2),
        color = color.color
    )
}

private fun DrawScope.drawArcRightInverse(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.x - size, pos.y - size),
        size = Size(size * 2, size * 2),
        color = color.color
    )
}

private fun DrawScope.drawArcLeftInverse(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawArc(
        startAngle = 90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.x, pos.y - size),
        size = Size(size * 2, size * 2),
        color = color.color
    )
}

private fun DrawScope.drawBoxTopRight(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.x + (size / 2), pos.y),
        size = Size(size / 2, size / 2),
        color = color.color
    )
}

private fun DrawScope.drawBoxTopLeft(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.x, pos.y),
        size = Size(size / 2, size / 2),
        color = color.color
    )
}

private fun DrawScope.drawBoxBottomRight(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.x + (size / 2), pos.y + (size / 2)),
        size = Size(size / 2, size / 2),
        color = color.color
    )
}

private fun DrawScope.drawBoxBottomLeft(
    pos: Offset,
    size: Float,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.x, pos.y + (size / 2)),
        size = Size(size / 2, size / 2),
        color = color.color
    )
}