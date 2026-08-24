package com.jerry.bit.shapes.ui.layers

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class LayersEditNavKey(
    val projectId: Long,
) : NavKey
