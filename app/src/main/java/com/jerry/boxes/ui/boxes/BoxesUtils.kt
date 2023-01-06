package com.jerry.boxes.ui.boxes

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.jerry.boxes.cache.data.Layer
import com.jerry.boxes.extensions.safeLet

fun generateBoxes(
    numX: Int,
    numY: Int,
    size: Float,
    xOffSet: Int,
    yOffSet: Int
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
    layers: List<Layer>,
    selections: Map<Point, Map<Long, SerializableColor?>?>,
    boxes: Map<Point, RectF>
) {
    val layerIds = layers.filter { it.on }.sortedBy { it.index }.map { it.id }
    selections.forEach { (point, pixels) ->
        val position = boxes[point]
        safeLet(position, pixels) { pos, selectedPixel ->
            layerIds.forEach {
                selectedPixel[it]?.let { color ->
                    drawCustomShape(pos, color)
                }
            }
        }
    }
}

fun DrawScope.drawCustomShape(
    pos: RectF,
    color: SerializableColor
) {
    when (color.shape) {
        Shape.Box -> {
            drawRect(
                style = Fill,
                topLeft = Offset(pos.left, pos.top),
                size = Size(pos.width(), pos.height()),
                color = color.color
            )
        }
        Shape.TriangleBottomLeft -> {
            drawPath(
                path = Path().apply {
                    moveTo(pos.left, pos.top)
                    lineTo(pos.right, pos.bottom)
                    lineTo(pos.left, pos.bottom)
                    close()
                },
                color = color.color
            )
        }
        Shape.TriangleBottomRight -> {
            drawPath(
                path = Path().apply {
                    moveTo(pos.left, pos.bottom)
                    lineTo(pos.right, pos.top)
                    lineTo(pos.right, pos.bottom)
                    close()
                },
                color = color.color
            )
        }
        Shape.TriangleTopRight -> {
            drawPath(
                path = Path().apply {
                    moveTo(pos.left, pos.top)
                    lineTo(pos.right, pos.top)
                    lineTo(pos.right, pos.bottom)
                    close()
                },
                color = color.color
            )
        }
        Shape.TriangleTopLeft -> {
            drawPath(
                path = Path().apply {
                    moveTo(pos.left, pos.top)
                    lineTo(pos.right, pos.top)
                    lineTo(pos.left, pos.bottom)
                    close()
                },
                color = color.color
            )
        }
        Shape.RectangleTop -> {
            drawRect(
                style = Fill,
                topLeft = Offset(pos.left, pos.top),
                size = Size(pos.width(), pos.height() / 2),
                color = color.color
            )
        }
        Shape.RectangleBottom -> {
            drawRect(
                style = Fill,
                topLeft = Offset(pos.left, pos.top + (pos.height() / 2)),
                size = Size(pos.width(), pos.height() / 2),
                color = color.color
            )
        }
        Shape.RectangleLeft -> {
            drawRect(
                style = Fill,
                topLeft = Offset(pos.left, pos.top),
                size = Size(pos.width() / 2, pos.height()),
                color = color.color
            )
        }
        Shape.RectangleRight -> {
            drawRect(
                style = Fill,
                topLeft = Offset(pos.left + (pos.width() / 2), pos.top),
                size = Size(pos.width() / 2, pos.height()),
                color = color.color
            )
        }
        Shape.ArcLeft -> {
            drawArc(
                startAngle = -90F,
                sweepAngle = -90F,
                useCenter = true,
                topLeft = Offset(pos.left, pos.top),
                size = Size(pos.width() * 2, pos.height() * 2),
                color = color.color
            )
        }
        Shape.ArcLeftInverse -> {
            drawArc(
                startAngle = 90F,
                sweepAngle = 90F,
                useCenter = true,
                topLeft = Offset(pos.left, pos.top - pos.height()),
                size = Size(pos.width() * 2, pos.height() * 2),
                color = color.color
            )
        }
        Shape.ArcRight -> {
            drawArc(
                startAngle = -90F,
                sweepAngle = 90F,
                useCenter = true,
                topLeft = Offset(pos.left - pos.width(), pos.top),
                size = Size(pos.width() * 2, pos.height() * 2),
                color = color.color
            )
        }
        Shape.ArcRightInverse -> {
            drawArc(
                startAngle = 0F,
                sweepAngle = 90F,
                useCenter = true,
                topLeft = Offset(pos.left - pos.width(), pos.top - pos.height()),
                size = Size(pos.width() * 2, pos.height() * 2),
                color = color.color
            )
        }
        Shape.Circle -> {
            drawArc(
                startAngle = 0F,
                sweepAngle = 360F,
                useCenter = true,
                topLeft = Offset(pos.left, pos.top),
                size = Size(pos.width(), pos.height()),
                color = color.color
            )
        }
        else -> {}
    }
}