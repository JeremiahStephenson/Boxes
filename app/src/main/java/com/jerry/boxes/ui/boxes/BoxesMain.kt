package com.jerry.boxes.ui.boxes

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.ProjectAndLayer
import com.jerry.boxes.extensions.asSerializableColor
import com.jerry.boxes.ui.boxes.history.HistoryItem
import com.jerry.boxes.ui.boxes.shapes.Shape
import com.jerry.boxes.ui.boxes.state.ButtonsState
import com.jerry.boxes.ui.boxes.state.CanvasState
import com.jerry.boxes.ui.boxes.state.TransformerState
import com.jerry.boxes.ui.common.*
import com.jerry.boxes.ui.destinations.CreateMainDestination
import com.jerry.boxes.util.CoroutineContextProvider
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun BoxesMain(
    projectId: Long,
    navController: DestinationsNavigator,
    cc: CoroutineContextProvider = get(),
    viewModel: BoxesViewModel = koinViewModel()
) {
    val project by viewModel.projectFlow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val layerState = viewModel.layerStateFlow.collectAsStateWithLifecycle(emptyList())
    val canvasState = remember { CanvasState(layerState) }
    val buttonsState by rememberSaveable {
        mutableStateOf(
            ButtonsState(
                eraserSelected = false,
                showPngBackground = false,
                showGrid = true,
                colorPickerOn = false
            )
        )
    }

    DefaultContainer(
        title = project?.project?.name.orEmpty(),
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
        val transformerState = remember { TransformerState() }
        val rootView = LocalView.current.rootView
        val handleAction: (Action) -> Unit = remember {
            {
                handleAction(
                    canvasState,
                    buttonsState,
                    transformerState,
                    drawerState,
                    viewModel,
                    project,
                    scope,
                    cc,
                    rootView,
                    navController,
                    it
                )
            }
        }
        DrawerContainer(
            drawerState = drawerState,
            drawerContent = {
                DrawerMenu(
                    buttonsState = buttonsState,
                    onAction = handleAction,
                    canvasState = canvasState
                )
            }) {
            project?.let { project ->
                MainCanvas(
                    project = project,
                    canvasState = canvasState,
                    buttonsState = buttonsState,
                    transformerState = transformerState,
                    onAction = handleAction,
                    getUsedColorList = { viewModel.usedColors }
                )
            }
        }
    }
}

@Composable
private fun MainCanvas(
    project: ProjectAndLayer,
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    transformerState: TransformerState,
    getUsedColorList: () -> List<SerializableColor>,
    onAction: (Action) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        val density = LocalDensity.current
        val strokeWidth = remember { with(density) { 2.dp.toPx() } }
        val buttonBarOffset = remember { with(density) { 56.dp.toPx() } }

        var currentColor by rememberSaveable { mutableStateOf(Color.Green.asSerializableColor) }
        var currentShape by rememberSaveable { mutableStateOf(Shape.Box) }

        val size = this.constraints
        LaunchedEffect(project) {
            canvasState.fillInSelections(project.layers)
        }

        val contentOffset = LocalAppBarHeight.current
        val appBarExpanded by remember { derivedStateOf { contentOffset.value == 0F } }
        LaunchedEffect(project.project.rows, project.project.columns, appBarExpanded) {
            canvasState.fillInBoxes(
                size,
                buttonBarOffset,
                project.project.columns,
                project.project.rows
            )
        }

        LifecycleEffect { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                onAction(Action.Save(true))
            }
        }

        Transformer(transformerState) { scale, offset, state ->
            val currentLayer by remember { derivedStateOf { canvasState.selectedLayer.id } }
            BoxCanvas(
                canvasState = canvasState,
                buttonsState = buttonsState,
                rows = project.project.rows,
                columns = project.project.columns,
                scale = scale,
                offset = offset,
                size = size,
                strokeWidth = strokeWidth,
                state = state,
                onTap = { point ->
                    if (canvasState.hasLayersTurnedOn) {
                        when (buttonsState.colorPickerOnState) {
                            true -> canvasState.getCurrentSelection(point)?.let {
                                currentColor = it
                            }
                            else -> {
                                onAction(
                                    Action.AddToHistory(
                                        canvasState.getTapHistoryItem(point, currentLayer)
                                    )
                                )
                                canvasState.onTap(
                                    point,
                                    currentLayer,
                                    currentColor,
                                    currentShape
                                )
                            }
                        }
                    }
                },
                onDrag = {
                    if (canvasState.hasLayersTurnedOn) {
                        canvasState.addToDragHistory(currentLayer, it)
                        canvasState.onDrag(
                            it,
                            currentLayer,
                            if (buttonsState.eraserSelectedState) null else currentColor,
                            currentShape
                        )
                    }
                },
                onDragStart = {},
                onDragEnd = {
                    if (canvasState.hasLayersTurnedOn) {
                        onAction(
                            Action.AddToHistory(
                                canvasState.closeDragHistory(currentLayer)
                            )
                        )
                    }
                }
            )
        }

        ButtonBar(
            color = currentColor,
            shape = currentShape,
            buttonsState = buttonsState,
            onColorChosen = {
                buttonsState.turnOffEraser()
                currentColor = it
                onAction(Action.AddColorToUsedList(it))
            },
            onShapeChosen = {
                buttonsState.turnOffEraser()
                currentShape = it
            },
            onAction = onAction,
            getUsedColorList = getUsedColorList
        )
    }
}

