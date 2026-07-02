package com.jerry.shapes.ui.boxes.history

import com.jerry.shapes.util.Point
import com.jerry.shapes.cache.data.ColorAndShape

data class UserHistory(
    val layerId: Long,
    val points: Map<Point, ColorAndShape?>,
)
