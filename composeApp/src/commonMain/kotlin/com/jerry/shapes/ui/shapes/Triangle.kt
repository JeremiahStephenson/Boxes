package com.jerry.shapes.ui.shapes

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawTriangleLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.right, pos.top + (pos.height / 2))
                lineTo(pos.left, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.right, pos.top)
                lineTo(pos.left, pos.top + (pos.height / 2))
                lineTo(pos.right, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleTop(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.left + (pos.width / 2), pos.bottom)
                lineTo(pos.right, pos.top)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleBottom(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.bottom)
                lineTo(pos.left + (pos.width / 2), pos.top)
                lineTo(pos.right, pos.bottom)
                close()
            },
        color = color.color,
    )
}
