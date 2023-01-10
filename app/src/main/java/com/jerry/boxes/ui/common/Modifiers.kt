package com.jerry.boxes.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

fun Modifier.unboundClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false),
        onClick = onClick
    )
}

fun Modifier.pngBackground(
    visible: Boolean,
    size: Float
): Modifier {
    if (!visible) return this
    return clipToBounds().drawBehind {
        val columns = (this.size.width / size).roundToInt()
        val rows = (this.size.height / size).roundToInt()
        for (r in 0..rows) {
            for (c in 0..columns) {
                drawRect(
                    color = Color.Gray,
                    topLeft = Offset(c * size, r * size),
                    size = Size(size, size),
                    alpha = when (r % 2 == 0) {
                        true -> when (c % 2 == 0) {
                            true -> 1F
                            else -> GRID_ODD_ALPHA
                        }
                        else -> when (c % 2 == 0) {
                            true -> GRID_ODD_ALPHA
                            else -> 1F
                        }
                    }
                )
            }
        }
    }
}

private const val GRID_ODD_ALPHA = 0.5F