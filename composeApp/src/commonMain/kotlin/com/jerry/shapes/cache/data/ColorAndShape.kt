package com.jerry.shapes.cache.data

import androidx.compose.ui.graphics.Color
import com.jerry.shapes.ui.shapes.Shape

data class ColorAndShape(
    val colorValue: ULong,
    val shape: Shape = Shape.Box,
) {
    constructor(
        colorValue: Int,
        shape: Shape = Shape.Box,
    ) : this(Color(colorValue).value, shape)

    val color = Color(colorValue)
}
