package com.jerry.boxes.util

import androidx.annotation.DrawableRes
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
import com.jerry.boxes.ui.common.unboundClickable

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
    @DrawableRes drawableResOn: Int,
    @DrawableRes drawableResOff: Int
) {
    Icon(
        modifier = Modifier
            .unboundClickable {
                onClick()
            }
            .padding(16.dp),
        painter = painterResource(
            when (isSelected()) {
                true -> drawableResOn
                else -> drawableResOff
            }
        ),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface
    )
}