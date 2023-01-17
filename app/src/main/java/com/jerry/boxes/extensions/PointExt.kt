package com.jerry.boxes.extensions

import android.graphics.Point
import com.jerry.boxes.ui.boxes.state.Direction

fun Point.isNotOutside(columns: Int, rows: Int) =
    x >= 0 && x <= (columns - 1) && y >= 0 && y <= (rows - 1)

fun Point.adjust(direction: Direction): Point {
    return when (direction) {
        Direction.LEFT -> Point(this.x - 1, y)
        Direction.RIGHT -> Point(x + 1, y)
        Direction.UP -> Point(x, y - 1)
        Direction.DOWN -> Point(x, y + 1)
    }
}

fun Point.adjustInPlace(direction: Direction): Point {
    return when (direction) {
        Direction.LEFT -> apply { set(x - 1, y) }
        Direction.RIGHT -> apply { set(x + 1, y) }
        Direction.UP -> apply { set(x, y - 1) }
        Direction.DOWN -> apply { set(x, y + 1) }
    }
}