package com.jerry.shapes.ui.shapes

import android.graphics.Paint
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.platform.CanvasExport

actual fun CanvasExport.drawTriangleLeft(
    pos: Rect,
    color: ColorAndShape
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.right, pos.top + (pos.height / 2))
            lineTo(pos.left, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.right, pos.top)
            lineTo(pos.left, pos.top + (pos.height / 2))
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleTop(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width / 2), pos.bottom)
            lineTo(pos.right, pos.top)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleBottom(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left, pos.bottom)
            lineTo(pos.left + (pos.width / 2), pos.top)
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}