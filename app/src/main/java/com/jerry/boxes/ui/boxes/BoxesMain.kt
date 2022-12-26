package com.jerry.boxes.ui.boxes

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.R
import com.jerry.boxes.ui.common.LocalAppBarTitle
import com.jerry.boxes.ui.common.unboundClickable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun BoxesMain(
    navController: DestinationsNavigator
) {
    LocalAppBarTitle.current("Boxes")

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        val numX = 10
        val numY = 20

        val density = LocalDensity.current

        val spacing = remember {
            with(density) {
                5.dp.roundToPx()
            }
        }

        val stroke = remember {
            Stroke(with(density) {
                2.dp.roundToPx()
            }.toFloat())
        }

        val radius = remember {
            with(density) {
                5.dp.roundToPx()
            }.toFloat()
        }

        val buttonBarOffset = remember {
            with(density) {
                56.dp.roundToPx()
            }
        }

        val size = this.constraints
        val width =
            remember { (size.maxWidth - (spacing * (numX + 1))) / numX.toFloat() }
        val height =
            remember { ((size.maxHeight - buttonBarOffset) - (spacing * (numY + 1))) / numY.toFloat() }

        var currentColor by remember { mutableStateOf(Color.Green) }

        val keys = remember {
            mutableStateMapOf<Rect, Color?>().apply {
                for (y in 0 until numY) {
                    for (x in 0 until numX) {
                        val topLeft = Offset(
                            ((x + 1) * spacing) + (width * x),
                            (((y + 1) * spacing) + (height * y)) + buttonBarOffset
                        )
                        put(
                            Rect(
                                topLeft.x.toInt(),
                                topLeft.y.toInt(),
                                (topLeft.x + width).toInt(),
                                (topLeft.y + height).toInt()
                            ),
                            null
                        )
                    }
                }
            }
        }

        var eraserSelected by remember { mutableStateOf(false) }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        keys.keys
                            .find { key ->
                                offset.x >= key.left && offset.x <= key.right &&
                                        offset.y >= key.top && offset.y <= key.bottom
                            }
                            ?.let {
                                val current = keys[it]
                                keys[it] = when (current == currentColor) {
                                    true -> null
                                    else -> currentColor
                                }
                            }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        val offset = change.position
                        keys.keys
                            .find { key ->
                                offset.x >= key.left && offset.x <= key.right &&
                                        offset.y >= key.top && offset.y <= key.bottom
                            }
                            ?.let {
                                keys[it] = when (eraserSelected) {
                                    true -> null
                                    else -> currentColor
                                }
                            }
                    }
                }
        ) {

            keys.forEach { key ->

                val style = when (key.value) {
                    null -> stroke
                    else -> Fill
                }

                drawRoundRect(
                    cornerRadius = CornerRadius(radius),
                    style = style,
                    topLeft = Offset(key.key.left.toFloat(), key.key.top.toFloat()),
                    size = Size(width, height),
                    color = when (key.value) {
                        null -> Color.Gray
                        else -> key.value ?: Color.Transparent
                    }
                )

            }
        }

        ButtonBar(
            color = currentColor,
            eraserSelected = eraserSelected,
            onClearClick = {
                keys
                    .filter { it.value != null }
                    .forEach {
                        keys[it.key] = null
                    }
            },
            onEraserClick = {
                eraserSelected = !eraserSelected
            },
            onColorChosen = {
                currentColor = it
            }
        )
    }
}

@Composable
private fun ButtonBar(
    color: Color,
    eraserSelected: Boolean,
    onClearClick: () -> Unit,
    onEraserClick: () -> Unit,
    onColorChosen: (Color) -> Unit
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onClearClick()
                }
                .padding(16.dp),
            painter = painterResource(R.drawable.ic_auto_renew),
            contentDescription = null
        )

        Spacer(modifier = Modifier.weight(1F))

        var colorPicker by remember { mutableStateOf(false) }
        if (colorPicker) {
            ColorPickerDialog(
                color = color,
                onColorChosen = onColorChosen) {
                colorPicker = false
            }
        }

        Box(
            modifier = Modifier
                .unboundClickable {
                    colorPicker = true
                }
                .padding(16.dp)
                .size(26.dp)
                .background(color)
        )

        Spacer(modifier = Modifier.weight(1F))

        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onEraserClick()
                }
                .run {
                    when (eraserSelected) {
                        true -> background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3F))
                        else -> this
                    }
                }
                .padding(16.dp),
            painter = painterResource(R.drawable.ic_eraser),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ColorPickerDialog(
    color: Color,
    onColorChosen: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        var currentColor by remember(color) { mutableStateOf(color) }
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClassicColorPicker(
                showAlphaBar = false,
                color = color,
                modifier = Modifier.fillMaxHeight(0.8F),
                onColorChanged = { color: HsvColor ->
                    currentColor = color.toColor()
                }
            )
            Button(
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                onClick = {
                    onColorChosen(currentColor)
                    onDismiss()
                }) {
                Text(text = "Set Color")
            }
            OutlinedButton(
                modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                onClick = onDismiss) {
                Text(text = "Close")
            }
        }
    }
}