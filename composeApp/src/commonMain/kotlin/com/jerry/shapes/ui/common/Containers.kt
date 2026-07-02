package com.jerry.shapes.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
fun DefaultContainer(
    title: String? = null,
    fabListener: (() -> Unit)? = null,
    disableAppbarScroll: Boolean = false,
    appBarActions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val localAppBarTitle = LocalAppBarTitle.current
    val localFloatingActionBarButton = LocalFloatingActionBarButton.current
    val localAppBarActions = LocalAppBarActions.current

    val setScaffoldValues = {
        localAppBarTitle(title.orEmpty() to disableAppbarScroll)
        localFloatingActionBarButton(
            when (fabListener) {
                null -> null
                else -> FloatButtonProperties(fabListener)
            },
        )
        localAppBarActions(appBarActions)
    }

    var hasPresented by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasPresented) {
            setScaffoldValues()
            hasPresented = true
        }
    }

    LifecycleResumeEffect(Unit) {
        setScaffoldValues()
        onPauseOrDispose { }
    }

    content()
}
