package com.jerry.boxes.ui.common

import androidx.compose.runtime.Composable

@Composable
fun DefaultContainer(
    title: String? = null,
    fabListener: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    LocalAppBarTitle.current(title.orEmpty())
    LocalFloatingActionBarButton.current(
        when (fabListener) {
            null -> null
            else -> FloatButtonProperties(fabListener)
        }
    )
    content()
}