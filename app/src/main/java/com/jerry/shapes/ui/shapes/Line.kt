package com.jerry.shapes.ui.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawBottomLeftToTopRightLine(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width() * 0.75F), pos.top)
                lineTo(pos.left + pos.width(), pos.top)
                lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.25F))
                lineTo(pos.left + (pos.width() * 0.25F), pos.top + pos.height())
                lineTo(pos.left, pos.top + pos.height())
                lineTo(pos.left, pos.top + (pos.height() * 0.75F))
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawBottomLeftToTopRightLine(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left + (pos.width() * 0.75F), pos.top)
            lineTo(pos.left + pos.width(), pos.top)
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.25F))
            lineTo(pos.left + (pos.width() * 0.25F), pos.top + pos.height())
            lineTo(pos.left, pos.top + pos.height())
            lineTo(pos.left, pos.top + (pos.height() * 0.75F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawTopLeftToBottomRightLine(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.left + (pos.width() * 0.25F), pos.top)
                lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.75F))
                lineTo(pos.left + pos.width(), pos.top + pos.height())
                lineTo(pos.left + (pos.width() * 0.75F), pos.top + pos.height())
                lineTo(pos.left, pos.top + (pos.height() * 0.25F))
                close()
            },
        color = color.color,
    )
}

fun Canvas.drawTopLeftToBottomRightLine(
    pos: RectF,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width() * 0.25F), pos.top)
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.75F))
            lineTo(pos.left + pos.width(), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.75F), pos.top + pos.height())
            lineTo(pos.left, pos.top + (pos.height() * 0.25F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawVerticalLine(
    pos: RectF,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width() * 0.4F), pos.top),
        size = Size(pos.width() * 0.2F, pos.height()),
        color = color.color,
    )
}

fun Canvas.drawVerticalLine(
    pos: RectF,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left + (pos.width() * 0.4F), pos.top, pos.right - (pos.width() * 0.4F), pos.bottom),
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawHorizontalLine(
    pos: RectF,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top + (pos.height() * 0.4F)),
        size = Size(pos.width(), pos.height() * 0.2F),
        color = color.color,
    )
}

fun Canvas.drawHorizontalLine(
    pos: RectF,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left, pos.top + (pos.height() * 0.4F), pos.right, pos.bottom - (pos.height() * 0.4F)),
        Paint().apply { this.color = color.color.toArgb() },
    )
}
