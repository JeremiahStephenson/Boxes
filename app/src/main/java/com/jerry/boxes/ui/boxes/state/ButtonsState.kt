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
    private val selectToolSelected: Boolean
) : Parcelable {

    @IgnoredOnParcel
    var eraserSelectedState by mutableStateOf(eraserSelected)
        private set

    @IgnoredOnParcel
    var selectToolSelectedState by mutableStateOf(selectToolSelected)
        private set

    @IgnoredOnParcel
    var showPngBackgroundState by mutableStateOf(showPngBackground)
        private set

    @IgnoredOnParcel
    var showGridState by mutableStateOf(showGrid)
        private set

    @IgnoredOnParcel
    var tapTypeState by mutableStateOf(TapType.TAP)
        private set

    fun toggleEraserSelected() {
        eraserSelectedState = !eraserSelectedState
        selectToolSelectedState = false
    }

    fun toggleShowPngBackground() {
        showPngBackgroundState = !showPngBackgroundState
    }

    fun toggleGrid() {
        showGridState = !showGridState
    }

    fun toggleSelectTool() {
        selectToolSelectedState = !selectToolSelectedState
        eraserSelectedState = false
    }

    fun turnOffEraser() {
        eraserSelectedState = false
    }

    fun turnOffSelectionTool() {
        selectToolSelectedState = false
    }

    fun setTapType(tapType: TapType) {
        tapTypeState = when (tapTypeState == tapType) {
            true -> TapType.TAP
            else -> tapType
        }
    }

    fun alternateTapType() {
        val newType = when (tapTypeState) {
            TapType.TAP -> TapType.PICKER
            TapType.PICKER -> TapType.FILL
            else -> TapType.TAP
        }
        setTapType(newType)
    }
}