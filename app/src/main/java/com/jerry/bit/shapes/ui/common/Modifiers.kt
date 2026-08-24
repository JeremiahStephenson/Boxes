package com.jerry.bit.shapes.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import com.jerry.bit.shapes.util.pngBackground

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.unboundClickable(
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
): Modifier =
    composed {
        combinedClickable(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = false),
            onClick = onClick,
            onLongClick = onLongClick,
        )
    }

fun Modifier.pngBackground(
    visible: Boolean,
    size: Float,
): Modifier {
    if (!visible) return this
    return clipToBounds().drawBehind {
        pngBackground(size)
    }
}
