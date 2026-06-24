package com.jerry.shapes.ui.create

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CreateNavKey(
    val projectId: Long? = null,
) : NavKey
