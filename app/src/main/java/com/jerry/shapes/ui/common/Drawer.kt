package com.jerry.shapes.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContainer(
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        drawerShape = RoundedCornerShape(topStart = 16.dp)
                    ) {
                        drawerContent()
                    }
                }
            },
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    content()
                }
            }
        )
    }
}

@Composable
fun ModalDrawerSheet(
    modifier: Modifier = Modifier,
    drawerShape: Shape = DrawerDefaults.shape,
    drawerContainerColor: Color = MaterialTheme.colorScheme.surface,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.ModalDrawerElevation,
    windowInsets: WindowInsets = DrawerDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit
) {
    val padding = with(LocalDensity.current) {
        WindowInsets.navigationBars.getRight(this, LayoutDirection.Ltr).toDp()
    }
    Row(
        modifier = Modifier
            .sizeIn(
                minWidth = DRAWER_MIN_WIDTH + padding,
                maxWidth = DRAWER_MAX_WIDTH + padding
            )
            .fillMaxHeight()
            .windowInsetsPadding(windowInsets)
    ) {
        Spacer(modifier = Modifier.size(padding))
        DrawerSheet(
            windowInsets,
            modifier,
            drawerShape,
            drawerContainerColor,
            drawerContentColor,
            drawerTonalElevation,
            content
        )
    }
}

@ExperimentalMaterial3Api
@Composable
private fun DrawerSheet(
    windowInsets: WindowInsets,
    modifier: Modifier = Modifier,
    drawerShape: Shape = RectangleShape,
    drawerContainerColor: Color = MaterialTheme.colorScheme.surface,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.PermanentDrawerElevation,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .sizeIn(
                minWidth = DRAWER_MIN_WIDTH,
                maxWidth = DRAWER_MAX_WIDTH
            )
            .fillMaxHeight(),
        shape = drawerShape,
        color = drawerContainerColor,
        contentColor = drawerContentColor,
        tonalElevation = drawerTonalElevation
    ) {
        Column(
            Modifier
                .sizeIn(
                    minWidth = DRAWER_MIN_WIDTH,
                    maxWidth = DRAWER_MAX_WIDTH
                )
                .windowInsetsPadding(windowInsets),
            content = content
        )
    }
}

private val DRAWER_MIN_WIDTH = 200.dp
private val DRAWER_MAX_WIDTH = 275.dp
