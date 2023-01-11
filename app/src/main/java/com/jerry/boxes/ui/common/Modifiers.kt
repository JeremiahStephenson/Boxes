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
import com.jerry.boxes.ui.boxes.pngBackground
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
        pngBackground(size)
    }
}
