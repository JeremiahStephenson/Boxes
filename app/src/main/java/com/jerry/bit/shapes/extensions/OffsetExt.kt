package com.jerry.bit.shapes.extensions

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.floor

private fun Offset.convert(
    scale: Float,
    offset: Offset,
    size: Size,
): Offset {
    val centerX = ((size.width / 2F) - offset.x)
    val centerY = ((size.height / 2F) - offset.y)
    val point =
        Offset(((x - centerX) * (1F / scale)) + centerX, ((y - centerY) * (1F / scale)) + centerY)
    return point - (offset / scale)
}

fun Offset.findBox(
    scale: Float,
    offset: Offset,
    size: Size,
    columns: Int,
    rows: Int,
    topLeft: RectF,
): Point? =
    with(topLeft.width()) {
        val point = convert(scale, offset, size)
        Point(
            floor((point.x - topLeft.left) / this).toInt(),
            floor((point.y - topLeft.top) / this).toInt(),
        ).run {
            when (this.isNotOutside(columns, rows)) {
                true -> this
                else -> null
            }
        }
    }

fun HashSet<Offset>.findBoxes(
    scale: Float,
    offset: Offset,
    size: Size,
    columns: Int,
    rows: Int,
    topLeft: RectF,
): HashSet<Point> =
    with(topLeft.width()) {
        mapNotNull { p ->
            val point = p.convert(scale, offset, size)
            Point(
                floor((point.x - topLeft.left) / this).toInt(),
                floor((point.y - topLeft.top) / this).toInt(),
            ).run {
                when (this.isNotOutside(columns, rows)) {
                    true -> this
                    else -> null
                }
            }
        }
    }.distinct().toHashSet()
