package com.jerry.boxes.ui.shapes

import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.jerry.boxes.ui.boxes.ColorAndShape

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