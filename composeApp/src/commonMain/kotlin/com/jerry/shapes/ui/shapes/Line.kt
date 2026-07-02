package com.jerry.shapes.ui.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawBottomLeftToTopRightLine(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width * 0.75F), pos.top)
                lineTo(pos.left + pos.width, pos.top)
                lineTo(pos.left + pos.width, pos.top + (pos.height * 0.25F))
                lineTo(pos.left + (pos.width * 0.25F), pos.top + pos.height)
                lineTo(pos.left, pos.top + pos.height)
                lineTo(pos.left, pos.top + (pos.height * 0.75F))
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawTopLeftToBottomRightLine(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top)
                lineTo(pos.left + (pos.width * 0.25F), pos.top)
                lineTo(pos.left + pos.width, pos.top + (pos.height * 0.75F))
                lineTo(pos.left + pos.width, pos.top + pos.height)
                lineTo(pos.left + (pos.width * 0.75F), pos.top + pos.height)
                lineTo(pos.left, pos.top + (pos.height * 0.25F))
                close()
            },
        color = color.color,
    )
}

fun DrawScope.drawVerticalLine(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width * 0.4F), pos.top),
        size = Size(pos.width * 0.2F, pos.height),
        color = color.color,
    )
}

fun DrawScope.drawHorizontalLine(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top + (pos.height * 0.4F)),
        size = Size(pos.width, pos.height * 0.2F),
        color = color.color,
    )
}
