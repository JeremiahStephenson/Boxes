package com.jerry.bit.shapes.ui.boxes.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.listSaver
import com.jerry.bit.shapes.ui.boxes.state.enums.ActiveTool
import com.jerry.bit.shapes.ui.boxes.state.enums.TapType
import com.jerry.bit.shapes.util.StateValue

@Stable
class ButtonsState(
    private val eraserSelected: Boolean = false,
    private val selectToolSelected: Boolean = false,
    private val tapType: TapType = TapType.TAP,
) {
    var activeToolState by
        StateValue(
            when {
                selectToolSelected -> ActiveTool.SELECT
                eraserSelected -> ActiveTool.ERASER
                tapType == TapType.PICKER -> ActiveTool.EYEDROPPER
                tapType == TapType.FILL -> ActiveTool.FILL
                else -> ActiveTool.DRAW
            },
        )
        private set

    var eraserSelectedState: Boolean
        get() = activeToolState == ActiveTool.ERASER
        set(selected) {
            if (selected) {
                activeToolState = ActiveTool.ERASER
            } else if (eraserSelectedState) {
                activeToolState = ActiveTool.DRAW
            }
        }

    var selectToolSelectedState: Boolean
        get() = activeToolState == ActiveTool.SELECT
        set(selected) {
            if (selected) {
                activeToolState = ActiveTool.SELECT
            } else if (selectToolSelectedState) {
                activeToolState = ActiveTool.DRAW
            }
        }

    val tapTypeState: TapType
        get() =
            when (activeToolState) {
                ActiveTool.EYEDROPPER -> TapType.PICKER
                ActiveTool.FILL -> TapType.FILL
                else -> TapType.TAP
            }

    fun toggleEraserSelected() {
        activeToolState =
            if (activeToolState == ActiveTool.ERASER) ActiveTool.DRAW else ActiveTool.ERASER
    }

    fun toggleSelectTool() {
        activeToolState =
            if (activeToolState == ActiveTool.SELECT) ActiveTool.DRAW else ActiveTool.SELECT
    }

    fun setTapType(tapType: TapType) {
        activeToolState =
            when (tapType) {
                TapType.TAP -> ActiveTool.DRAW
                TapType.PICKER -> ActiveTool.EYEDROPPER
                TapType.FILL -> ActiveTool.FILL
            }
    }

    companion object {
        val SAVER =
            listSaver<ButtonsState, Any>(
                save = { item -> listOf(item.activeToolState) },
                restore = { state ->
                    val activeTool = state[0] as ActiveTool
                    ButtonsState(
                        eraserSelected = activeTool == ActiveTool.ERASER,
                        selectToolSelected = activeTool == ActiveTool.SELECT,
                        tapType =
                            when (activeTool) {
                                ActiveTool.EYEDROPPER -> TapType.PICKER
                                ActiveTool.FILL -> TapType.FILL
                                else -> TapType.TAP
                            },
                    )
                },
            )
    }
}
