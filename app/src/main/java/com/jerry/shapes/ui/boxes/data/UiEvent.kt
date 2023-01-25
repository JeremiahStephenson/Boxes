package com.jerry.shapes.ui.boxes.data

import com.jerry.shapes.util.ExportType

sealed class UiEvent {
    data class Error(val error: String?) : UiEvent()
    data class Export(
        val filePath: String?,
        val exportType: ExportType
    ) : UiEvent()
}
