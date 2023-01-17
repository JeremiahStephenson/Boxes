package com.jerry.boxes.ui.boxes.state

import android.graphics.Point
import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jerry.boxes.extensions.adjust
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
class SelectionState(
    private val topLeft: Point? = null,
    private val bottomRight: Point? = null
) : Parcelable {

    @IgnoredOnParcel
    var topLeftState by mutableStateOf(topLeft)
        private set

    @IgnoredOnParcel
    var bottomRightState by mutableStateOf(bottomRight)
        private set

    fun setTopLeft(point: Point?) {
        topLeftState = point
    }

    fun setBottomRight(point: Point?) {
        bottomRightState = point
    }

    fun clear() {
        topLeftState = null
        bottomRightState = null
    }

    fun move(direction: Direction) {
        topLeftState = topLeftState?.adjust(direction)
        bottomRightState = bottomRightState?.adjust(direction)
    }
}