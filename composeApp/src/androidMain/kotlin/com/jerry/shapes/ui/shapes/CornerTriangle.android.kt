package com.jerry.shapes.ui.shapes

import android.graphics.Paint
import android.graphics.Path
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.platform.CanvasExport

actual fun CanvasExport.drawTriangleBottomLeft(
    pos: Rect,
    color: ColorAndShape
) {
    drawPath(
        Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.right, pos.bottom)
            lineTo(pos.left, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleBottomRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left, pos.bottom)
            lineTo(pos.right, pos.top)
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleTopLeft(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.right, pos.top)
            lineTo(pos.left, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleTopRight(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.right, pos.top)
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleBottomLeftSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left, pos.top + (pos.height / 2))
            lineTo(pos.left + (pos.width / 2), pos.bottom)
            lineTo(pos.left, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleBottomRightSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left + (pos.width / 2), pos.bottom)
            lineTo(pos.right, pos.top + (pos.height / 2))
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleTopLeftSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width / 2), pos.top)
            lineTo(pos.left, pos.top + (pos.height / 2))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleTopRightSmall(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left + (pos.width / 2), pos.top)
            lineTo(pos.right, pos.top)
            lineTo(pos.right, pos.top + (pos.height / 2))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleBottomLeftSmallest(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left, pos.bottom - (pos.height / 4))
            lineTo(pos.left + (pos.width / 4), pos.bottom)
            lineTo(pos.left, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleBottomRightSmallest(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.right - (pos.width / 4), pos.bottom)
            lineTo(pos.right, pos.bottom - (pos.height / 4))
            lineTo(pos.right, pos.bottom)
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleTopLeftSmallest(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.left, pos.top)
            lineTo(pos.left + (pos.width / 4), pos.top)
            lineTo(pos.left, pos.top + (pos.height / 4))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawTriangleTopRightSmallest(
    pos: Rect,
    color: ColorAndShape,
) {
    drawPath(
        Path().apply {
            moveTo(pos.right - (pos.width / 4), pos.top)
            lineTo(pos.right, pos.top)
            lineTo(pos.right, pos.top + (pos.height / 4))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}
