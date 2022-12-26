package com.jerry.boxes.extensions

import androidx.compose.ui.graphics.Color
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.ui.boxes.SerializableColor

val HsvColor.asSerializableColor get() =
    SerializableColor(hue, saturation, value, alpha)

val Color.asSerializableColor get() =
    HsvColor.from(this).asSerializableColor