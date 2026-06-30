package com.jerry.shapes.ui.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawCornerArcRight(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - pos.width(), pos.top),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color,
    )
}

fun Canvas.drawCornerArcRight(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(pos.left - pos.width(), pos.top, pos.right, pos.bottom + pos.height()),
        -90F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawCornerArcRightSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - (pos.width() / 2), pos.top + (pos.height() / 2)),
        size = Size(pos.width(), pos.height()),
        color = color.color,
    )
}

fun Canvas.drawCornerArcRightSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(
            pos.left - (pos.width() / 2),
            pos.top + (pos.height() / 2),
            pos.right - (pos.width() / 2),
            pos.bottom + (pos.height() / 2),
        ),
        -90F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawCornerArcLeft(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -90F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color,
    )
}

fun Canvas.drawCornerArcLeft(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(pos.left, pos.top, pos.right + pos.width(), pos.bottom + pos.height()),
        -90F,
        -90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawCornerArcLeftSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -90F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top + (pos.height() / 2)),
        size = Size(pos.width(), pos.height()),
        color = color.color,
    )
}

fun Canvas.drawCornerArcLeftSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(
            pos.left + (pos.width() / 2),
            pos.top + (pos.height() / 2),
            pos.right + (pos.width() / 2),
            pos.bottom + (pos.height() / 2),
        ),
        -90F,
        -90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawCornerArcRightInverse(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - pos.width(), pos.top - pos.height()),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color,
    )
}

fun Canvas.drawCornerArcRightInverse(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(pos.left - pos.width(), pos.top - pos.height(), pos.right, pos.bottom),
        0F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawCornerArcRightInverseSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - (pos.width() / 2), pos.top - (pos.height() / 2)),
        size = Size(pos.width(), pos.height()),
        color = color.color,
    )
}

fun Canvas.drawCornerArcRightInverseSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(
            pos.left - (pos.width() / 2),
            pos.top - (pos.height() / 2),
            pos.right - (pos.width() / 2),
            pos.bottom - (pos.height() / 2),
        ),
        0F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawCornerArcLeftInverse(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = 90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top - pos.height()),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color,
    )
}

fun Canvas.drawCornerArcLeftInverse(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(pos.left, pos.top - pos.height(), pos.right + pos.width(), pos.bottom),
        90F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawCornerArcLeftInverseSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = 90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top - (pos.height() / 2)),
        size = Size(pos.width(), pos.height()),
        color = color.color,
    )
}

fun Canvas.drawCornerArcLeftInverseSmall(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(
            pos.left + (pos.width() / 2),
            pos.top - (pos.height() / 2),
            pos.right + (pos.width() / 2),
            pos.bottom - (pos.height() / 2),
        ),
        90F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawArcRight(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -180F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color,
    )
}

fun Canvas.drawArcRight(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(pos.left + (pos.width() / 2), pos.top, pos.right + (pos.width() / 2), pos.bottom),
        -90F,
        -180F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawArcTop(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -180F,
        sweepAngle = -180F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top - (pos.height() / 2)),
        size = Size(pos.width(), pos.height()),
        color = color.color,
    )
}

fun Canvas.drawArcTop(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(pos.left, pos.top - (pos.height() / 2), pos.right, pos.bottom - (pos.height() / 2)),
        -180F,
        -180F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawArcLeft(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 180F,
        useCenter = true,
        topLeft = Offset(pos.left - (pos.width() / 2), pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color,
    )
}

fun Canvas.drawArcLeft(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(pos.left - (pos.width() / 2), pos.top, pos.right - (pos.width() / 2), pos.bottom),
        -90F,
        180F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

fun DrawScope.drawArcBottom(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -180F,
        sweepAngle = 180F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top + (pos.height() / 2)),
        size = Size(pos.width(), pos.height()),
        color = color.color,
    )
}

fun Canvas.drawArcBottom(
    pos: RectF,
    color: ColorAndShape,
) {
    drawArc(
        RectF(pos.left, pos.top + (pos.height() / 2), pos.right, pos.bottom + (pos.height() / 2)),
        -180F,
        180F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}
