package com.jerry.shapes.ui.boxes.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.listSaver
import com.jerry.shapes.ui.boxes.state.enums.TapType
import com.jerry.shapes.util.StateValue

@Stable
class ButtonsState(
    private val eraserSelected: Boolean = false,
    private val selectToolSelected: Boolean = false,
    private val tapType: TapType = TapType.TAP,
) {
    var eraserSelectedState by StateValue(eraserSelected)

    var selectToolSelectedState by StateValue(selectToolSelected)

    var tapTypeState by StateValue(tapType)
        private set

    fun toggleEraserSelected() {
        eraserSelectedState = !eraserSelectedState
        selectToolSelectedState = false
    }

    fun toggleSelectTool() {
        selectToolSelectedState = !selectToolSelectedState
        eraserSelectedState = false
    }

    fun setTapType(tapType: TapType) {
        tapTypeState =
            when (tapTypeState == tapType) {
                true -> TapType.TAP
                else -> tapType
            }
    }

    fun alternateTapType() {
        val newType =
            when (tapTypeState) {
                TapType.TAP -> TapType.PICKER
                TapType.PICKER -> TapType.FILL
                else -> TapType.TAP
            }
        setTapType(newType)
    }

    companion object {
        val SAVER =
            listSaver<ButtonsState, Any>(
                save = { item ->
                    listOf(item.eraserSelectedState, item.selectToolSelectedState, item.tapTypeState)
                },
                restore = { state ->
                    ButtonsState(
                        eraserSelected = state[0] as Boolean,
                        selectToolSelected = state[1] as Boolean,
                        tapType = state[2] as TapType,
                    )
                },
            )
    }
}
