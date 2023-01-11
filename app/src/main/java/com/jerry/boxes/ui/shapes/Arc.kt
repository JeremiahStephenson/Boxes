package com.jerry.boxes.ui.shapes

import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.boxes.ui.boxes.SerializableColor

fun DrawScope.drawCornerArcRight(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - pos.width(), pos.top),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color
    )
}

fun DrawScope.drawCornerArcLeft(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -90F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color
    )
}

fun DrawScope.drawCornerArcRightInverse(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - pos.width(), pos.top - pos.height()),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color
    )
}

fun DrawScope.drawCornerArcLeftInverse(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = 90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top - pos.height()),
        size = Size(pos.width() * 2, pos.height() * 2),
        color = color.color
    )
}

fun DrawScope.drawArcRight(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -180F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width() / 2), pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

fun DrawScope.drawArcTop(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -180F,
        sweepAngle = -180F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top - (pos.height() / 2)),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

fun DrawScope.drawArcLeft(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 180F,
        useCenter = true,
        topLeft = Offset(pos.left - (pos.width() / 2), pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

fun DrawScope.drawArcBottom(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = -180F,
        sweepAngle = 180F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top + (pos.height() / 2)),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}