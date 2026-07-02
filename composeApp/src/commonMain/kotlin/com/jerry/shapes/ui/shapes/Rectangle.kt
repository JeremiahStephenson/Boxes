package com.jerry.shapes.ui.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawRectangleLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width / 2, pos.height),
        color = color.color,
    )
}

fun DrawScope.drawRectangleRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width / 2), pos.top),
        size = Size(pos.width / 2, pos.height),
        color = color.color,
    )
}

fun DrawScope.drawRectangleTop(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width, pos.height / 2),
        color = color.color,
    )
}

fun DrawScope.drawRectangleBottom(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top + (pos.height / 2)),
        size = Size(pos.width, pos.height / 2),
        color = color.color,
    )
}

fun DrawScope.drawBoxTopRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width / 2), pos.top),
        size = Size(pos.width / 2, pos.height / 2),
        color = color.color,
    )
}

fun DrawScope.drawBoxTopLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width / 2, pos.height / 2),
        color = color.color,
    )
}

fun DrawScope.drawBoxBottomRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width / 2), pos.top + (pos.height / 2)),
        size = Size(pos.width / 2, pos.height / 2),
        color = color.color,
    )
}

fun DrawScope.drawBoxBottomLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top + (pos.height / 2)),
        size = Size(pos.width / 2, pos.height / 2),
        color = color.color,
    )
}
