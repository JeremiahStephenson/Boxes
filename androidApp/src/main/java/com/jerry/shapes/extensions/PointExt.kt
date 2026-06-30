package com.jerry.shapes.extensions

import android.graphics.Point
import com.jerry.shapes.cache.data.HistoryItem
import com.jerry.shapes.ui.boxes.state.enums.Direction
import com.jerry.shapes.util.QUADRANT_SIZE
import kotlin.math.floor

fun Point.isNotOutside(
    columns: Int,
    rows: Int,
) = x >= 0 && x <= (columns - 1) && y >= 0 && y <= (rows - 1)

fun Point.adjust(direction: Direction): Point =
    when (direction) {
        Direction.LEFT -> Point(this.x - 1, y)
        Direction.RIGHT -> Point(x + 1, y)
        Direction.UP -> Point(x, y - 1)
        Direction.DOWN -> Point(x, y + 1)
    }

fun Point.adjustInPlace(direction: Direction): Point =
    when (direction) {
        Direction.LEFT -> apply { set(x - 1, y) }
        Direction.RIGHT -> apply { set(x + 1, y) }
        Direction.UP -> apply { set(x, y - 1) }
        Direction.DOWN -> apply { set(x, y + 1) }
    }

val Point.quadrant
    get() =
        Point(
            floor(x.toFloat() / QUADRANT_SIZE).toInt(),
            floor(y.toFloat() / QUADRANT_SIZE).toInt(),
        )

val HistoryItem.quadrant
    get() =
        Point(
            floor(x.toFloat() / QUADRANT_SIZE).toInt(),
            floor(y.toFloat() / QUADRANT_SIZE).toInt(),
        )

val List<HistoryItem>.quadrants
    get() = groupBy { it.quadrant }

fun Point.quadrantName(layerId: Long): String {
    val quadrant = quadrant
    return "${layerId}_${quadrant.x}_${quadrant.y}"
}

val Set<Point>.groupByQuadrant
    get() =
        groupBy {
            Point(
                floor(it.x.toFloat() / QUADRANT_SIZE).toInt(),
                floor(it.y.toFloat() / QUADRANT_SIZE).toInt(),
            )
        }

val Set<Point>.quadrants
    get() =
        map {
            Point(
                floor(it.x.toFloat() / QUADRANT_SIZE).toInt(),
                floor(it.y.toFloat() / QUADRANT_SIZE).toInt(),
            )
        }.distinct()
