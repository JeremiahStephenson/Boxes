package com.jerry.shapes.ui.shapes

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.platform.CanvasExport

actual fun CanvasExport.drawBottomLeftToTopRightLine(
    pos: Rect,
    color: ColorAndShape
) {
    drawPath(
        Path().apply {
            moveTo(pos.left + (pos.width * 0.75F), pos.top)
            lineTo(pos.left + pos.width, pos.top)
            lineTo(pos.left + pos.width, pos.top + (pos.height * 0.25F))
            lineTo(pos.left + (pos.width * 0.25F), pos.top + pos.height)
            lineTo(pos.left, pos.top + pos.height)
            lineTo(pos.left, pos.top + (pos.height * 0.75F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTopLeftToBottomRightLine(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width * 0.25F), pos.top)
            lineTo(pos.left + pos.width, pos.top + (pos.height * 0.75F))
            lineTo(pos.left + pos.width, pos.top + pos.height)
            lineTo(pos.left + (pos.width * 0.75F), pos.top + pos.height)
            lineTo(pos.left, pos.top + (pos.height * 0.25F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawVerticalLine(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left + (pos.width * 0.4F), pos.top, pos.right - (pos.width * 0.4F), pos.bottom),
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawHorizontalLine(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left, pos.top + (pos.height * 0.4F), pos.right, pos.bottom - (pos.height * 0.4F)),
        Paint().apply { this.color = color.color.toArgb() },
    )
}