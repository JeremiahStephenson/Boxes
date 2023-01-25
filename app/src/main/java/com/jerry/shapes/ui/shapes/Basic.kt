package com.jerry.shapes.ui.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.core.graphics.withRotation
import com.jerry.shapes.cache.data.ColorAndShape

fun DrawScope.drawBox(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

fun Canvas.drawBox(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(pos, Paint().apply { this.color = color.color.toArgb() })
}

fun DrawScope.drawLegoSquare(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        style = Fill,
        topLeft = Offset(pos.left + (pos.width() * 0.01F), pos.top + (pos.height() * 0.01F)),
        size = Size(pos.width() - (pos.width() * 0.02F), pos.height() - (pos.height() * 0.02F)),
        color = color.color
    )
    rotate(
        degrees = 50F,
        pivot = Offset(pos.left + (pos.width() / 2), pos.top + (pos.height() / 2))
    ) {
        drawOval(
            topLeft = Offset(pos.left + (pos.width() * 0.2F), pos.top + (pos.height() * 0.21F)),
            size = Size(pos.width() * 0.7F, pos.height() * 0.58F),
            brush = Brush.linearGradient(
                colors = listOf(Color.White, Color.Black.copy(alpha = 0.3F)),
                start = Offset(pos.left + (pos.width() * 0.2F), pos.top + (pos.height() / 2F)),
                end = Offset(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() / 2F))
            ),
            alpha = color.color.alpha
        )
    }
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width() * 0.22F), pos.top + (pos.height() * 0.22F)),
        size = Size(pos.width() * 0.56F, pos.height() * 0.56F),
        color = color.color
    )
}

fun Canvas.drawLegoSquare(
    pos: RectF,
    color: ColorAndShape
) {
    drawRect(
        RectF(
            pos.left + (pos.width() * 0.01F),
            pos.top + (pos.height() * 0.01F),
            pos.right - (pos.width() * 0.02F),
            pos.bottom - (pos.height() * 0.02F)
        ),
        Paint().apply { this.color = color.color.toArgb() }
    )
    withRotation(
        50F,
        pos.left + (pos.width() / 2),
        pos.top + (pos.height() / 2)
    ) {
        drawOval(
            RectF(
                pos.left + (pos.width() * 0.2F),
                pos.top + (pos.height() * 0.21F),
                pos.right - (pos.width() * 0.10F),
                pos.bottom - (pos.height() * 0.21F)
            ),
            Paint().apply {
                shader = LinearGradientShader(
                    colors = listOf(Color.White, Color.Black.copy(alpha = 0.3F)),
                    from = Offset(pos.left + (pos.width() * 0.2F), pos.top + (pos.height() / 2F)),
                    to = Offset(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() / 2F))
                )
                alpha = (255 * color.color.alpha).toInt()
            }
        )
    }
    drawArc(
        RectF(
            pos.left + (pos.width() * 0.22F),
            pos.top + (pos.height() * 0.22F),
            pos.right - (pos.width() * 0.22F),
            pos.bottom - (pos.height() * 0.22F)
        ),
        0F,
        360F,
        true,
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawLegoRound(
    pos: RectF,
    color: ColorAndShape
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width() * 0.01F), pos.top + (pos.height() * 0.01F)),
        size = Size(pos.width() - (pos.width() * 0.02F), pos.height() - (pos.height() * 0.02F)),
        color = color.color
    )
    rotate(
        degrees = 50F,
        pivot = Offset(pos.left + (pos.width() / 2), pos.top + (pos.height() / 2))
    ) {
        drawOval(
            topLeft = Offset(pos.left + (pos.width() * 0.2F), pos.top + (pos.height() * 0.21F)),
            size = Size(pos.width() * 0.7F, pos.height() * 0.58F),
            brush = Brush.linearGradient(
                colors = listOf(Color.White, Color.Black.copy(alpha = 0.3F)),
                start = Offset(pos.left + (pos.width() * 0.2F), pos.top + (pos.height() / 2F)),
                end = Offset(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() / 2F))
            ),
            alpha = color.color.alpha
        )
    }
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left + (pos.width() * 0.22F), pos.top + (pos.height() * 0.22F)),
        size = Size(pos.width() * 0.56F, pos.height() * 0.56F),
        color = color.color
    )
}

