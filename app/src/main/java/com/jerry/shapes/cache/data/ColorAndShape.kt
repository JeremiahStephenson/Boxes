package com.jerry.shapes.cache.data

import androidx.compose.ui.graphics.Color
import com.jerry.shapes.ui.shapes.Shape
import java.io.Serializable

data class ColorAndShape(
    val color: Color,
    val shape: Shape = Shape.Box
) : Serializable
