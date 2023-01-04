package com.jerry.boxes.ui.boxes

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset

fun generateBoxes(
    numX: Int,
    numY: Int,
    size: Float,
    xOffSet: Int,
    yOffSet: Int
) = mutableMapOf<Point, RectF>().apply {
    for (y in 0 until numY) {
        for (x in 0 until numX) {
            val topLeft = Offset(
                (size * x) + xOffSet,
                (size * y) + yOffSet
            )
            put(
                Point(x, y),
                RectF(
                    topLeft.x,
                    topLeft.y,
                    (topLeft.x + size),
                    (topLeft.y + size)
                )
            )
        }
    }
}