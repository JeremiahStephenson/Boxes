package com.jerry.boxes.ui.boxes

import androidx.compose.ui.graphics.Color
import com.jerry.boxes.ui.shapes.Shape
import java.io.Serializable

data class ColorAndShape(
    val color: Color,
    val shape: Shape = Shape.Box
) : Serializable