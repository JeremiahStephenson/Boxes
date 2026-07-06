package com.jerry.shapes.ui.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.platform.CanvasExport

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

expect fun CanvasExport.drawCornerArcRight(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawCornerArcRightSmall(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawCornerArcLeft(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawCornerArcLeftSmall(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawCornerArcRightInverse(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawCornerArcRightInverseSmall(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawCornerArcLeftInverse(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawCornerArcLeftInverseSmall(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawArcRight(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawArcTop(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawArcLeft(
    pos: Rect,
    color: ColorAndShape,
)

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

expect fun CanvasExport.drawArcBottom(
    pos: Rect,
    color: ColorAndShape,
)
