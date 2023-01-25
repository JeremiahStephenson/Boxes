package com.jerry.shapes.ui.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawRectangleLeft(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width() / 2, pos.height()),
        color = color.color
    )
}

fun Canvas.drawRectangleLeft(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        RectF(pos.left, pos.top, pos.right - (pos.width() / 2), pos.bottom),
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawRectangleRight(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top),
        size = Size(pos.width() / 2, pos.height()),
        color = color.color
    )
}

fun Canvas.drawRectangleRight(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        RectF(pos.left + (pos.width() / 2), pos.top, pos.right, pos.bottom),
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawRectangleTop(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width(), pos.height() / 2),
        color = color.color
    )
}

fun Canvas.drawRectangleTop(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        RectF(pos.left, pos.top, pos.right, pos.top + (pos.height() / 2)),
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawRectangleBottom(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top + (pos.height() / 2)),
        size = Size(pos.width(), pos.height() / 2),
        color = color.color
    )
}

fun Canvas.drawRectangleBottom(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        RectF(pos.left, pos.top + (pos.height() / 2), pos.right, pos.bottom),
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawBoxTopRight(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top),
        size = Size(pos.width() / 2, pos.height() / 2),
        color = color.color
    )
}

fun Canvas.drawBoxTopRight(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        RectF(pos.left + (pos.width() / 2), pos.top, pos.right, pos.top + (pos.height() / 2)),
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawBoxTopLeft(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width() / 2, pos.height() / 2),
        color = color.color
    )
}

fun Canvas.drawBoxTopLeft(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        RectF(pos.left, pos.top, pos.right - (pos.width() / 2), pos.top + (pos.height() / 2)),
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawBoxBottomRight(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top + (pos.height() / 2)),
        size = Size(pos.width() / 2, pos.height() / 2),
        color = color.color
    )
}

fun Canvas.drawBoxBottomRight(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        RectF(pos.left + (pos.width() / 2), pos.top + (pos.height() / 2), pos.right, pos.bottom),
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawBoxBottomLeft(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top + (pos.height() / 2)),
        size = Size(pos.width() / 2, pos.height() / 2),
        color = color.color
    )
}

fun Canvas.drawBoxBottomLeft(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        RectF(pos.left, pos.top + (pos.height() / 2), pos.left + (pos.height() / 2), pos.bottom),
        Paint().apply { this.color = color.color.toArgb() }
    )
}
