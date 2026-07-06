package com.jerry.shapes.ui.shapes

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.withRotation
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.util.CanvasExport
import com.jerry.shapes.util.expectPlatformCanvas
import androidx.compose.ui.graphics.Color

actual fun CanvasExport.drawBox(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawRect(pos.toAndroidRectF(), Paint().apply { this.color = color.color.toArgb() })
}

actual fun CanvasExport.drawLegoSquare(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawRect(
        RectF(
            pos.left + (pos.width * 0.01F),
            pos.top + (pos.height * 0.01F),
            pos.right - (pos.width * 0.02F),
            pos.bottom - (pos.height * 0.02F),
        ),
        Paint().apply { this.color = color.color.toArgb() },
    )
    withRotation(
        50F,
        pos.left + (pos.width / 2),
        pos.top + (pos.height / 2),
    ) {
        drawOval(
            RectF(
                pos.left + (pos.width * 0.2F),
                pos.top + (pos.height * 0.21F),
                pos.right - (pos.width * 0.10F),
                pos.bottom - (pos.height * 0.21F),
            ),
            Paint().apply {
                shader =
                    LinearGradientShader(
                        colors = listOf(Color.White, Color.Black.copy(alpha = 0.3F)),
                        from = Offset(
                            pos.left + (pos.width * 0.2F),
                            pos.top + (pos.height / 2F)
                        ),
                        to = Offset(pos.left + (pos.width * 0.6F), pos.top + (pos.height / 2F)),
                    )
                alpha = (255 * color.color.alpha).toInt()
            },
        )
    }
    drawArc(
        RectF(
            pos.left + (pos.width * 0.22F),
            pos.top + (pos.height * 0.22F),
            pos.right - (pos.width * 0.22F),
            pos.bottom - (pos.height * 0.22F),
        ),
        0F,
        360F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawLegoRound(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        RectF(
            pos.left + (pos.width * 0.01F),
            pos.top + (pos.height * 0.01F),
            pos.right - (pos.width * 0.02F),
            pos.bottom - (pos.height * 0.02F),
        ),
        0F,
        360F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
    withRotation(
        50F,
        pos.left + (pos.width / 2),
        pos.top + (pos.height / 2),
    ) {
        drawOval(
            RectF(
                pos.left + (pos.width * 0.2F),
                pos.top + (pos.height * 0.21F),
                pos.right - (pos.width * 0.10F),
                pos.bottom - (pos.height * 0.21F),
            ),
            Paint().apply {
                shader =
                    LinearGradientShader(
                        colors = listOf(Color.White, Color.Black.copy(alpha = 0.3F)),
                        from = Offset(
                            pos.left + (pos.width * 0.2F),
                            pos.top + (pos.height / 2F)
                        ),
                        to = Offset(pos.left + (pos.width * 0.6F), pos.top + (pos.height / 2F)),
                    )
                alpha = (255 * color.color.alpha).toInt()
            },
        )
    }
    drawArc(
        RectF(
            pos.left + (pos.width * 0.22F),
            pos.top + (pos.height * 0.22F),
            pos.right - (pos.width * 0.22F),
            pos.bottom - (pos.height * 0.22F),
        ),
        0F,
        360F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawCircle(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawArc(
        pos.toAndroidRectF(),
        0F,
        360F,
        true,
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawStar(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawPath(
        android.graphics.Path().apply {
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
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawDiamond(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left + (pos.width / 2), pos.top)
            lineTo(pos.left + pos.width, pos.top + (pos.height / 2))
            lineTo(pos.left + (pos.width / 2), pos.top + pos.height)
            lineTo(pos.left, pos.top + (pos.height / 2))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawRoundedBox(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawRoundRect(
        pos.toAndroidRectF(),
        pos.width * 0.3F,
        pos.width * 0.3F,
        Paint().apply { this.color = color.color.toArgb() })
}

actual fun CanvasExport.drawOctagon(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    val sides = (pos.width - (pos.width * 0.414F)) / 2F
    drawPath(
        android.graphics.Path().apply {
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
        Paint().apply { this.color = color.color.toArgb() },
    )
}

actual fun CanvasExport.drawHeart(
    pos: Rect,
    color: ColorAndShape,
) = expectPlatformCanvas {
    drawPath(
        android.graphics.Path().apply {
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
        },
        Paint().apply { this.color = color.color.toArgb() },
    )
}