@Composable
private fun ButtonBar(
    color: SerializableColor,
    shape: Shape,
    buttonsState: ButtonsState,
    getUsedColorList: () -> List<SerializableColor>,
    onAction: (Action) -> Unit,
    onColorChosen: (SerializableColor) -> Unit,
    onShapeChosen: (Shape) -> Unit,
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
                usedColors = getUsedColorList(),
                onColorChosen = onColorChosen
            ) {
                colorPicker = false
            }
        }

        var shapePicker by remember { mutableStateOf(false) }
        if (shapePicker) {
            ShapePickerDialog(
                color = color,
                onShapeChosen = onShapeChosen,
            ) {
                shapePicker = false
            }
        }

        IconMenuButton(
            onClick = {
                when (buttonsState.colorPickerOnState) {
                    true -> buttonsState.turnOnOrOffColorPicker(on = false)
                    else -> {
                        colorPicker = true
                    }
                }
            },
            color = color,
            drawableRes = when (buttonsState.colorPickerOnState) {
                true -> R.drawable.ic_baseline_colorize_24
                else -> R.drawable.ic_baseline_color_lens_24
            }
        )

        ShapeOption(
            shape = shape,
            color = color
        ) {
            shapePicker = true
        }

        Spacer(modifier = Modifier.weight(1F))

        IconMenuButton(
            onClick = { onAction(Action.Undo) },
            drawableRes = R.drawable.ic_baseline_undo_24
        )

        Spacer(modifier = Modifier.weight(1F))

        IconMenuButton(
            onClick = { onAction(Action.ResetZoom) },
            drawableRes = R.drawable.ic_baseline_zoom_out_map_24
        )
    }
}

private fun handleAction(
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    transformerState: TransformerState,
    drawerState: DrawerState,
    viewModel: BoxesViewModel,
    project: ProjectAndLayer?,
    scope: CoroutineScope,
    cc: CoroutineContextProvider,
    rootView: View,
    navController: DestinationsNavigator,
    action: Action
) {
    when (action) {
        is Action.Eraser -> buttonsState.toggleEraserSelected()
        is Action.ColorPicker -> buttonsState.toggleColorPicker()
        is Action.Save -> viewModel.save(
            if (action.autoSave) null else canvasState.boxes.keys.toList(),
            canvasState.selections.toMap(),
            canvasState.layers.map { it.id to it.on }
        )
        is Action.SelectLayer -> viewModel.selectLayer(action.layerId)
        is Action.Clear -> if (canvasState.hasLayersTurnedOn) {
            scope.launch {
                viewModel.addToHistory(
                    HistoryItem.HistoryClearItem(
                        canvasState.getCurrentSelectedLayerSelections()
                    )
                )
                canvasState.clear()
            }
        }
        is Action.Undo -> scope.launch {
            canvasState.onUndo(viewModel.getLastHistoryItem())
        }
        is Action.AddToHistory -> scope.launch {
            viewModel.addToHistory(action.historyItem)
        }
        is Action.ShowPngBackground -> buttonsState.toggleShowPngBackground()
        is Action.ShowGrid -> buttonsState.toggleGrid()
        is Action.ResetZoom -> transformerState.reset(scope)
        is Action.Edit -> {
            scope.launch { drawerState.close() }
            navController.navigate(CreateMainDestination(project?.project?.id))
        }
        is Action.AddLayer ->
            viewModel.addLayer(
                (project?.layers?.maxOf { it.layer.index } ?: -1) + 1,
                canvasState.selections.toMap()
            )
        is Action.TurnOnOrOffLayer ->
            viewModel.setLayerOnOrOff(action.layerId, action.on)
        is Action.AddColorToUsedList ->
            scope.launch { viewModel.addUsedColor(action.color) }
        is Action.Export -> exportCanvas(
            rootView = rootView,
            rows = project?.project?.rows ?: 0,
            columns = project?.project?.columns ?: 0,
            layers = canvasState.layers,
            selections = canvasState.selections,
            cc = cc
        )
    }
}


