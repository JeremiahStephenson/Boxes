package com.jerry.shapes.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

data class FloatButtonProperties(
    val onClick: () -> Unit,
)

val LocalFloatingActionBarButton = compositionLocalOf<(FloatButtonProperties?) -> Unit> { {} }
val LocalAppBarTitle = compositionLocalOf<(Pair<String, Boolean>) -> Unit> { {} }
val LocalAppBarHeight = compositionLocalOf<State<Float>> { mutableStateOf(0F) }
val LocalAppBarActions = compositionLocalOf<((@Composable RowScope.() -> Unit)?) -> Unit> { {} }
