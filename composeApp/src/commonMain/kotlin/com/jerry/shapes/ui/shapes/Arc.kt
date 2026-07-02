package com.jerry.shapes.ui.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawCornerArcRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - pos.width, pos.top),
        size = Size(pos.width * 2, pos.height * 2),
        color = color.color,
    )
}

fun DrawScope.drawCornerArcRightSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - (pos.width / 2), pos.top + (pos.height / 2)),
        size = Size(pos.width, pos.height),
        color = color.color,
    )
}

fun DrawScope.drawCornerArcLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -90F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width * 2, pos.height * 2),
        color = color.color,
    )
}

fun DrawScope.drawCornerArcLeftSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -90F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width / 2), pos.top + (pos.height / 2)),
        size = Size(pos.width, pos.height),
        color = color.color,
    )
}

fun DrawScope.drawCornerArcRightInverse(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - pos.width, pos.top - pos.height),
        size = Size(pos.width * 2, pos.height * 2),
        color = color.color,
    )
}

fun DrawScope.drawCornerArcRightInverseSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left - (pos.width / 2), pos.top - (pos.height / 2)),
        size = Size(pos.width, pos.height),
        color = color.color,
    )
}

fun DrawScope.drawCornerArcLeftInverse(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = 90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top - pos.height),
        size = Size(pos.width * 2, pos.height * 2),
        color = color.color,
    )
}

fun DrawScope.drawCornerArcLeftInverseSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = 90F,
        sweepAngle = 90F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width / 2), pos.top - (pos.height / 2)),
        size = Size(pos.width, pos.height),
        color = color.color,
    )
}

fun DrawScope.drawArcRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = -180F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width / 2), pos.top),
        size = Size(pos.width, pos.height),
        color = color.color,
    )
}

fun DrawScope.drawArcTop(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -180F,
        sweepAngle = -180F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top - (pos.height / 2)),
        size = Size(pos.width, pos.height),
        color = color.color,
    )
}

fun DrawScope.drawArcLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -90F,
        sweepAngle = 180F,
        useCenter = true,
        topLeft = Offset(pos.left - (pos.width / 2), pos.top),
        size = Size(pos.width, pos.height),
        color = color.color,
    )
}

fun DrawScope.drawArcBottom(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = -180F,
        sweepAngle = 180F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top + (pos.height / 2)),
        size = Size(pos.width, pos.height),
        color = color.color,
    )
}
