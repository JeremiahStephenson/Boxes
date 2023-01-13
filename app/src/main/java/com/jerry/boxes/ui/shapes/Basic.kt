package com.jerry.boxes.ui.shapes

import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
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

fun DrawScope.drawLegoSquare(
    pos: RectF,
    color: SerializableColor
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width() * 0.01F), pos.top + (pos.height() * 0.01F)),
        size = Size(pos.width() - (pos.width() * 0.02F), pos.height() - (pos.height() * 0.02F)),
        color = color.color
    )
    rotate(
        degrees = 50F,
        pivot = Offset(pos.left + (pos.width() / 2), pos.top + (pos.height() / 2))
    ) {
        drawOval(
            topLeft = Offset(pos.left + (pos.width() * 0.2F), pos.top + (pos.height() * 0.21F)),
            size = Size(pos.width() * 0.7F, pos.height() * 0.58F),
            brush = Brush.linearGradient(
                colors = listOf(Color.White, Color.Black.copy(alpha = 0.3F)),
                start = Offset(pos.left + (pos.width() * 0.2F), pos.top + (pos.height() / 2F)),
                end = Offset(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() / 2F))
            ),
        )
    }
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width() * 0.22F), pos.top + (pos.height() * 0.22F)),
        size = Size(pos.width() * 0.56F, pos.height() * 0.56F),
        color = color.color
    )
}

fun DrawScope.drawLegoRound(
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
    rotate(
        degrees = 50F,
        pivot = Offset(pos.left + (pos.width() / 2), pos.top + (pos.height() / 2))
    ) {
        drawOval(
            topLeft = Offset(pos.left + (pos.width() * 0.2F), pos.top + (pos.height() * 0.21F)),
            size = Size(pos.width() * 0.7F, pos.height() * 0.58F),
            brush = Brush.linearGradient(
                colors = listOf(Color.White, Color.Black.copy(alpha = 0.3F)),
                start = Offset(pos.left + (pos.width() * 0.2F), pos.top + (pos.height() / 2F)),
                end = Offset(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() / 2F))
            ),
        )
    }
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width() * 0.22F), pos.top + (pos.height() * 0.22F)),
        size = Size(pos.width() * 0.56F, pos.height() * 0.56F),
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