package com.jerry.boxes.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jerry.boxes.ui.boxes.SerializableColor

@Composable
fun IconMenuButton(
    onClick: () -> Unit,
    color: SerializableColor? = null,
    padding: PaddingValues = PaddingValues(16.dp),
    @DrawableRes drawableRes: Int
) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .padding(padding),
        painter = painterResource(drawableRes),
        tint = color?.color ?: LocalContentColor.current,
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
                true -> drawableResOff
                else -> drawableResOn
            }
        ),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface
    )
}