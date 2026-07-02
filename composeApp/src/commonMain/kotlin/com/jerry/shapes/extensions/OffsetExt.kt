package com.jerry.shapes.extensions

import com.jerry.shapes.util.Point
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
    topLeft: Rect,
): Point? {
    val width = topLeft.width
    val point = convert(scale, offset, size)
    val p = Point(
        floor((point.x - topLeft.left) / width).toInt(),
        floor((point.y - topLeft.top) / width).toInt(),
    )
    return if (p.isNotOutside(columns, rows)) p else null
}

fun HashSet<Offset>.findBoxes(
    scale: Float,
    offset: Offset,
    size: Size,
    columns: Int,
    rows: Int,
    topLeft: Rect,
): HashSet<Point> {
    val width = topLeft.width
    return mapNotNull { p ->
        val convertedPoint = p.convert(scale, offset, size)
        val boxPoint = Point(
            floor((convertedPoint.x - topLeft.left) / width).toInt(),
            floor((convertedPoint.y - topLeft.top) / width).toInt(),
        )
        if (boxPoint.isNotOutside(columns, rows)) boxPoint else null
    }.distinct().toHashSet()
}
