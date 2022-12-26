package com.jerry.boxes.ui.common

import androidx.compose.runtime.compositionLocalOf

data class FloatButtonProperties(
    val onClick: () -> Unit
)

val LocalFloatingActionBarButton = compositionLocalOf<(FloatButtonProperties?) -> Unit> { {} }
val LocalAppBarTitle = compositionLocalOf<(String) -> Unit> { {} }