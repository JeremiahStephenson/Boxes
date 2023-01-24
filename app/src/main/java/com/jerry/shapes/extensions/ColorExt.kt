package com.jerry.shapes.extensions

import com.godaddy.android.colorpicker.HsvColor
import com.jerry.shapes.ui.boxes.data.ColorAndShape

val HsvColor.asColorAndShape get() =
    ColorAndShape(this.toColor())