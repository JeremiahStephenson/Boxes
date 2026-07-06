package com.jerry.shapes.ui.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.util.CanvasExport

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

expect fun CanvasExport.drawRectangleLeft(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawRectangleRight(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawRectangleTop(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawRectangleBottom(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawBoxTopRight(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawBoxTopLeft(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawBoxBottomRight(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawBoxBottomLeft(
    pos: Rect,
    color: ColorAndShape,
)
