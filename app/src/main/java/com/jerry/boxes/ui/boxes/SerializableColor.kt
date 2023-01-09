package com.jerry.boxes.ui.boxes

import androidx.compose.ui.graphics.Color
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.ui.boxes.shapes.Shape
import java.io.Serializable

data class SerializableColor(
    // from = 0.0, to = 360.0
    val hue: Float,

    // from = 0.0, to = 1.0
    val saturation: Float,

    // from = 0.0, to = 1.0
    val value: Float,

    // from = 0.0, to = 1.0
    val alpha: Float,

    val shape: Shape = Shape.Box
) : Serializable {

    @Transient
    private var _color: Color? = null
    val color: Color get() =
        _color ?: HsvColor(hue, saturation, value, alpha).toColor().also { _color = it }
}