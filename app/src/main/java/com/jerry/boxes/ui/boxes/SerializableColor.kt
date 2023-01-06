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
    val hsvColor: HsvColor = HsvColor(hue, saturation, value, alpha)

    @Transient
    val color: Color = hsvColor.toColor()
}