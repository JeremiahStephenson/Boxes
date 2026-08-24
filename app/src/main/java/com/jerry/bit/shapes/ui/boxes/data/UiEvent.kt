package com.jerry.bit.shapes.ui.boxes.data

import com.jerry.bit.shapes.ui.boxes.state.enums.Direction
import com.jerry.bit.shapes.util.ExportType

sealed class UiEvent {
    data class MoveSelection(
        val direction: Direction,
    ) : UiEvent()

    data class Error(
        val error: String?,
    ) : UiEvent()

    data class Export(
        val filePath: String?,
        val exportType: ExportType,
    ) : UiEvent()
}
