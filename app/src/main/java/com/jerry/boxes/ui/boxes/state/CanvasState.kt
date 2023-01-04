package com.jerry.boxes.ui.boxes.state

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import com.jerry.boxes.ui.boxes.SerializableColor

@Stable
class CanvasState {
    val boxes = mutableStateMapOf<Point, RectF>()
    val selections = mutableStateMapOf<Point, SerializableColor?>()
}