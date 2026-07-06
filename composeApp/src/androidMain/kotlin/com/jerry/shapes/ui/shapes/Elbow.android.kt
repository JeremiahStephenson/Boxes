package com.jerry.shapes.ui.shapes

import android.graphics.Paint
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.platform.CanvasExport
import android.graphics.Path

actual fun CanvasExport.drawBottomLeftElbow(
    pos: Rect,
    color: ColorAndShape
) {
    drawPath(
        Path().apply {
            moveTo(pos.left, pos.top + (pos.height * 0.4F))
            lineTo(pos.left + (pos.width * 0.6F), pos.top + (pos.height * 0.4F))
            lineTo(pos.left + (pos.width * 0.6F), pos.top + pos.height)
            lineTo(pos.left + (pos.width * 0.4F), pos.top + pos.height)
            lineTo(pos.left + (pos.width * 0.4F), pos.top + (pos.height * 0.6F))
            lineTo(pos.left, pos.top + (pos.height * 0.6F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawBottomRightElbow(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left + (pos.width * 0.4F), pos.top + (pos.height * 0.4F))
            lineTo(pos.left + pos.width, pos.top + (pos.height * 0.4F))
            lineTo(pos.left + pos.width, pos.top + (pos.height * 0.6F))
            lineTo(pos.left + (pos.width * 0.6F), pos.top + (pos.height * 0.6F))
            lineTo(pos.left + (pos.width * 0.6F), pos.top + pos.height)
            lineTo(pos.left + (pos.width * 0.4F), pos.top + pos.height)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTopLeftElbow(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left + (pos.width * 0.4F), pos.top)
            lineTo(pos.left + (pos.width * 0.6F), pos.top)
            lineTo(pos.left + (pos.width * 0.6F), pos.top + (pos.height * 0.6F))
            lineTo(pos.left, pos.top + (pos.height * 0.6F))
            lineTo(pos.left, pos.top + (pos.height * 0.4F))
            lineTo(pos.left + (pos.width * 0.4F), pos.top + (pos.height * 0.4F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTopRightElbow(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left + (pos.width * 0.4F), pos.top)
            lineTo(pos.left + (pos.width * 0.6F), pos.top)
            lineTo(pos.left + (pos.width * 0.6F), pos.top + (pos.height * 0.4F))
            lineTo(pos.left + pos.width, pos.top + (pos.height * 0.4F))
            lineTo(pos.left + pos.width, pos.top + (pos.height * 0.6F))
            lineTo(pos.left + (pos.width * 0.4F), pos.top + (pos.height * 0.6F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}