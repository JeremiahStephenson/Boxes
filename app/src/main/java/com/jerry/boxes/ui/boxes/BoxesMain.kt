package com.jerry.boxes.ui.boxes

import android.graphics.Point
import android.graphics.Rect
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.R
import com.jerry.boxes.extensions.asSerializableColor
import com.jerry.boxes.extensions.safeLet
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.unboundClickable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.max
import kotlin.math.min

@Destination
@Composable
fun BoxesMain(
    projectId: Long,
    navController: DestinationsNavigator,
    viewModel: BoxesViewModel = koinViewModel()
) {
    val project = viewModel.projectFlow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    DefaultContainer(
        title = project.value?.project?.name.orEmpty()
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

            val density = LocalDensity.current
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
            var eraserSelected by remember { mutableStateOf(false) }

            val boxes = remember { mutableStateMapOf<Point, Rect>() }
            val selections = remember { mutableStateMapOf<Point, SerializableColor?>() }

            val size = this.constraints
            LaunchedEffect(project.value) {
                project.value?.let { project ->
                    when (viewModel.boxes) {
                        null -> selections.putAll(project.pixels.associate {
                            Point(it.x, it.y) to
                                    SerializableColor(it.hue, it.saturation, it.value, it.alpha)
                        })
                        else -> selections.putAll(viewModel.boxes!!)
                    }
                }
            }

            LaunchedEffect(project.value?.project?.rows, project.value?.project?.columns) {
                project.value?.let { project ->
                    val maxWidth =
                        (size.maxWidth - (project.project.columns + 1)) / project.project.columns
                    val maxHeight =
                        (size.maxHeight - (project.project.rows + 1)) / project.project.rows
                    val min = min(maxWidth, maxHeight).toFloat()

                    boxes.clear()
                    boxes.putAll(
                        generateBoxes(
                            project.project.columns,
                            project.project.rows,
                            min,
                            min,
                            buttonBarOffset
                        )
                    )
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    viewModel.boxes = HashMap(selections.toMap())
                }
            }

            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                scale = max(1F, scale * zoomChange)
                offset += offsetChange
            }
            BoxCanvas(
                boxes = boxes,
                selections = selections,
                rows = project.value?.project?.rows ?: 0,
                columns = project.value?.project?.columns ?: 0,
                scale = scale,
                offset = offset,
                stroke = stroke,
                state = state,
                onTap = { p ->
                    val point = p.convert(scale, offset, size)
                    boxes.entries.find { box ->
                        point.x >= box.value.left && point.x <= box.value.right &&
                                point.y >= box.value.top && point.y <= box.value.bottom
                    }?.key?.let {
                        val selection = selections[it]
                        selections[it] = when (selection == currentColor) {
                            true -> null
                            else -> currentColor
                        }
                    }
                },
                onDrag = { p ->
                    val position = p.convert(scale, offset, size)
                    boxes.entries.find { box ->
                        position.x >= box.value.left && position.x <= box.value.right &&
                                position.y >= box.value.top && position.y <= box.value.bottom
                    }?.key?.let {
                        selections[it] = when (eraserSelected) {
                            true -> null
                            else -> currentColor
                        }
                    }
                }
            )

            val zoomAnimator = remember { Animatable(0F) }
            val panAnimatorX = remember { Animatable(0F) }
            val panAnimatorY = remember { Animatable(0F) }
            ButtonBar(
                color = currentColor,
                eraserSelected = { eraserSelected },
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
                    scope.launch {
                        zoomAnimator.snapTo(scale)
                        zoomAnimator.animateTo(1F) {
                            scale = this.value
                        }
                    }
                    scope.launch {
                        panAnimatorX.snapTo(offset.x)
                        panAnimatorX.animateTo(0F) {
                            offset = offset.copy(x = this.value)
                        }
                    }
                    scope.launch {
                        panAnimatorY.snapTo(offset.y)
                        panAnimatorY.animateTo(0F) {
                            offset = offset.copy(y = this.value)
                        }
                    }
                },
                onSave = {
                    viewModel.save(selections.toMap())
                }
            )
        }
    }
}

