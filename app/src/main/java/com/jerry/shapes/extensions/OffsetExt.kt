package com.jerry.shapes.extensions

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
    boxes: Map<Point, RectF>,
): Point? =
    boxes[Point(0, 0)]?.let {
        val point = convert(scale, offset, size)
        with(it.width()) {
            Point(
                floor((point.x - it.left) / this).toInt(),
                floor((point.y - it.top) / this).toInt(),
            ).run {
                when (this.isNotOutside(columns, rows)) {
                    true -> this
                    else -> null
                }
            }
        }
    }

fun HashSet<Offset>.findBoxes(
    scale: Float,
    offset: Offset,
    size: Size,
    columns: Int,
    rows: Int,
    boxes: Map<Point, RectF>,
): HashSet<Point> =
    boxes[Point(0, 0)]?.let {
        with(it.width()) {
            mapNotNull { p ->
                val point = p.convert(scale, offset, size)
                Point(
                    floor((point.x - it.left) / this).toInt(),
                    floor((point.y - it.top) / this).toInt(),
                ).run {
                    when (this.isNotOutside(columns, rows)) {
                        true -> this
                        else -> null
                    }
                }
            }
        }.distinct().toHashSet()
    } ?: HashSet()
