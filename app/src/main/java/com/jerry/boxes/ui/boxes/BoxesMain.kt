package com.jerry.boxes.ui.boxes

import android.graphics.Point
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.google.accompanist.flowlayout.FlowRow
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.ProjectAndPixel
import com.jerry.boxes.extensions.asSerializableColor
import com.jerry.boxes.ui.boxes.state.ButtonsState
import com.jerry.boxes.ui.boxes.state.CanvasState
import com.jerry.boxes.ui.boxes.state.TransformerState
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.unboundClickable
import com.jerry.boxes.util.IconMenuButton
import com.jerry.boxes.util.IconSelectableMenuButton
import com.jerry.boxes.util.LifecycleEffect
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
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val canvasState = remember { CanvasState() }
    val buttonState by rememberSaveable {
        mutableStateOf(
            ButtonsState(
                eraserSelected = false,
                showPngBackground = false
            )
        )
    }

    DefaultContainer(
        title = project.value?.project?.name.orEmpty(),
        disableAppbarScroll = true,
        appBarActions = {
            Icon(
                modifier = Modifier
                    .unboundClickable {
                        scope.launch {
                            when (drawerState.isOpen) {
                                true -> drawerState.close()
                                else -> drawerState.open()
                            }
                        }
                    }
                    .padding(8.dp),
                painter = painterResource(R.drawable.ic_baseline_menu_24),
                contentDescription = null
            )
        }
    ) {
        DrawerContainer(
            drawerState = drawerState,
            drawerContent = {
                val rootView = LocalView.current.rootView
                DrawerMenu(
                    eraserSelected = { buttonState.eraserSelectedState },
                    onClearClick = {
                        canvasState.selections
                            .filter { it.value != null }
                            .forEach {
                                canvasState.selections[it.key] = null
                            }
                    },
                    onEraserClick = {
                        buttonState.toggleEraserSelected()
                    },
                    onSave = {
                        viewModel.save(canvasState.selections.toMap())
                    },
                    onShowPngBackground = {
                        buttonState.toggleShowPngBackground()
                    },
                    showPngBackgroundSelected = { buttonState.showPngBackgroundState },
                    onExport = {
                        exportCanvas(
                            scope = scope,
                            rootView = rootView,
                            rows = project.value?.project?.rows ?: 0,
                            columns = project.value?.project?.columns ?: 0,
                            selections = canvasState.selections
                        )
                    }
                )
            }) {
            project.value?.let { project ->
                MainCanvas(
                    project = project,
                    savedBoxes = viewModel.boxes,
                    onSaveBoxes = {
                        viewModel.boxes = it
                    },
                    onSaveProject = {
                        viewModel.save(it)
                    },
                    canvasState = canvasState,
                    buttonsState = buttonState
                )
            }
        }
    }
}

