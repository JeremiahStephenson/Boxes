package com.jerry.bit.shapes.ui.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import com.jerry.bit.shapes.cache.data.ColorAndShape
import kotlin.math.min

fun DrawScope.drawRing(
    pos: RectF,
    color: ColorAndShape,
) {
    val strokeWidth = min(pos.width(), pos.height()) * 0.18F
    drawCircle(
        color = color.color,
        radius = (min(pos.width(), pos.height()) - strokeWidth) / 2F,
        center = Offset(pos.centerX(), pos.centerY()),
        style = Stroke(strokeWidth),
    )
}

fun Canvas.drawRing(
    pos: RectF,
    color: ColorAndShape,
) {
    val strokeWidth = min(pos.width(), pos.height()) * 0.18F
    drawCircle(
        pos.centerX(),
        pos.centerY(),
        (min(pos.width(), pos.height()) - strokeWidth) / 2F,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color.color.toArgb()
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
        },
    )
}

fun DrawScope.drawSmallCircle(
    pos: RectF,
    color: ColorAndShape,
) {
    drawCircle(
        color = color.color,
        radius = min(pos.width(), pos.height()) * 0.25F,
        center = Offset(pos.centerX(), pos.centerY()),
    )
}

fun Canvas.drawSmallCircle(
    pos: RectF,
    color: ColorAndShape,
) {
    drawCircle(
        pos.centerX(),
        pos.centerY(),
        min(pos.width(), pos.height()) * 0.25F,
        Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawHorizontalCapsule(
    pos: RectF,
    color: ColorAndShape,
) {
    val height = pos.height() * 0.5F
    drawRoundRect(
        color = color.color,
        topLeft = Offset(pos.left, pos.top + (pos.height() * 0.25F)),
        size = Size(pos.width(), height),
        cornerRadius = CornerRadius(height / 2F),
    )
}

fun Canvas.drawHorizontalCapsule(
    pos: RectF,
    color: ColorAndShape,
) {
    val inset = pos.height() * 0.25F
    val radius = pos.height() * 0.25F
    drawRoundRect(
        RectF(pos.left, pos.top + inset, pos.right, pos.bottom - inset),
        radius,
        radius,
        Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawVerticalCapsule(
    pos: RectF,
    color: ColorAndShape,
) {
    val width = pos.width() * 0.5F
    drawRoundRect(
        color = color.color,
        topLeft = Offset(pos.left + (pos.width() * 0.25F), pos.top),
        size = Size(width, pos.height()),
        cornerRadius = CornerRadius(width / 2F),
    )
}

fun Canvas.drawVerticalCapsule(
    pos: RectF,
    color: ColorAndShape,
) {
    val inset = pos.width() * 0.25F
    val radius = pos.width() * 0.25F
    drawRoundRect(
        RectF(pos.left + inset, pos.top, pos.right - inset, pos.bottom),
        radius,
        radius,
        Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color.color.toArgb() },
    )
}
