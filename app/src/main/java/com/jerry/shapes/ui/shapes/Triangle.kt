package com.jerry.shapes.ui.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawTriangleLeft(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.right, pos.top + (pos.height() / 2))
            lineTo(pos.left, pos.bottom)
            close()
        },
        color = color.color
    )
}

fun Canvas.drawTriangleLeft(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.right, pos.top + (pos.height() / 2))
            lineTo(pos.left, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawTriangleRight(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.right, pos.top)
            lineTo(pos.left, pos.top + (pos.height() / 2))
            lineTo(pos.right, pos.bottom)
            close()
        },
        color = color.color
    )
}

fun Canvas.drawTriangleRight(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.right, pos.top)
            lineTo(pos.left, pos.top + (pos.height() / 2))
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawTriangleTop(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width() / 2), pos.bottom)
            lineTo(pos.right, pos.top)
            close()
        },
        color = color.color
    )
}

fun Canvas.drawTriangleTop(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width() / 2), pos.bottom)
            lineTo(pos.right, pos.top)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawTriangleBottom(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left, pos.bottom)
            lineTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.right, pos.bottom)
            close()
        },
        color = color.color
    )
}

fun Canvas.drawTriangleBottom(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.bottom)
            lineTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() }
    )
}