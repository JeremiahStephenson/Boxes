package com.jerry.shapes.ui.shapes

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.platform.CanvasExport

actual fun CanvasExport.drawRectangleLeft(
    pos: Rect,
    color: ColorAndShape
) {
    drawRect(
        RectF(pos.left, pos.top, pos.right - (pos.width / 2), pos.bottom),
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawRectangleRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left + (pos.width / 2), pos.top, pos.right, pos.bottom),
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawRectangleTop(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left, pos.top, pos.right, pos.top + (pos.height / 2)),
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawRectangleBottom(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left, pos.top + (pos.height / 2), pos.right, pos.bottom),
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawBoxTopRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left + (pos.width / 2), pos.top, pos.right, pos.top + (pos.height / 2)),
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawBoxTopLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left, pos.top, pos.right - (pos.width / 2), pos.top + (pos.height / 2)),
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawBoxBottomRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left + (pos.width / 2), pos.top + (pos.height / 2), pos.right, pos.bottom),
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawBoxBottomLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawRect(
        RectF(pos.left, pos.top + (pos.height / 2), pos.left + (pos.height / 2), pos.bottom),
        Paint().apply { this.color = color.color.toArgb() },
    )
}