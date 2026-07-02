package com.jerry.shapes.ui.shapes

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawTriangleBottomLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.right, pos.bottom)
                lineTo(pos.left, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleBottomRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.bottom)
                lineTo(pos.right, pos.top)
                lineTo(pos.right, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleTopLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.right, pos.top)
                lineTo(pos.left, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleTopRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.right, pos.top)
                lineTo(pos.right, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleBottomLeftSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top + (pos.height / 2))
                lineTo(pos.left + (pos.width / 2), pos.bottom)
                lineTo(pos.left, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleBottomRightSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width / 2), pos.bottom)
                lineTo(pos.right, pos.top + (pos.height / 2))
                lineTo(pos.right, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleTopLeftSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.left + (pos.width / 2), pos.top)
                lineTo(pos.left, pos.top + (pos.height / 2))
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleTopRightSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width / 2), pos.top)
                lineTo(pos.right, pos.top)
                lineTo(pos.right, pos.top + (pos.height / 2))
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleBottomLeftSmallest(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.bottom - (pos.height / 4))
                lineTo(pos.left + (pos.width / 4), pos.bottom)
                lineTo(pos.left, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleBottomRightSmallest(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.right - (pos.width / 4), pos.bottom)
                lineTo(pos.right, pos.bottom - (pos.height / 4))
                lineTo(pos.right, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleTopLeftSmallest(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.left + (pos.width / 4), pos.top)
                lineTo(pos.left, pos.top + (pos.height / 4))
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTriangleTopRightSmallest(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.right - (pos.width / 4), pos.top)
                lineTo(pos.right, pos.top)
                lineTo(pos.right, pos.top + (pos.height / 4))
                close()
            },
        color = color.color,
    )
}
