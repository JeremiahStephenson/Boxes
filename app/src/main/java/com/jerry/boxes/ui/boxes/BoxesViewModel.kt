package com.jerry.boxes.ui.boxes

import android.graphics.Point
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jerry.boxes.util.SavedHandle

class BoxesViewModel(
    handle: SavedStateHandle
) : ViewModel() {

    var boxes by SavedHandle<HashMap<Point, SerializableColor?>>(handle, "TESTING", HashMap())

}