package com.jerry.boxes.ui.boxes

import android.graphics.Point
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.doOnLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.R
import com.jerry.boxes.extensions.asSerializableColor
import com.jerry.boxes.extensions.safeLet
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.LocalAppBarHeight
import com.jerry.boxes.ui.common.unboundClickable
import com.jerry.boxes.util.IconMenuButton
import com.jerry.boxes.util.IconSelectableMenuButton
import com.jerry.boxes.util.LifecycleEffect
import com.jerry.boxes.util.storeImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.Dispatchers
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

            val boxes = remember { mutableStateMapOf<Point, RectF>() }
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

            val bottom = 0 //WindowInsets.navigationBars.getBottom(LocalDensity.current)
            LaunchedEffect(project.value?.project?.rows, project.value?.project?.columns) {
                project.value?.let { project ->
                    val maxWidth =
                        size.maxWidth / project.project.columns.toFloat()
                    val maxHeight =
                        (size.maxHeight - buttonBarOffset) / project.project.rows.toFloat()
                    val min = min(maxWidth, maxHeight)

                    val yOffSet = max(
                        (((size.maxHeight - buttonBarOffset) - (min * project.project.rows)) / 2),
                        0F
                    )
                    val xOffSet = max(((size.maxWidth - (min * project.project.columns)) / 2), 0F)

                    boxes.clear()
                    boxes.putAll(
                        generateBoxes(
                            project.project.columns,
                            project.project.rows,
                            min,
                            xOffSet.toInt(),
                            buttonBarOffset + yOffSet.toInt()
                        )
                    )
                }
            }

            LifecycleEffect { _, event ->
                if (event == Lifecycle.Event.ON_PAUSE) {
                    viewModel.save(selections.toMap())
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
                size = size,
                stroke = stroke,
                state = state,
                onTap = {
                    val selection = selections[it]
                    selections[it] = when (selection == currentColor) {
                        true -> null
                        else -> currentColor
                    }
                },
                onDrag = {
                    selections[it] = when (eraserSelected) {
                        true -> null
                        else -> currentColor
                    }
                }
            )

            val zoomAnimator = remember { Animatable(0F) }
            val panAnimatorX = remember { Animatable(0F) }
            val panAnimatorY = remember { Animatable(0F) }

            val rootView = LocalView.current.rootView
            val captureController = rememberCaptureController()

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
                },
                onExport = {
                    // todo export this logic out and make it better
                    scope.launch(Dispatchers.Main) {
                        (rootView as? ViewGroup)?.run {
                            val composeView = ComposeView(context).apply {
                                val rows = project.value?.project?.rows ?: 0
                                val columns = project.value?.project?.columns ?: 0

                                val newBoxes = generateBoxes(columns, rows, 100F, 0, 0)

                                layoutParams = ViewGroup.LayoutParams(
                                    columns * 100,
                                    rows * 100
                                )
                                visibility = View.INVISIBLE

                                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                                id = R.id.imageExportId

                                setContent {
                                    val context = LocalContext.current
                                    Capturable(
                                        controller = captureController,
                                        onCaptured = { bitmap, error ->
                                            // This is captured bitmap of a content inside Capturable Composable.
                                            bitmap?.asAndroidBitmap()?.storeImage(context)
                                            if (error != null) {
                                                // Error occurred. Handle it!
                                            }
                                            rootView.removeView(this)
                                        }
                                    ) {
                                        SelectionsBoxes(
                                            scale = 1F,
                                            offset = Offset.Zero,
                                            boxes = newBoxes,
                                            selections = selections
                                        )
                                    }
                                }
                            }
                            addView(composeView)
                            composeView.doOnLayout {
                                captureController.capture()
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun BoxCanvas(
    boxes: SnapshotStateMap<Point, RectF>,
    selections: SnapshotStateMap<Point, SerializableColor?>,
    columns: Int,
    rows: Int,
    scale: Float,
    size: Constraints,
    offset: Offset,
    stroke: Stroke,
    state: TransformableState,
    onTap: (Point) -> Unit,
    onDrag: (Point) -> Unit,
) {
    val contentOffset = LocalAppBarHeight.current
    val appBarExpanded by remember { derivedStateOf { contentOffset.value == 0F } }
    val scaleState by rememberUpdatedState(scale)
    val offsetState by rememberUpdatedState(offset)
    val sizeState by rememberUpdatedState(size)
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(appBarExpanded) {
            detectTapGestures { p ->
                val point = p.convert(scaleState, offsetState, sizeState)
                boxes.entries.find { box ->
                    point.x >= box.value.left && point.x <= box.value.right &&
                            point.y >= box.value.top && point.y <= box.value.bottom
                }?.key?.let { onTap(it) }
            }
        }
        .pointerInput(appBarExpanded) {
            detectDragGestures { change, _ ->
                if (state.isTransformInProgress) return@detectDragGestures
                val position = change.position.convert(scaleState, offsetState, sizeState)
                boxes.entries.find { box ->
                    position.x >= box.value.left && position.x <= box.value.right &&
                            position.y >= box.value.top && position.y <= box.value.bottom
                }?.key?.let { onDrag(it) }
            }
        }
        .transformable(
            state = state,
            lockRotationOnZoomPan = true
        )) {

        SelectionsBoxes(
            scale = scale,
            offset = offset,
            boxes = boxes,
            selections = selections
        )

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
                        start = Offset(start.left, start.top),
                        end = Offset(end.right, end.top)
                    )
                    if (i == rows - 1) {
                        drawLine(
                            strokeWidth = stroke.width / scale,
                            color = Color.Gray,
                            start = Offset(start.left, start.bottom),
                            end = Offset(end.right, end.bottom)
                        )
                    }
                }
            }
            for (i in 0 until columns) {
                safeLet(boxes[Point(i, 0)], boxes[Point(i, rows - 1)]) { start, end ->
                    drawLine(
                        strokeWidth = stroke.width / scale,
                        color = Color.Gray,
                        start = Offset(start.left, start.top),
                        end = Offset(end.left, end.bottom)
                    )
                    if (i == columns - 1) {
                        drawLine(
                            strokeWidth = stroke.width / scale,
                            color = Color.Gray,
                            start = Offset(start.right, start.top),
                            end = Offset(end.right, end.bottom)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionsBoxes(
    scale: Float,
    offset: Offset,
    boxes: MutableMap<Point, RectF>,
    selections: SnapshotStateMap<Point, SerializableColor?>
) {
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
                    topLeft = Offset(pos.left, pos.top),
                    size = Size(pos.width(), pos.height()),
                    color = selectedColor.color
                )
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
    onExport: () -> Unit,
    onColorChosen: (SerializableColor) -> Unit
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6F))
            .fillMaxWidth()
    ) {

        IconMenuButton(
            onClick = onClearClick,
            drawableRes = R.drawable.ic_auto_renew
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

        IconMenuButton(
            onClick = onSave,
            drawableRes = R.drawable.ic_baseline_save_24
        )

        IconMenuButton(
            onClick = onResetZoom,
            drawableRes = R.drawable.ic_baseline_zoom_out_map_24
        )

        IconMenuButton(
            onClick = onExport,
            drawableRes = R.drawable.ic_baseline_image_24
        )

        IconSelectableMenuButton(
            onClick = onEraserClick,
            isSelected = eraserSelected,
            drawableRes = R.drawable.ic_eraser
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
                color = color.color,
                modifier = Modifier.fillMaxHeight(0.5F),
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
    size: Float,
    xOffSet: Int,
    yOffSet: Int
) = mutableMapOf<Point, RectF>().apply {
    for (y in 0 until numY) {
        for (x in 0 until numX) {
            val topLeft = Offset(
                (size * x) + xOffSet,
                (size * y) + yOffSet
            )
            put(
                Point(x, y),
                RectF(
                    topLeft.x,
                    topLeft.y,
                    (topLeft.x + size),
                    (topLeft.y + size)
                )
            )
        }
    }
}

private fun Offset.convert(scale: Float, offset: Offset, size: Constraints): Offset {
    val centerX = size.maxWidth / 2F
    val centerY = size.maxHeight / 2F
    val point =
        Offset(((x - centerX) * (1F / scale)) + centerX, ((y - centerY) * (1F / scale)) + centerY)
    return point - (offset / scale)
}