fun Canvas.drawLegoRound(
    pos: RectF,
    color: ColorAndShape
) {
    drawArc(
        RectF(
            pos.left + (pos.width() * 0.01F),
            pos.top + (pos.height() * 0.01F),
            pos.right - (pos.width() * 0.02F),
            pos.bottom - (pos.height() * 0.02F)
        ),
        0F,
        360F,
        true,
        Paint().apply { this.color = color.color.toArgb() }
    )
    withRotation(
        50F,
        pos.left + (pos.width() / 2),
        pos.top + (pos.height() / 2)
    ) {
        drawOval(
            RectF(
                pos.left + (pos.width() * 0.2F),
                pos.top + (pos.height() * 0.21F),
                pos.right - (pos.width() * 0.10F),
                pos.bottom - (pos.height() * 0.21F)
            ),
            Paint().apply {
                shader = LinearGradientShader(
                    colors = listOf(Color.White, Color.Black.copy(alpha = 0.3F)),
                    from = Offset(pos.left + (pos.width() * 0.2F), pos.top + (pos.height() / 2F)),
                    to = Offset(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() / 2F))
                )
                alpha = (255 * color.color.alpha).toInt()
            }
        )
    }
    drawArc(
        RectF(
            pos.left + (pos.width() * 0.22F),
            pos.top + (pos.height() * 0.22F),
            pos.right - (pos.width() * 0.22F),
            pos.bottom - (pos.height() * 0.22F)
        ),
        0F,
        360F,
        true,
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawCircle(
    pos: RectF,
    color: ColorAndShape
) {
    drawArc(
        startAngle = 0F,
        sweepAngle = 360F,
        useCenter = true,
        topLeft = Offset(pos.left, pos.top),
        size = Size(pos.width(), pos.height()),
        color = color.color
    )
}

fun Canvas.drawCircle(
    pos: RectF,
    color: ColorAndShape
) {
    drawArc(
        pos,
        0F,
        360F,
        true,
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawStar(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.left + (pos.width() * 0.63F), pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + (pos.width() * 0.72F), pos.top + (pos.height() * 0.61F))
            lineTo(pos.left + (pos.width() * 0.81F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() / 2), pos.top + (pos.height() * 0.76F))
            lineTo(pos.left + (pos.width() * 0.19F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.28F), pos.top + (pos.height() * 0.61F))
            lineTo(pos.left, pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + (pos.width() * 0.37F), pos.top + (pos.height() * 0.38F))
            close()
        },
        color = color.color
    )
}

fun Canvas.drawStar(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.left + (pos.width() * 0.63F), pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + (pos.width() * 0.72F), pos.top + (pos.height() * 0.61F))
            lineTo(pos.left + (pos.width() * 0.81F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() / 2), pos.top + (pos.height() * 0.76F))
            lineTo(pos.left + (pos.width() * 0.19F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.28F), pos.top + (pos.height() * 0.61F))
            lineTo(pos.left, pos.top + (pos.height() * 0.38F))
            lineTo(pos.left + (pos.width() * 0.37F), pos.top + (pos.height() * 0.38F))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() }
    )
}

fun DrawScope.drawDiamond(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.left + pos.width(), pos.top + (pos.height() / 2))
            lineTo(pos.left + (pos.width() / 2), pos.top + pos.height())
            lineTo(pos.left, pos.top + (pos.height() / 2))
            close()
        },
        color = color.color
    )
}

fun Canvas.drawDiamond(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        android.graphics.Path().apply {
            moveTo(pos.left + (pos.width() / 2), pos.top)
            lineTo(pos.left + pos.width(), pos.top + (pos.height() / 2))
            lineTo(pos.left + (pos.width() / 2), pos.top + pos.height())
            lineTo(pos.left, pos.top + (pos.height() / 2))
            close()
        },
        Paint().apply { this.color = color.color.toArgb() }
    )
}
