package com.jerry.boxes.ui.shapes

import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.jerry.boxes.ui.boxes.SerializableColor

fun DrawScope.drawBox(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

fun DrawScope.drawCircle(
    pos: RectF,
    color: SerializableColor
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

fun DrawScope.drawStar(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.left + (pos.width() * 0.63F), pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + (pos.width() * 0.72F), pos.top + (pos.height() * 0.61F))
            lineTo(pos.left + (pos.width() * 0.81F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() / 2), pos.top + (pos.height() * 0.76F))
            lineTo(pos.left + (pos.width() * 0.19F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.28F), pos.top + (pos.height() * 0.61F))
            lineTo(pos.left, pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + (pos.width() * 0.37F), pos.top + (pos.height() * 0.38F))
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawDiamond(
    pos: RectF,
    color: SerializableColor
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.left + pos.width(), pos.top + (pos.height() / 2))
            lineTo(pos.left + (pos.width() / 2), pos.top + pos.height())
            lineTo(pos.left, pos.top + (pos.height() / 2))
            close()
        },
        color = color.color
    )
}