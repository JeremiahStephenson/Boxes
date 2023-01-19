package com.jerry.boxes.extensions

import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.ui.boxes.ColorAndShape

val HsvColor.asColorAndShape get() =
    ColorAndShape(this.toColor())