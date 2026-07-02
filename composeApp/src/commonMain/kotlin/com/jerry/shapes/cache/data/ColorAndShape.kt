package com.jerry.shapes.cache.data

import com.jerry.shapes.util.Parcelable
import androidx.compose.ui.graphics.Color
import com.jerry.shapes.ui.shapes.Shape
import kotlinx.parcelize.IgnoredOnParcel
import com.jerry.shapes.util.Parcelize

data class ColorAndShape(
    val colorValue: ULong,
    val shape: Shape = Shape.Box,
) {
    constructor(
        colorValue: Int,
        shape: Shape = Shape.Box,
    ) : this(Color(colorValue).value, shape)

    @IgnoredOnParcel
    val color = Color(colorValue)
}
