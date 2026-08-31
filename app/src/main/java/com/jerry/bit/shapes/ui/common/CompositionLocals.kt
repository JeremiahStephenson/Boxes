package com.jerry.bit.shapes.ui.common

import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableFloatStateOf

data class FloatButtonProperties(
    val onClick: () -> Unit,
)

val LocalFloatingActionBarButton = compositionLocalOf<(FloatButtonProperties?) -> Unit> { {} }
val LocalAppBarHeight = compositionLocalOf<State<Float>> { mutableFloatStateOf(0F) }
