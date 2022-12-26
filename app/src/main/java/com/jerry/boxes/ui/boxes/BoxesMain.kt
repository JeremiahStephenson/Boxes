package com.jerry.boxes.ui.boxes

import android.graphics.Point
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.R
import com.jerry.boxes.extensions.asSerializableColor
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.unboundClickable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import kotlin.math.min

@Destination
@Composable
fun BoxesMain(
    projectId: Long,
    projectColumns: Int,
    projectRows: Int,
    navController: DestinationsNavigator,
    viewModel: BoxesViewModel = koinViewModel()
) {
    val project = viewModel.projectFlow.collectAsStateWithLifecycle()

    DefaultContainer(
        title = project.value?.project?.name
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

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

            val buttonBarOffset = remember {
                with(density) {
                    56.dp.roundToPx()
                }
            }

            var currentColor by rememberSaveable { mutableStateOf(Color.Green.asSerializableColor) }

            val size = this.constraints
            val min = remember {
                val maxWidth = (size.maxWidth - (spacing * (projectColumns + 1))) / projectColumns
                val maxHeight = (size.maxHeight - (spacing * (projectRows + 1))) / projectRows
                min(maxWidth, maxHeight).toFloat()
            }

            val boxes = rememberBoxes(
                numX = projectColumns,
                numY = projectRows,
                spacing = spacing,
                width = min,
                height = min,
                buttonBarOffset = buttonBarOffset
            )

            val selections = remember {
                mutableStateMapOf<Point, SerializableColor?>()
            }

            LaunchedEffect(project.value) {
                when (viewModel.boxes) {
                    null -> selections.putAll(project.value?.pixels?.associate {
                        Point(it.x, it.y) to
                                SerializableColor(it.hue, it.saturation, it.value, it.alpha)
                    } ?: emptyMap())
                    else -> selections.putAll(viewModel.boxes!!)
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    viewModel.boxes = HashMap(selections.toMap())
                }
            }

            var eraserSelected by remember { mutableStateOf(false) }

            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                scale *= zoomChange
                offset += offsetChange
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            boxes.entries.find { box ->
                                offset.x >= box.value.left && offset.x <= box.value.right &&
                                        offset.y >= box.value.top && offset.y <= box.value.bottom
                            }?.key?.let {
                                val selection = selections[it]
                                selections.put(
                                    it, when (selection == currentColor) {
                                        true -> null
                                        else -> currentColor
                                    }
                                )
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val position = change.position
                            boxes.entries.find { box ->
                                position.x >= box.value.left && position.x <= box.value.right &&
                                        position.y >= box.value.top && position.y <= box.value.bottom
                            }?.key?.let {
                                selections.put(
                                    it, when (eraserSelected) {
                                        true -> null
                                        else -> currentColor
                                    }
                                )
                            }
                        }
                    }
                    .transformable(state = state)
            ) {

                boxes.forEach { key ->

                    val selection = selections[key.key]

                    val style = when (selection) {
                        null -> stroke
                        else -> Fill
                    }

                    drawRect(
                        style = style,
                        topLeft = Offset(key.value.left.toFloat(), key.value.top.toFloat()),
                        size = Size(key.value.width().toFloat(), key.value.height().toFloat()),
                        color = when (selection) {
                            null -> Color.Gray
                            else -> selection.color
                        }
                    )

                }
            }

            ButtonBar(
                color = currentColor,
                eraserSelected = eraserSelected,
                onClearClick = {
                    selections
                        .filter { it.value != null }
                        .forEach {
                            selections[it.key] = null
                        }
                },
                onEraserClick = {
                    eraserSelected = !eraserSelected
                },
                onColorChosen = {
                    currentColor = it
                },
                onResetZoom = {
                    scale = 1F
                    offset = Offset.Zero
                },
                onSave = {
                    viewModel.save(selections.toMap())
                }
            )
        }
    }
}

@Composable
private fun ButtonBar(
    color: SerializableColor,
    eraserSelected: Boolean,
    onClearClick: () -> Unit,
    onEraserClick: () -> Unit,
    onResetZoom: () -> Unit,
    onSave: () -> Unit,
    onColorChosen: (SerializableColor) -> Unit
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
                onColorChosen = onColorChosen
            ) {
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
                .background(color.color)
        )

        Spacer(modifier = Modifier.weight(1F))

        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onSave()
                }
                .padding(16.dp),
            painter = painterResource(R.drawable.ic_baseline_save_24),
            contentDescription = null
        )

        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onResetZoom()
                }
                .padding(16.dp),
            painter = painterResource(R.drawable.ic_baseline_zoom_out_map_24),
            contentDescription = null
        )

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
    color: SerializableColor,
    onColorChosen: (SerializableColor) -> Unit,
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
                color = color.color,
                modifier = Modifier.fillMaxHeight(0.8F),
                onColorChanged = { color: HsvColor ->
                    currentColor = color.asSerializableColor
                }
            )
            Button(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                onClick = {
                    onColorChosen(currentColor)
                    onDismiss()
                }) {
                Text(text = "Set Color")
            }
            OutlinedButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                onClick = onDismiss
            ) {
                Text(text = "Close")
            }
        }
    }
}

@Composable
private fun rememberBoxes(
    numX: Int,
    numY: Int,
    spacing: Int,
    width: Float,
    height: Float,
    buttonBarOffset: Int
) = remember {
    mutableMapOf<Point, Rect>().apply {
        for (y in 0 until numY) {
            for (x in 0 until numX) {
                val topLeft = Offset(
                    ((x + 1) * spacing) + (width * x),
                    (((y + 1) * spacing) + (height * y)) + buttonBarOffset
                )
                put(
                    Point(x, y),
                    Rect(
                        topLeft.x.toInt(),
                        topLeft.y.toInt(),
                        (topLeft.x + width).toInt(),
                        (topLeft.y + height).toInt()
                    )
                )
            }
        }
    }
}