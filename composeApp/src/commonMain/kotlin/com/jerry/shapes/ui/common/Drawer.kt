@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.jerry.shapes.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContainer(
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheetContainer {
                drawerContent()
            }
        },
        content = content,
    )
}

@Composable
private fun DrawerSheetContainer(
    modifier: Modifier = Modifier,
    drawerShape: Shape = DrawerDefaults.shape,
    drawerContainerColor: Color = DrawerDefaults.modalContainerColor,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.ModalDrawerElevation,
    windowInsets: WindowInsets = DrawerDefaults.windowInsets,
    content: @Composable () -> Unit,
) {
    val padding =
        with(LocalDensity.current) {
            WindowInsets.navigationBars.getRight(this, LayoutDirection.Ltr).toDp()
        }
    Row(
        modifier =
            Modifier
                .sizeIn(
                    minWidth = DRAWER_MIN_WIDTH + padding,
                    maxWidth = DRAWER_MAX_WIDTH + padding,
                ).fillMaxHeight()
                .windowInsetsPadding(windowInsets),
    ) {
        Spacer(modifier = Modifier.size(padding))
        DrawerSheet(
            modifier = modifier,
            drawerShape = drawerShape,
            drawerContainerColor = drawerContainerColor,
            drawerContentColor = drawerContentColor,
            drawerTonalElevation = drawerTonalElevation,
            content = content,
        )
    }
}

@Composable
private fun DrawerSheet(
    modifier: Modifier,
    drawerShape: Shape,
    drawerContainerColor: Color,
    drawerContentColor: Color,
    drawerTonalElevation: Dp,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier =
            modifier
                .sizeIn(
                    minWidth = DRAWER_MIN_WIDTH,
                    maxWidth = DRAWER_MAX_WIDTH,
                ).fillMaxHeight(),
        shape = drawerShape,
        color = drawerContainerColor,
        contentColor = drawerContentColor,
        tonalElevation = drawerTonalElevation,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            content()
        }
    }
}

private val DRAWER_MIN_WIDTH = 240.dp
private val DRAWER_MAX_WIDTH = 320.dp
