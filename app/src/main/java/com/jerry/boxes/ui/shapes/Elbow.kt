package com.jerry.boxes.ui.shapes

import android.graphics.RectF
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.boxes.ui.boxes.ColorAndShape

fun DrawScope.drawBottomLeftElbow(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left, pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left, pos.top + (pos.height() * 0.6F))
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawBottomRightElbow(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + pos.height())
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + pos.height())
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawTopLeftElbow(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() * 0.4F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left, pos.top + (pos.height() * 0.6F))
            lineTo(pos.left, pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.4F))
            close()
        },
        color = color.color
    )
}

fun DrawScope.drawTopRightElbow(
    pos: RectF,
    color: ColorAndShape
) {
    drawPath(
        path = Path().apply {
            moveTo(pos.left + (pos.width() * 0.4F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top)
            lineTo(pos.left + (pos.width() * 0.6F), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.4F))
            lineTo(pos.left + pos.width(), pos.top + (pos.height() * 0.6F))
            lineTo(pos.left + (pos.width() * 0.4F), pos.top + (pos.height() * 0.6F))
            close()
        },
        color = color.color
    )
}