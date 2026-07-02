package com.jerry.shapes.ui.common

import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.ui.shapes.Shape
import com.jerry.shapes.util.drawCustomShape
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import boxes.composeapp.generated.resources.Res
import boxes.composeapp.generated.resources.select_shape

@Composable
fun IconMenuButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    color: ColorAndShape? = null,
    padding: PaddingValues = PaddingValues(16.dp),
    contentDescription: String,
    allowTooltip: Boolean = true,
    drawableRes: DrawableResource,
) {
    IconMenuButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        color = color,
        padding = padding,
        contentDescription = contentDescription,
        allowTooltip = allowTooltip,
        painter = painterResource(drawableRes)
    )
}

@Composable
fun IconMenuButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    color: ColorAndShape? = null,
    padding: PaddingValues = PaddingValues(16.dp),
    contentDescription: String,
    allowTooltip: Boolean = true,
    painter: Painter,
) {
    Box {
        var popupControl by remember { mutableStateOf(false) }
        Icon(
            modifier =
                Modifier
                    .clip(CircleShape)
                    .run {
                        when (enabled) {
                            true ->
                                combinedClickable(
                                    onClick = { onClick() },
                                    onLongClick = {
                                        if (allowTooltip) {
                                            popupControl = true
                                        }
                                    },
                                )
                            else -> this
                        }
                    }.padding(padding)
                    .then(modifier),
            painter = painter,
            tint =
                (color?.color ?: LocalContentColor.current).run {
                    copy(
                        alpha =
                            when (enabled) {
                                true -> this.alpha
                                else -> 0.3F
                            },
                    )
                },
            contentDescription = contentDescription,
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
    drawableResOn: DrawableResource,
    drawableResOff: DrawableResource? = null,
) {
    Box {
        var popupControl by remember { mutableStateOf(false) }
        Icon(
            modifier =
                modifier
                    .run {
                        when (isEnabled()) {
                            true ->
                                unboundClickable(
                                    onClick = { onClick() },
                                    onLongClick = { popupControl = true },
                                )
                            else -> this
                        }
                    }.padding(4.dp)
                    .clip(CircleShape)
                    .run {
                        when (drawableResOff) {
                            null ->
                                when (isSelected()) {
                                    true -> background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4F))
                                    else -> this
                                }
                            else -> this
                        }
                    }.padding(12.dp),
            painter =
                when (drawableResOff) {
                    null -> painterResource(drawableResOn)
                    else ->
                        painterResource(
                            when (isSelected()) {
                                true -> drawableResOff
                                else -> drawableResOn
                            },
                        )
                },
            contentDescription = contentDescription,
            tint =
                tint ?: when (isEnabled()) {
                    true -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4F)
                },
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
    onClick: () -> Unit,
) {
    Box(modifier) {
        var popupControl by remember { mutableStateOf(false) }
        val onSurface = MaterialTheme.colorScheme.onSurface
        val shapeColor = (color ?: ColorAndShape(onSurface.value)).copy(shape = shape)
        val size = with(LocalDensity.current) { (shapeSize - 2.dp).toPx() }
        val stroke = with(LocalDensity.current) { 1.dp.toPx() }
        Canvas(
            modifier =
                Modifier
                    .unboundClickable(
                        onClick = { onClick() },
                        onLongClick = { if (showToolTip) popupControl = true },
                    ).padding(16.dp)
                    .size(shapeSize)
                    .then(modifier),
        ) {
            drawCustomShape(
                Rect(0F, 0F, size, size),
                shapeColor,
            )
            drawRect(
                color = onSurface.copy(alpha = 0.4F),
                style = Stroke(stroke),
                size = Size(size, size),
            )
        }
        if (popupControl) {
            ToolTip(text = stringResource(Res.string.select_shape)) {
                popupControl = false
            }
        }
    }
}

@Composable
private fun ToolTip(
    text: String,
    onDismiss: () -> Unit,
) {
    // Need a multiplatform position provider or simplified tooltip
    Popup(
        onDismissRequest = onDismiss,
    ) {
        LaunchedEffect(Unit) {
            delay(5.seconds)
            onDismiss()
        }
        Box(
            modifier =
                Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(16.dp),
        ) {
            Text(text = text)
        }
    }
}
