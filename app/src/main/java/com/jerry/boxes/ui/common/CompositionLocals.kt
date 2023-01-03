package com.jerry.boxes.ui.common

import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

data class FloatButtonProperties(
    val onClick: () -> Unit
)

val LocalFloatingActionBarButton = compositionLocalOf<(FloatButtonProperties?) -> Unit> { {} }
val LocalAppBarTitle = compositionLocalOf<(String) -> Unit> { {} }
val LocalContentOffset = compositionLocalOf<State<Float>> { mutableStateOf(0F) }