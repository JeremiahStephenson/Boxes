package com.jerry.boxes.ui.boxes.state

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
class ButtonsState(
    private val eraserSelected: Boolean,
    private val showPngBackground: Boolean,
) : Parcelable {

    @IgnoredOnParcel
    var eraserSelectedState by mutableStateOf(eraserSelected)
        private set

    @IgnoredOnParcel
    var showPngBackgroundState by mutableStateOf(showPngBackground)
        private set

    fun toggleEraserSelected() {
        eraserSelectedState = !eraserSelectedState
    }

    fun toggleShowPngBackground() {
        showPngBackgroundState = !showPngBackgroundState
    }
}