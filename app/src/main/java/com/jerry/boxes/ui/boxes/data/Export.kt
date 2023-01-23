package com.jerry.boxes.ui.boxes.data

import com.jerry.boxes.util.ExportType

data class Export(
    val filePath: String?,
    val error: String?,
    val exportType: ExportType
)
