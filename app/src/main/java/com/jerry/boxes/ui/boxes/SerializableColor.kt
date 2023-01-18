package com.jerry.boxes.ui.boxes

import androidx.compose.ui.graphics.Color
import com.jerry.boxes.ui.shapes.Shape
import java.io.Serializable

data class SerializableColor(
    val colorArgb: Int,
    val shape: Shape = Shape.Box
) : Serializable {

    @Transient
    private var _color: Color? = null
    val color: Color get() =
        _color ?: Color(colorArgb)
}