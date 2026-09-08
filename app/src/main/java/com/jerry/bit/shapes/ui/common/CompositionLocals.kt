package com.jerry.bit.shapes.ui.common

import androidx.compose.runtime.compositionLocalOf

data class FloatButtonProperties(
    val onClick: () -> Unit,
)

val LocalFloatingActionBarButton = compositionLocalOf<(FloatButtonProperties?) -> Unit> { {} }
