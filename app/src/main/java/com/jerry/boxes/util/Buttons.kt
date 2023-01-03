package com.jerry.boxes.util

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jerry.boxes.R

@Composable
fun IconMenuButton(
    onClick: () -> Unit,
    @DrawableRes drawableRes: Int
) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .padding(16.dp),
        painter = painterResource(drawableRes),
        contentDescription = null
    )
}

@Composable
fun IconSelectableMenuButton(
    onClick: () -> Unit,
    isSelected: () -> Boolean,
    @DrawableRes drawableRes: Int
) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .run {
                when (isSelected()) {
                    true -> background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3F))
                    else -> this
                }
            }
            .padding(16.dp),
        painter = painterResource(drawableRes),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface
    )
}