@Composable
private fun BoxCanvas(
    boxes: SnapshotStateMap<Point, Rect>,
    selections: SnapshotStateMap<Point, SerializableColor?>,
    columns: Int,
    rows: Int,
    scale: Float,
    offset: Offset,
    stroke: Stroke,
    state: TransformableState,
    onTap: (Offset) -> Unit,
    onDrag: (Offset) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures { point ->
                onTap(point)
            }
        }
        .pointerInput(Unit) {
            detectDragGestures { change, _ ->
                if (state.isTransformInProgress) return@detectDragGestures
                onDrag(change.position)
            }
        }
        .transformable(
            state = state,
            lockRotationOnZoomPan = true
        )) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        ) {
            selections.forEach { (point, color) ->
                val position = boxes[point]
                safeLet(position, color) { pos, selectedColor ->
                    drawRect(
                        style = Fill,
                        topLeft = Offset(pos.left.toFloat(), pos.top.toFloat()),
                        size = Size(pos.width().toFloat(), pos.height().toFloat()),
                        color = selectedColor.color
                    )
                }
            }
        }

        Canvas(
            Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        ) {
            for (i in 0 until rows) {
                safeLet(boxes[Point(0, i)], boxes[Point(columns - 1, i)]) { start, end ->
                    drawLine(
                        strokeWidth = stroke.width / scale,
                        color = Color.Gray,
                        start = Offset(start.left.toFloat(), start.top.toFloat()),
                        end = Offset(end.right.toFloat(), end.top.toFloat())
                    )
                    if (i == rows - 1) {
                        drawLine(
                            strokeWidth = stroke.width / scale,
                            color = Color.Gray,
                            start = Offset(start.left.toFloat(), start.bottom.toFloat()),
                            end = Offset(end.right.toFloat(), end.bottom.toFloat())
                        )
                    }
                }
            }
            for (i in 0 until columns) {
                safeLet(boxes[Point(i, 0)], boxes[Point(i, rows - 1)]) { start, end ->
                    drawLine(
                        strokeWidth = stroke.width / scale,
                        color = Color.Gray,
                        start = Offset(start.left.toFloat(), start.top.toFloat()),
                        end = Offset(end.left.toFloat(), end.bottom.toFloat())
                    )
                    if (i == columns - 1) {
                        drawLine(
                            strokeWidth = stroke.width / scale,
                            color = Color.Gray,
                            start = Offset(start.right.toFloat(), start.top.toFloat()),
                            end = Offset(end.right.toFloat(), end.bottom.toFloat())
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ButtonBar(
    color: SerializableColor,
    eraserSelected: () -> Boolean,
    onClearClick: () -> Unit,
    onEraserClick: () -> Unit,
    onResetZoom: () -> Unit,
    onSave: () -> Unit,
    onColorChosen: (SerializableColor) -> Unit
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6F))
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
                    when (eraserSelected()) {
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
                Text(text = stringResource(R.string.set_color))
            }
            OutlinedButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.close))
            }
        }
    }
}

private fun generateBoxes(
    numX: Int,
    numY: Int,
    width: Float,
    height: Float,
    buttonBarOffset: Int
) = mutableMapOf<Point, Rect>().apply {
    for (y in 0 until numY) {
        for (x in 0 until numX) {
            val topLeft = Offset(
                (x + 1) + (width * x),
                ((y + 1) + (height * y)) + buttonBarOffset
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

private fun Offset.convert(scale: Float, offset: Offset, size: Constraints): Offset {
    val centerX = size.maxWidth / 2F
    val centerY = size.maxHeight / 2F
    val point = Offset(((x - centerX) * (1F / scale)) + centerX, ((y - centerY) * (1F / scale)) + centerY)
    return point - (offset / scale)
}
