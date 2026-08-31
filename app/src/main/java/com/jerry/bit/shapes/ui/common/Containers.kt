package com.jerry.bit.shapes.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.jerry.bit.shapes.Toolbar
import kotlin.text.orEmpty

@Composable
fun DefaultContainer(
    title: String? = null,
    fabListener: (() -> Unit)? = null,
    appBarActions: (@Composable RowScope.() -> Unit)? = null,
    showBackArrow: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    content: @Composable () -> Unit,
) {
    val localFloatingActionBarButton = LocalFloatingActionBarButton.current
    val titleRemembered by rememberUpdatedState(title)

    val setScaffoldValues = {
        localFloatingActionBarButton(
            when (fabListener) {
                null -> null
                else -> FloatButtonProperties(fabListener)
            },
        )
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

    TopBarLayout(
        topBar = {
            Toolbar(
                scrollBehavior = scrollBehavior,
                showBackArrow = showBackArrow,
                getTitle = { titleRemembered.orEmpty() },
                actions = { appBarActions ?: {} },
            )
        },
        content = content,
    )
}
