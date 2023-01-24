package com.jerry.shapes.ui.boxes.data

import com.jerry.shapes.util.ExportType

data class Export(
    val filePath: String?,
    val error: String?,
    val exportType: ExportType
)
