package com.jerry.shapes.ui.boxes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class BoxesNavKey(
    val projectId: Long,
    val projectName: String?,
) : NavKey
