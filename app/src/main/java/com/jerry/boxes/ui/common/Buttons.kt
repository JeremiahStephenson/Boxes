package com.jerry.boxes.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jerry.boxes.ui.boxes.ColorAndShape

@Composable
fun IconMenuButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    color: ColorAndShape? = null,
    padding: PaddingValues = PaddingValues(16.dp),
    @DrawableRes drawableRes: Int
) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .run {
                when (enabled) {
                    true -> clickable {
                        onClick()
                    }
                    else -> this
                }
            }
            .padding(padding)
            .then(modifier),
        painter = painterResource(drawableRes),
        tint = (color?.color ?: LocalContentColor.current).run {
            copy(
                alpha = when (enabled) {
                    true -> this.alpha
                    else -> 0.3F
                }
            )
        },
        contentDescription = null
    )
}

@Composable
fun IconSelectableMenuButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isSelected: () -> Boolean,
    isEnabled: () -> Boolean = { true },
    @DrawableRes drawableResOn: Int,
    @DrawableRes drawableResOff: Int? = null,
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .run {
                when (drawableResOff) {
                    null -> when (isSelected()) {
                        true -> background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4F))
                        else -> this
                    }
                    else -> this
                }
            }
            .run {
                when (isEnabled()) {
                    true -> unboundClickable {
                        onClick()
                    }
                    else -> this
                }
            }
            .padding(16.dp),
        painter = when (drawableResOff) {
            null -> painterResource(drawableResOn)
            else -> painterResource(
                when (isSelected()) {
                    true -> drawableResOff
                    else -> drawableResOn
                }
            )
        },
        contentDescription = null,
        tint = when (isEnabled()) {
            true -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4F)
        }
    )
}