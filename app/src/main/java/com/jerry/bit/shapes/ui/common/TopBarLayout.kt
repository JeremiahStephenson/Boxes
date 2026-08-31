package com.jerry.bit.shapes.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout

@Composable
fun TopBarLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        // Subcompose and measure the top bar
        val topBarPlaceables =
            subcompose(slotId = "topBar", content = topBar).map {
                it.measure(constraints.copy(minHeight = 0))
            }

        // Calculate the height of the top bar
        val topBarHeight = topBarPlaceables.maxOfOrNull { it.height } ?: 0

        // Subcompose and measure the content with the remaining height
        val contentConstraints =
            constraints.copy(
                minHeight = 0,
                maxHeight = (constraints.maxHeight - topBarHeight).coerceAtLeast(0),
            )
        val contentPlaceables =
            subcompose(slotId = "content", content = content).map {
                it.measure(contentConstraints)
            }

        // Set the size of the layout and place the children
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place the top bar at the top
            topBarPlaceables.forEach { it.placeRelative(0, 0) }

            // Place the content below the top bar
            contentPlaceables.forEach { it.placeRelative(0, topBarHeight) }
        }
    }
}
