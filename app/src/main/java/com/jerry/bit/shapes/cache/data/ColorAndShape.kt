package com.jerry.bit.shapes.cache.data

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import com.jerry.bit.shapes.ui.shapes.Shape
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorAndShape(
    val colorValue: ULong,
    val shape: Shape = Shape.Box,
) : Parcelable {
    constructor(
        colorValue: Int,
        shape: Shape = Shape.Box,
    ) : this(Color(colorValue).value, shape)

    @IgnoredOnParcel
    val color = Color(colorValue)
}
