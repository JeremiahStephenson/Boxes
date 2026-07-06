package com.jerry.shapes.ui.shapes

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.util.CanvasExport

fun DrawScope.drawBox(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width, pos.height),
        color = color.color,
    )
}

expect fun CanvasExport.drawBox(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawLegoSquare(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width * 0.01F), pos.top + (pos.height * 0.01F)),
        size = Size(pos.width - (pos.width * 0.02F), pos.height - (pos.height * 0.02F)),
        color = color.color,
    )
    rotate(
        degrees = 50F,
        pivot = Offset(pos.left + (pos.width / 2), pos.top + (pos.height / 2)),
    ) {
        drawOval(
            topLeft = Offset(pos.left + (pos.width * 0.2F), pos.top + (pos.height * 0.21F)),
            size = Size(pos.width * 0.7F, pos.height * 0.58F),
            brush =
                Brush.linearGradient(
                    colors = listOf(Color.White, Color.Black.copy(alpha = 0.3F)),
                    start = Offset(pos.left + (pos.width * 0.2F), pos.top + (pos.height / 2F)),
                    end = Offset(pos.left + (pos.width * 0.6F), pos.top + (pos.height / 2F)),
                ),
            alpha = color.color.alpha,
        )
    }
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width * 0.22F), pos.top + (pos.height * 0.22F)),
        size = Size(pos.width * 0.56F, pos.height * 0.56F),
        color = color.color,
    )
}

expect fun CanvasExport.drawLegoSquare(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawLegoRound(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width * 0.01F), pos.top + (pos.height * 0.01F)),
        size = Size(pos.width - (pos.width * 0.02F), pos.height - (pos.height * 0.02F)),
        color = color.color,
    )
    rotate(
        degrees = 50F,
        pivot = Offset(pos.left + (pos.width / 2), pos.top + (pos.height / 2)),
    ) {
        drawOval(
            topLeft = Offset(pos.left + (pos.width * 0.2F), pos.top + (pos.height * 0.21F)),
            size = Size(pos.width * 0.7F, pos.height * 0.58F),
            brush =
                Brush.linearGradient(
                    colors = listOf(Color.White, Color.Black.copy(alpha = 0.3F)),
                    start = Offset(pos.left + (pos.width * 0.2F), pos.top + (pos.height / 2F)),
                    end = Offset(pos.left + (pos.width * 0.6F), pos.top + (pos.height / 2F)),
                ),
            alpha = color.color.alpha,
        )
    }
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width * 0.22F), pos.top + (pos.height * 0.22F)),
        size = Size(pos.width * 0.56F, pos.height * 0.56F),
        color = color.color,
    )
}

expect fun CanvasExport.drawLegoRound(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawCircle(
    pos: Rect,
    color: ColorAndShape,
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width, pos.height),
        color = color.color,
    )
}

expect fun CanvasExport.drawCircle(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawStar(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width / 2), pos.top)
                lineTo(pos.left + (pos.width * 0.63F), pos.top + (pos.height * 0.38F))
                lineTo(pos.left + pos.width, pos.top + (pos.height * 0.38F))
                lineTo(pos.left + (pos.width * 0.72F), pos.top + (pos.height * 0.61F))
                lineTo(pos.left + (pos.width * 0.81F), pos.top + pos.height)
                lineTo(pos.left + (pos.width / 2), pos.top + (pos.height * 0.76F))
                lineTo(pos.left + (pos.width * 0.19F), pos.top + pos.height)
                lineTo(pos.left + (pos.width * 0.28F), pos.top + (pos.height * 0.61F))
                lineTo(pos.left, pos.top + (pos.height * 0.38F))
                lineTo(pos.left + (pos.width * 0.37F), pos.top + (pos.height * 0.38F))
                close()
            },
        color = color.color,
    )
}

expect fun CanvasExport.drawStar(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawDiamond(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width / 2), pos.top)
                lineTo(pos.left + pos.width, pos.top + (pos.height / 2))
                lineTo(pos.left + (pos.width / 2), pos.top + pos.height)
                lineTo(pos.left, pos.top + (pos.height / 2))
                close()
            },
        color = color.color,
    )
}

expect fun CanvasExport.drawDiamond(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawRoundedBox(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRoundRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width, pos.height),
        color = color.color,
        cornerRadius = CornerRadius(pos.width * 0.3F, pos.width * 0.3F),
    )
}

expect fun CanvasExport.drawRoundedBox(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawOctagon(
    pos: Rect,
    color: ColorAndShape,
) {
    val sides = (pos.width - (pos.width * 0.414F)) / 2F
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + sides, pos.top)
                lineTo(pos.right - sides, pos.top)
                lineTo(pos.right, pos.top + sides)
                lineTo(pos.right, pos.bottom - sides)
                lineTo(pos.right - sides, pos.bottom)
                lineTo(pos.left + sides, pos.bottom)
                lineTo(pos.left, pos.bottom - sides)
                lineTo(pos.left, pos.top + sides)
                close()
            },
        color = color.color,
    )
}

expect fun CanvasExport.drawOctagon(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawHeart(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width / 2), pos.bottom)
                cubicTo(
                    pos.left - (pos.width * 0.45F),
                    pos.top + (pos.height * 0.4F),
                    pos.left + (pos.width * 0.2F),
                    pos.top - (pos.height * 0.33F),
                    pos.left + (pos.width / 2),
                    pos.top + (pos.height * 0.17F),
                )
                moveTo(pos.left + (pos.width / 2), pos.bottom)
                cubicTo(
                    pos.right + (pos.width * 0.45F),
                    pos.top + (pos.height * 0.4F),
                    pos.right - (pos.width * 0.2F),
                    pos.top - (pos.height * 0.33F),
                    pos.right - (pos.width / 2),
                    pos.top + (pos.height * 0.17F),
                )
                close()
            },
        color = color.color,
    )
}

expect fun CanvasExport.drawHeart(
    pos: Rect,
    color: ColorAndShape,
)
