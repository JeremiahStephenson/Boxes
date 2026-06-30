package com.jerry.shapes.ui.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawBottomLeftElbow(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top + (pos.height() * 0.4F))
                lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.4F))
                lineTo(pos.left + (pos.width() * 0.6F), pos.top + pos.height())
                lineTo(pos.left + (pos.width() * 0.4F), pos.top + pos.height())
                lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.6F))
                lineTo(pos.left, pos.top + (pos.height() * 0.6F))
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawBottomLeftElbow(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left, pos.top + (pos.height() * 0.6F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawBottomRightElbow(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.4F))
                lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.4F))
                lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.6F))
                lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.6F))
                lineTo(pos.left + (pos.width() * 0.6F), pos.top + pos.height())
                lineTo(pos.left + (pos.width() * 0.4F), pos.top + pos.height())
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawBottomRightElbow(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + pos.height())
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTopLeftElbow(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width() * 0.4F), pos.top)
                lineTo(pos.left + (pos.width() * 0.6F), pos.top)
                lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.6F))
                lineTo(pos.left, pos.top + (pos.height() * 0.6F))
                lineTo(pos.left, pos.top + (pos.height() * 0.4F))
                lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.4F))
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTopLeftElbow(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left + (pos.width() * 0.4F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left, pos.top + (pos.height() * 0.6F))
            lineTo(pos.left, pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.4F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTopRightElbow(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width() * 0.4F), pos.top)
                lineTo(pos.left + (pos.width() * 0.6F), pos.top)
                lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.4F))
                lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.4F))
                lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.6F))
                lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.6F))
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTopRightElbow(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left + (pos.width() * 0.4F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.6F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}
