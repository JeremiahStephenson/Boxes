package com.jerry.shapes.ui.shapes

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.util.CanvasExport
import com.jerry.shapes.util.expectPlatformCanvas

actual fun CanvasExport.drawCornerArcRight(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(pos.left - pos.width, pos.top, pos.right, pos.bottom + pos.height),
        -90F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawCornerArcRightSmall(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(
            pos.left - (pos.width / 2),
            pos.top + (pos.height / 2),
            pos.right - (pos.width / 2),
            pos.bottom + (pos.height / 2),
        ),
        -90F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawCornerArcLeft(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(pos.left, pos.top, pos.right + pos.width, pos.bottom + pos.height),
        -90F,
        -90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawCornerArcLeftSmall(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(
            pos.left + (pos.width / 2),
            pos.top + (pos.height / 2),
            pos.right + (pos.width / 2),
            pos.bottom + (pos.height / 2),
        ),
        -90F,
        -90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawCornerArcRightInverse(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
     drawArc(
        RectF(pos.left - pos.width, pos.top - pos.height, pos.right, pos.bottom),
        0F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawCornerArcRightInverseSmall(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(
            pos.left - (pos.width / 2),
            pos.top - (pos.height / 2),
            pos.right - (pos.width / 2),
            pos.bottom - (pos.height / 2),
        ),
        0F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawCornerArcLeftInverse(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(pos.left, pos.top - pos.height, pos.right + pos.width, pos.bottom),
        90F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawCornerArcLeftInverseSmall(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(
            pos.left + (pos.width / 2),
            pos.top - (pos.height / 2),
            pos.right + (pos.width / 2),
            pos.bottom - (pos.height / 2),
        ),
        90F,
        90F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawArcRight(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(pos.left + (pos.width / 2), pos.top, pos.right + (pos.width / 2), pos.bottom),
        -90F,
        -180F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawArcTop(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(pos.left, pos.top - (pos.height / 2), pos.right, pos.bottom - (pos.height / 2)),
        -180F,
        -180F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawArcLeft(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(pos.left - (pos.width / 2), pos.top, pos.right - (pos.width / 2), pos.bottom),
        -90F,
        180F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawArcBottom(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(pos.left, pos.top + (pos.height / 2), pos.right, pos.bottom + (pos.height / 2)),
        -180F,
        180F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}
