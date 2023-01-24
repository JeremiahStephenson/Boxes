package com.jerry.boxes.ui.common

import android.graphics.RectF
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import com.jerry.boxes.R
import com.jerry.boxes.ui.boxes.data.ColorAndShape
import com.jerry.boxes.ui.boxes.drawCustomShape
import com.jerry.boxes.ui.shapes.Shape
import com.jerry.boxes.util.TooltipPositionProvider
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconMenuButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    color: ColorAndShape? = null,
    padding: PaddingValues = PaddingValues(16.dp),
    contentDescription: String,
    allowTooltip: Boolean = true,
    @DrawableRes drawableRes: Int
) {
    Box {
        var popupControl by remember { mutableStateOf(false) }
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .run {
                    when (enabled) {
                        true -> combinedClickable(
                            onClick = { onClick() },
                            onLongClick = {
                                if (allowTooltip) {
                                    popupControl = true
                                }
                            }
                        )
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
            contentDescription = contentDescription
        )
        if (popupControl) {
            ToolTip(text = contentDescription) { popupControl = false }
        }
    }
}

@Composable
fun IconSelectableMenuButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isSelected: () -> Boolean,
    isEnabled: () -> Boolean = { true },
    contentDescription: String,
    tint: Color? = null,
    @DrawableRes drawableResOn: Int,
    @DrawableRes drawableResOff: Int? = null
) {
    Box {
        var popupControl by remember { mutableStateOf(false) }
        Icon(
            modifier = modifier
                .run {
                    when (isEnabled()) {
                        true -> unboundClickable(
                            onClick = { onClick() },
                            onLongClick = { popupControl = true }
                        )
                        else -> this
                    }
                }
                .padding(4.dp)
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
                .padding(12.dp),
            painter = when (drawableResOff) {
                null -> painterResource(drawableResOn)
                else -> painterResource(
                    when (isSelected()) {
                        true -> drawableResOff
                        else -> drawableResOn
                    }
                )
            },
            contentDescription = contentDescription,
            tint = tint ?: when (isEnabled()) {
                true -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4F)
            }
        )
        if (popupControl) {
            ToolTip(text = contentDescription) { popupControl = false }
        }
    }
}

@Composable
fun ShapeOption(
    modifier: Modifier = Modifier,
    shape: Shape,
    color: ColorAndShape? = null,
    showToolTip: Boolean = false,
    shapeSize: Dp = 26.dp,
    onClick: () -> Unit
) {
    Box(modifier) {
        var popupControl by remember { mutableStateOf(false) }
        val onSurface = MaterialTheme.colorScheme.onSurface
        val shapeColor = (color ?: ColorAndShape(onSurface)).copy(shape = shape)
        val size = with(LocalDensity.current) { (shapeSize - 2.dp).toPx() }
        val stroke = with(LocalDensity.current) { 1.dp.toPx() }
        Canvas(
            modifier = Modifier
                .unboundClickable(
                    onClick = { onClick() },
                    onLongClick = { if (showToolTip) popupControl = true }
                )
                .padding(16.dp)
                .size(shapeSize)
                .then(modifier)
        ) {
            drawCustomShape(
                RectF(0F, 0F, size, size),
                shapeColor
            )
            drawRect(
                color = onSurface.copy(alpha = 0.4F),
                style = Stroke(stroke),
                size = Size(size, size)
            )
        }
        if (popupControl) {
            ToolTip(text = stringResource(R.string.select_shape)) {
                popupControl = false
            }
        }
    }
}

@Composable
private fun ToolTip(
    text: String,
    onDismiss: () -> Unit
) {
    val offsetY = with(LocalDensity.current) { 16.dp.roundToPx() }
    Popup(
        popupPositionProvider = TooltipPositionProvider(offsetY),
        onDismissRequest = onDismiss
    ) {
        LaunchedEffect(Unit) {
            delay(5000L)
            onDismiss()
        }
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp)
        ) {
            Text(text = text)
        }
    }
}
