package com.jerry.boxes.extensions

import androidx.compose.ui.graphics.toArgb
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.ui.boxes.SerializableColor

val HsvColor.asSerializableColor get() =
    SerializableColor(this.toColor().toArgb())