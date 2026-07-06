package com.jerry.shapes.ui.shapes

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.util.CanvasExport

fun DrawScope.drawBottomLeftElbow(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left, pos.top + (pos.height * 0.4F))
                lineTo(pos.left + (pos.width * 0.6F), pos.top + (pos.height * 0.4F))
                lineTo(pos.left + (pos.width * 0.6F), pos.top + pos.height)
                lineTo(pos.left + (pos.width * 0.4F), pos.top + pos.height)
                lineTo(pos.left + (pos.width * 0.4F), pos.top + (pos.height * 0.6F))
                lineTo(pos.left, pos.top + (pos.height * 0.6F))
                close()
            },
        color = color.color,
    )
}

expect fun CanvasExport.drawBottomLeftElbow(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawBottomRightElbow(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width * 0.4F), pos.top + (pos.height * 0.4F))
                lineTo(pos.left + pos.width, pos.top + (pos.height * 0.4F))
                lineTo(pos.left + pos.width, pos.top + (pos.height * 0.6F))
                lineTo(pos.left + (pos.width * 0.6F), pos.top + (pos.height * 0.6F))
                lineTo(pos.left + (pos.width * 0.6F), pos.top + pos.height)
                lineTo(pos.left + (pos.width * 0.4F), pos.top + pos.height)
                close()
            },
        color = color.color,
    )
}

expect fun CanvasExport.drawBottomRightElbow(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawTopLeftElbow(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width * 0.4F), pos.top)
                lineTo(pos.left + (pos.width * 0.6F), pos.top)
                lineTo(pos.left + (pos.width * 0.6F), pos.top + (pos.height * 0.6F))
                lineTo(pos.left, pos.top + (pos.height * 0.6F))
                lineTo(pos.left, pos.top + (pos.height * 0.4F))
                lineTo(pos.left + (pos.width * 0.4F), pos.top + (pos.height * 0.4F))
                close()
            },
        color = color.color,
    )
}

expect fun CanvasExport.drawTopLeftElbow(
    pos: Rect,
    color: ColorAndShape,
)

fun DrawScope.drawTopRightElbow(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        path =
            Path().apply {
                moveTo(pos.left + (pos.width * 0.4F), pos.top)
                lineTo(pos.left + (pos.width * 0.6F), pos.top)
                lineTo(pos.left + (pos.width * 0.6F), pos.top + (pos.height * 0.4F))
                lineTo(pos.left + pos.width, pos.top + (pos.height * 0.4F))
                lineTo(pos.left + pos.width, pos.top + (pos.height * 0.6F))
                lineTo(pos.left + (pos.width * 0.4F), pos.top + (pos.height * 0.6F))
                close()
            },
        color = color.color,
    )
}

expect fun CanvasExport.drawTopRightElbow(
    pos: Rect,
    color: ColorAndShape,
)
