package com.jerry.shapes.ui.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawTriangleBottomLeft(
    pos: RectF,
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

fun Canvas.drawTriangleBottomLeft(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.right, pos.bottom)
            lineTo(pos.left, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleBottomRight(
    pos: RectF,
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

fun Canvas.drawTriangleBottomRight(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.bottom)
            lineTo(pos.right, pos.top)
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleTopLeft(
    pos: RectF,
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

fun Canvas.drawTriangleTopLeft(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.right, pos.top)
            lineTo(pos.left, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleTopRight(
    pos: RectF,
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

fun Canvas.drawTriangleTopRight(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.right, pos.top)
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleBottomLeftSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top + (pos.height() / 2))
                lineTo(pos.left + (pos.width() / 2), pos.bottom)
                lineTo(pos.left, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTriangleBottomLeftSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top + (pos.height() / 2))
            lineTo(pos.left + (pos.width() / 2), pos.bottom)
            lineTo(pos.left, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleBottomRightSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width() / 2), pos.bottom)
                lineTo(pos.right, pos.top + (pos.height() / 2))
                lineTo(pos.right, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTriangleBottomRightSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.bottom)
            lineTo(pos.right, pos.top + (pos.height() / 2))
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleTopLeftSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.left + (pos.width() / 2), pos.top)
                lineTo(pos.left, pos.top + (pos.height() / 2))
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTriangleTopLeftSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.left, pos.top + (pos.height() / 2))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleTopRightSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width() / 2), pos.top)
                lineTo(pos.right, pos.top)
                lineTo(pos.right, pos.top + (pos.height() / 2))
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTriangleTopRightSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.right, pos.top)
            lineTo(pos.right, pos.top + (pos.height() / 2))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleBottomLeftSmallest(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.bottom - (pos.height() / 4))
                lineTo(pos.left + (pos.width() / 4), pos.bottom)
                lineTo(pos.left, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTriangleBottomLeftSmallest(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.bottom - (pos.height() / 4))
            lineTo(pos.left + (pos.width() / 4), pos.bottom)
            lineTo(pos.left, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleBottomRightSmallest(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.right - (pos.width() / 4), pos.bottom)
                lineTo(pos.right, pos.bottom - (pos.height() / 4))
                lineTo(pos.right, pos.bottom)
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTriangleBottomRightSmallest(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.right - (pos.width() / 4), pos.bottom)
            lineTo(pos.right, pos.bottom - (pos.height() / 4))
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleTopLeftSmallest(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.left + (pos.width() / 4), pos.top)
                lineTo(pos.left, pos.top + (pos.height() / 4))
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTriangleTopLeftSmallest(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width() / 4), pos.top)
            lineTo(pos.left, pos.top + (pos.height() / 4))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTriangleTopRightSmallest(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.right - (pos.width() / 4), pos.top)
                lineTo(pos.right, pos.top)
                lineTo(pos.right, pos.top + (pos.height() / 4))
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTriangleTopRightSmallest(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.right - (pos.width() / 4), pos.top)
            lineTo(pos.right, pos.top)
            lineTo(pos.right, pos.top + (pos.height() / 4))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}
