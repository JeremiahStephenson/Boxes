package com.jerry.boxes.ui.boxes.state

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.extensions.asSerializableColor
import com.jerry.boxes.ui.boxes.SerializableColor
import com.jerry.boxes.ui.boxes.shapes.Shape
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
class ColorAndShapeState(
    val color: Int?,
    val shape: Shape?
) : Parcelable {
    @IgnoredOnParcel
    var colorState by mutableStateOf(
        HsvColor.from(
            Color(color ?: Color.Green.toArgb())
        ).asSerializableColor
    )
        private set

    @IgnoredOnParcel
    var shapeState by mutableStateOf(shape ?: Shape.Box)
        private set

    fun setColor(color: SerializableColor) {
        colorState = color
    }

    fun setShape(shape: Shape) {
        shapeState = shape
    }
}