@Composable
private fun MainCanvas(
    project: ProjectAndPixel,
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    savedBoxes: HashMap<Point, SerializableColor?>?,
    onSaveBoxes: (HashMap<Point, SerializableColor?>) -> Unit,
    onSaveProject: (Map<Point, SerializableColor?>) -> Unit
) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        val density = LocalDensity.current
        val strokeWidth = remember {
            with(density) {
                2.dp.roundToPx()
            }.toFloat()
        }

        val buttonBarOffset = remember {
            with(density) {
                56.dp.roundToPx()
            }
        }

        var currentColor by rememberSaveable { mutableStateOf(Color.Green.asSerializableColor) }

        val size = this.constraints
        LaunchedEffect(project) {
            when (savedBoxes) {
                null -> canvasState.selections.putAll(project.pixels.associate {
                    Point(it.x, it.y) to
                            SerializableColor(
                                it.hue,
                                it.saturation,
                                it.value,
                                it.alpha
                            )
                })
                else -> canvasState.selections.putAll(savedBoxes)
            }
        }

        LaunchedEffect(project.project.rows, project.project.columns) {
            val maxWidth =
                size.maxWidth / project.project.columns.toFloat()
            val maxHeight =
                (size.maxHeight - buttonBarOffset) / project.project.rows.toFloat()
            val min = min(maxWidth, maxHeight)

            val yOffSet = max(
                (((size.maxHeight - buttonBarOffset) - (min * project.project.rows)) / 2),
                0F
            )
            val xOffSet =
                max(((size.maxWidth - (min * project.project.columns)) / 2), 0F)

            canvasState.boxes.clear()
            canvasState.boxes.putAll(
                generateBoxes(
                    project.project.columns,
                    project.project.rows,
                    min,
                    xOffSet.toInt(),
                    buttonBarOffset + yOffSet.toInt()
                )
            )
        }

        LifecycleEffect { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                onSaveProject(canvasState.selections.toMap())
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                onSaveBoxes(HashMap(canvasState.selections.toMap()))
            }
        }

        val transformerState = remember { TransformerState() }
        Transformer(transformerState) { scale, offset, state ->
            if (buttonsState.showPngBackgroundState) {
                PngBackground()
            }
            BoxCanvas(
                boxes = canvasState.boxes,
                selections = canvasState.selections,
                rows = project.project.rows,
                columns = project.project.columns,
                scale = scale,
                offset = offset,
                size = size,
                strokeWidth = strokeWidth,
                state = state,
                showPngBackgroundSelected = { buttonsState.showPngBackgroundState },
                onTap = {
                    val selection = canvasState.selections[it]
                    canvasState.selections[it] = when (selection == currentColor) {
                        true -> null
                        else -> currentColor
                    }
                },
                onDrag = {
                    canvasState.selections[it] = when (buttonsState.eraserSelectedState) {
                        true -> null
                        else -> currentColor
                    }
                }
            )
        }

        ButtonBar(
            color = currentColor,
            onColorChosen = {
                currentColor = it
            },
            onResetZoom = {
                transformerState.reset(scope)
            }
        )
    }
}

@Composable
private fun DrawerMenu(
    eraserSelected: () -> Boolean,
    showPngBackgroundSelected: () -> Boolean,
    onClearClick: () -> Unit,
    onEraserClick: () -> Unit,
    onSave: () -> Unit,
    onExport: () -> Unit,
    onShowPngBackground: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(8.dp)
    ) {
        ButtonSection(R.string.tools) {
            IconSelectableMenuButton(
                onClick = onEraserClick,
                isSelected = eraserSelected,
                drawableResOn = R.drawable.ic_baseline_eraser_on_24,
                drawableResOff = R.drawable.ic_baseline_eraser_off_24
            )
            IconSelectableMenuButton(
                onClick = onShowPngBackground,
                isSelected = showPngBackgroundSelected,
                drawableResOn = R.drawable.ic_baseline_grid_on_24,
                drawableResOff = R.drawable.ic_baseline_grid_off_24
            )
        }
        ButtonSection(R.string.export) {
            IconMenuButton(
                onClick = onExport,
                drawableRes = R.drawable.ic_baseline_image_24
            )
        }
        ButtonSection(R.string.save) {
            IconMenuButton(
                onClick = onSave,
                drawableRes = R.drawable.ic_baseline_save_24
            )
        }
        Spacer(modifier = Modifier.weight(1F))
        ButtonSection(R.string.clear) {
            IconMenuButton(
                onClick = onClearClick,
                drawableRes = R.drawable.ic_auto_renew
            )
        }
    }
}

@Composable
private fun ButtonSection(
    @StringRes title: Int,
    content: @Composable () -> Unit
) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        text = stringResource(title)
    )
    Divider(modifier = Modifier.padding(horizontal = 16.dp))
    FlowRow(content = content)
}

@Composable
private fun ButtonBar(
    color: SerializableColor,
    onResetZoom: () -> Unit,
    onColorChosen: (SerializableColor) -> Unit
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6F))
            .fillMaxWidth()
    ) {

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
            onClick = onResetZoom,
            drawableRes = R.drawable.ic_baseline_zoom_out_map_24
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

@Composable
private fun DrawerContainer(
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        drawerShape = RoundedCornerShape(topStart = 16.dp),
                        modifier = Modifier.fillMaxWidth(0.6F)
                    ) {
                        drawerContent()
                    }
                }
            },
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    content()
                }
            }
        )
    }
}


