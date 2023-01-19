package com.jerry.boxes.ui.boxes.state

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jerry.boxes.ui.boxes.state.enums.TapType
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
class ButtonsState(
    private val eraserSelected: Boolean = false,
    private val selectToolSelected: Boolean = false
) : Parcelable {

    @IgnoredOnParcel
    var eraserSelectedState by mutableStateOf(eraserSelected)
        private set

    @IgnoredOnParcel
    var selectToolSelectedState by mutableStateOf(selectToolSelected)
        private set

    @IgnoredOnParcel
    var tapTypeState by mutableStateOf(TapType.TAP)
        private set

    fun toggleEraserSelected() {
        eraserSelectedState = !eraserSelectedState
        selectToolSelectedState = false
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