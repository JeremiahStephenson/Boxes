package com.jerry.bit.shapes.ui.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import com.jerry.bit.shapes.cache.data.ColorAndShape

private enum class ConnectorDirection {
    TOP,
    RIGHT,
    BOTTOM,
    LEFT,
}

fun DrawScope.drawTTop(
    pos: RectF,
    color: ColorAndShape,
) = drawTConnector(pos, color, ConnectorDirection.TOP)

fun DrawScope.drawTRight(
    pos: RectF,
    color: ColorAndShape,
) = drawTConnector(pos, color, ConnectorDirection.RIGHT)

fun DrawScope.drawTBottom(
    pos: RectF,
    color: ColorAndShape,
) = drawTConnector(pos, color, ConnectorDirection.BOTTOM)

fun DrawScope.drawTLeft(
    pos: RectF,
    color: ColorAndShape,
) = drawTConnector(pos, color, ConnectorDirection.LEFT)

fun Canvas.drawTTop(
    pos: RectF,
    color: ColorAndShape,
) = drawTConnector(pos, color, ConnectorDirection.TOP)

fun Canvas.drawTRight(
    pos: RectF,
    color: ColorAndShape,
) = drawTConnector(pos, color, ConnectorDirection.RIGHT)

fun Canvas.drawTBottom(
    pos: RectF,
    color: ColorAndShape,
) = drawTConnector(pos, color, ConnectorDirection.BOTTOM)

fun Canvas.drawTLeft(
    pos: RectF,
    color: ColorAndShape,
) = drawTConnector(pos, color, ConnectorDirection.LEFT)

fun DrawScope.drawFourWayIntersection(
    pos: RectF,
    color: ColorAndShape,
) {
    drawHorizontalConnector(pos, color)
    drawVerticalConnector(pos, color)
}

fun Canvas.drawFourWayIntersection(
    pos: RectF,
    color: ColorAndShape,
) {
    drawHorizontalConnector(pos, color)
    drawVerticalConnector(pos, color)
}

private fun DrawScope.drawTConnector(
    pos: RectF,
    color: ColorAndShape,
    direction: ConnectorDirection,
) {
    when (direction) {
        ConnectorDirection.TOP,
        ConnectorDirection.BOTTOM,
        -> drawHorizontalConnector(pos, color)
        ConnectorDirection.RIGHT,
        ConnectorDirection.LEFT,
        -> drawVerticalConnector(pos, color)
    }
    val left = pos.left + (pos.width() * 0.4F)
    val top = pos.top + (pos.height() * 0.4F)
    when (direction) {
        ConnectorDirection.TOP -> drawRect(color.color, Offset(left, pos.top), Size(pos.width() * 0.2F, pos.height() * 0.6F))
        ConnectorDirection.RIGHT -> drawRect(color.color, Offset(left, top), Size(pos.width() * 0.6F, pos.height() * 0.2F))
        ConnectorDirection.BOTTOM -> drawRect(color.color, Offset(left, top), Size(pos.width() * 0.2F, pos.height() * 0.6F))
        ConnectorDirection.LEFT -> drawRect(color.color, Offset(pos.left, top), Size(pos.width() * 0.6F, pos.height() * 0.2F))
    }
}

private fun Canvas.drawTConnector(
    pos: RectF,
    color: ColorAndShape,
    direction: ConnectorDirection,
) {
    when (direction) {
        ConnectorDirection.TOP,
        ConnectorDirection.BOTTOM,
        -> drawHorizontalConnector(pos, color)
        ConnectorDirection.RIGHT,
        ConnectorDirection.LEFT,
        -> drawVerticalConnector(pos, color)
    }
    val left = pos.left + (pos.width() * 0.4F)
    val right = pos.right - (pos.width() * 0.4F)
    val top = pos.top + (pos.height() * 0.4F)
    val bottom = pos.bottom - (pos.height() * 0.4F)
    val paint = Paint().apply { this.color = color.color.toArgb() }
    when (direction) {
        ConnectorDirection.TOP -> drawRect(left, pos.top, right, pos.top + (pos.height() * 0.6F), paint)
        ConnectorDirection.RIGHT -> drawRect(left, top, pos.right, bottom, paint)
        ConnectorDirection.BOTTOM -> drawRect(left, top, right, pos.bottom, paint)
        ConnectorDirection.LEFT -> drawRect(pos.left, top, pos.left + (pos.width() * 0.6F), bottom, paint)
    }
}

private fun DrawScope.drawHorizontalConnector(
    pos: RectF,
    color: ColorAndShape,
) {
    drawRect(
        color = color.color,
        topLeft = Offset(pos.left, pos.top + (pos.height() * 0.4F)),
        size = Size(pos.width(), pos.height() * 0.2F),
    )
}

private fun DrawScope.drawVerticalConnector(
    pos: RectF,
    color: ColorAndShape,
) {
    drawRect(
        color = color.color,
        topLeft = Offset(pos.left + (pos.width() * 0.4F), pos.top),
        size = Size(pos.width() * 0.2F, pos.height()),
    )
}

private fun Canvas.drawHorizontalConnector(
    pos: RectF,
    color: ColorAndShape,
) {
    drawRect(
        pos.left,
        pos.top + (pos.height() * 0.4F),
        pos.right,
        pos.bottom - (pos.height() * 0.4F),
        Paint().apply { this.color = color.color.toArgb() },
    )
}

private fun Canvas.drawVerticalConnector(
    pos: RectF,
    color: ColorAndShape,
) {
    drawRect(
        pos.left + (pos.width() * 0.4F),
        pos.top,
        pos.right - (pos.width() * 0.4F),
        pos.bottom,
        Paint().apply { this.color = color.color.toArgb() },
    )
}
