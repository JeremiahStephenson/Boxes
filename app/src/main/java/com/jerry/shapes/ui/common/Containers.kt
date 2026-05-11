package com.jerry.shapes.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

@Composable
fun DefaultContainer(
    title: String? = null,
    fabListener: (() -> Unit)? = null,
    disableAppbarScroll: Boolean = false,
    appBarActions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    LocalAppBarTitle.current(title.orEmpty() to disableAppbarScroll)
    LocalFloatingActionBarButton.current(
        when (fabListener) {
            null -> null
            else -> FloatButtonProperties(fabListener)
        },
    )
    LocalAppBarActions.current(appBarActions)
    content()
}
