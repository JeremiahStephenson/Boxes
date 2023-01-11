package com.jerry.boxes.ui.shapes

import android.graphics.RectF
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.boxes.ui.boxes.SerializableColor

fun DrawScope.drawTriangleBottomLeft(
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

fun DrawScope.drawTriangleBottomRight(
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

fun DrawScope.drawTriangleTopLeft(
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

fun DrawScope.drawTriangleTopRight(
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

fun DrawScope.drawTriangleBottomLeftSmall(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left, pos.top + (pos.height() / 2))
            lineTo(pos.left + (pos.width() / 2), pos.bottom)
            lineTo(pos.left, pos.bottom)
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawTriangleBottomRightSmall(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.bottom)
            lineTo(pos.right, pos.top + (pos.height() / 2))
            lineTo(pos.right, pos.bottom)
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawTriangleTopLeftSmall(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.left, pos.top + (pos.height() / 2))
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawTriangleTopRightSmall(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.right, pos.top)
            lineTo(pos.right, pos.top + (pos.height() / 2))
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawTriangleBottomLeftSmallest(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left, pos.bottom - (pos.height() / 4))
            lineTo(pos.left + (pos.width() / 4), pos.bottom)
            lineTo(pos.left, pos.bottom)
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawTriangleBottomRightSmallest(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.right - (pos.width() / 4), pos.bottom)
            lineTo(pos.right, pos.bottom - (pos.height() / 4))
            lineTo(pos.right, pos.bottom)
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawTriangleTopLeftSmallest(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width() / 4), pos.top)
            lineTo(pos.left, pos.top + (pos.height() / 4))
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawTriangleTopRightSmallest(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.right - (pos.width() / 4), pos.top)
            lineTo(pos.right, pos.top)
            lineTo(pos.right, pos.top + (pos.height() / 4))
            close()
        },
        color = color.color
    )
}