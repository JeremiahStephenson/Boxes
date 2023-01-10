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
import com.jerry.boxes.ui.boxes.shapes.Shape
import timber.log.Timber

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
    layers: List<Layer>,
    selections: Map<Point, Map<Long, SerializableColor?>?>,
    boxes: Map<Point, RectF>
) {
    Timber.d("DrawTest - drawing")
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
        Shape.Box -> drawBox(pos, color)
        Shape.TriangleBottomLeft -> drawTriangleBottomLeft(pos, color)
        Shape.TriangleBottomRight -> drawTriangleBottomRight(pos, color)
        Shape.TriangleTopRight -> drawTriangleTopRight(pos, color)
        Shape.TriangleTopLeft -> drawTriangleTopLeft(pos, color)
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
        else -> {}
    }
}

private fun DrawScope.drawBox(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

private fun DrawScope.drawCircle(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

private fun DrawScope.drawStar(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.left + (pos.width() * 0.63F), pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + (pos.width() * 0.72F), pos.top + (pos.height() * 0.61F))
            lineTo(pos.left + (pos.width() * 0.81F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() / 2), pos.top + (pos.height() * 0.76F))
            lineTo(pos.left + (pos.width() * 0.19F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.28F), pos.top + (pos.height() * 0.61F))
            lineTo(pos.left, pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + (pos.width() * 0.37F), pos.top + (pos.height() * 0.38F))
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawTriangleBottomLeft(
    pos: RectF,
    color: SerializableColor
) {
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

private fun DrawScope.drawTriangleBottomRight(
    pos: RectF,
    color: SerializableColor
) {
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

private fun DrawScope.drawTriangleTopLeft(
    pos: RectF,
    color: SerializableColor
) {
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

private fun DrawScope.drawTriangleTopRight(
    pos: RectF,
    color: SerializableColor
) {
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

private fun DrawScope.drawRectangleLeft(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width() / 2, pos.height()),
        color = color.color
    )
}

private fun DrawScope.drawRectangleRight(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top),
        size = Size(pos.width() / 2, pos.height()),
        color = color.color
    )
}

private fun DrawScope.drawRectangleTop(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width(), pos.height() / 2),
        color = color.color
    )
}

private fun DrawScope.drawRectangleBottom(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top + (pos.height() / 2)),
        size = Size(pos.width(), pos.height() / 2),
        color = color.color
    )
}

private fun DrawScope.drawCornerArcRight(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - pos.width(), pos.top),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color
    )
}

private fun DrawScope.drawCornerArcLeft(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -90F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color
    )
}

private fun DrawScope.drawCornerArcRightInverse(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - pos.width(), pos.top - pos.height()),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color
    )
}

private fun DrawScope.drawCornerArcLeftInverse(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = 90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top - pos.height()),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color
    )
}

private fun DrawScope.drawBoxTopRight(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top),
        size = Size(pos.width() / 2, pos.height() / 2),
        color = color.color
    )
}

private fun DrawScope.drawBoxTopLeft(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width() / 2, pos.height() / 2),
        color = color.color
    )
}

private fun DrawScope.drawBoxBottomRight(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top + (pos.height() / 2)),
        size = Size(pos.width() / 2, pos.height() / 2),
        color = color.color
    )
}

private fun DrawScope.drawBoxBottomLeft(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top + (pos.height() / 2)),
        size = Size(pos.width() / 2, pos.height() / 2),
        color = color.color
    )
}

private fun DrawScope.drawDiamond(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.left + pos.width(), pos.top + (pos.height() / 2))
            lineTo(pos.left + (pos.width() / 2), pos.top + pos.height())
            lineTo(pos.left, pos.top + (pos.height() / 2))
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawArcRight(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -180F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

private fun DrawScope.drawArcTop(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -180F,
        sweepAngle = -180F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top - (pos.height() / 2)),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

private fun DrawScope.drawArcLeft(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 180F,
        useCenter = true,
        topLeft = Offset(pos.left - (pos.width() / 2), pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

private fun DrawScope.drawArcBottom(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -180F,
        sweepAngle = 180F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top + (pos.height() / 2)),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

private fun DrawScope.drawBottomLeftToTopRightLine(
    pos:RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() * 0.75F), pos.top)
            lineTo(pos.left + pos.width(), pos.top)
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.25F))
            lineTo(pos.left + (pos.width() * 0.25F), pos.top + pos.height())
            lineTo(pos.left, pos.top + pos.height())
            lineTo(pos.left, pos.top + (pos.height() * 0.75F))
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawTopLeftToBottomRightLine(
    pos:RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width() * 0.25F), pos.top)
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.75F))
            lineTo(pos.left + pos.width(), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.75F), pos.top + pos.height())
            lineTo(pos.left, pos.top + (pos.height() * 0.25F))
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawVerticalLine(
    pos:RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width() * 0.4F), pos.top),
        size = Size(pos.width() * 0.2F, pos.height()),
        color = color.color
    )
}

private fun DrawScope.drawHorizontalLine(
    pos:RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top + (pos.height() * 0.4F)),
        size = Size(pos.width(), pos.height() * 0.2F),
        color = color.color
    )
}

private fun DrawScope.drawBottomLeftElbow(
    pos:RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left, pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left, pos.top + (pos.height() * 0.6F))
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawBottomRightElbow(
    pos:RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + pos.height())
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawTopLeftElbow(
    pos:RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() * 0.4F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left, pos.top + (pos.height() * 0.6F))
            lineTo(pos.left, pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.4F))
            close()
        },
        color = color.color
    )
}

private fun DrawScope.drawTopRightElbow(
    pos:RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() * 0.4F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.6F))
            close()
        },
        color = color.color
    )
}