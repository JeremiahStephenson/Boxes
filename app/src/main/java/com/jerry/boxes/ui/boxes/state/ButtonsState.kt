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
    private val showGrid: Boolean,
    private val colorPickerOn: Boolean
) : Parcelable {

    @IgnoredOnParcel
    var eraserSelectedState by mutableStateOf(eraserSelected)
        private set

    @IgnoredOnParcel
    var showPngBackgroundState by mutableStateOf(showPngBackground)
        private set

    @IgnoredOnParcel
    var showGridState by mutableStateOf(showGrid)
        private set

    @IgnoredOnParcel
    var colorPickerOnState by mutableStateOf(colorPickerOn)
        private set

    fun toggleEraserSelected() {
        eraserSelectedState = !eraserSelectedState
    }

    fun toggleShowPngBackground() {
        showPngBackgroundState = !showPngBackgroundState
    }

    fun toggleGrid() {
        showGridState = !showGridState
    }

    fun turnOffEraser() {
        eraserSelectedState = false
    }

    fun turnOnOrOffColorPicker(on: Boolean) {
        colorPickerOnState = on
    }

    fun toggleColorPicker() {
        colorPickerOnState = !colorPickerOnState
    }
}