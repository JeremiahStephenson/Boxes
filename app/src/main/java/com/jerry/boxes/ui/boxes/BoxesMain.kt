package com.jerry.boxes.ui.boxes

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.ProjectAndLayers
import com.jerry.boxes.ui.boxes.history.UserHistory
import com.jerry.boxes.ui.boxes.state.ButtonsState
import com.jerry.boxes.ui.boxes.state.CanvasState
import com.jerry.boxes.ui.boxes.state.ColorAndShapeState
import com.jerry.boxes.ui.boxes.state.TransformerState
import com.jerry.boxes.ui.common.*
import com.jerry.boxes.ui.destinations.CreateMainDestination
import com.jerry.boxes.ui.destinations.LayersEditMainDestination
import com.jerry.boxes.ui.shapes.Shape
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

    val canvasState = rememberCanvasState(viewModel)
    val buttonsState = rememberSaveable {
        ButtonsState(
            eraserSelected = false,
            showPngBackground = false,
            showGrid = true,
            colorPickerOn = false
        )
    }

    BackHandler(drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    DefaultContainer(
        title = project?.project?.name.orEmpty(),
        disableAppbarScroll = true,
        appBarActions = {
            Icon(
                modifier = Modifier
                    .unboundClickable {
                        navController.navigate(LayersEditMainDestination(projectId))
                    }
                    .padding(16.dp),
                painter = painterResource(R.drawable.ic_layers_24),
                contentDescription = null
            )
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
                    .padding(16.dp),
                painter = painterResource(R.drawable.ic_menu_24),
                contentDescription = null
            )
        }
    ) {
        val projectNotNull by remember { derivedStateOf { project?.project != null } }
        val colorAndShapeState = when (projectNotNull) {
            true -> rememberSaveable {
                ColorAndShapeState(
                    project!!.project.currentColor,
                    project!!.project.currentShape
                )
            }
            else -> remember { ColorAndShapeState(null, null) }
        }
        val colorAndShapeUpdatedState by rememberUpdatedState(colorAndShapeState)

        val transformerState = remember { TransformerState() }
        val rootView = LocalView.current.rootView
        val handleAction: (Action) -> Unit = remember {
            {
                handleAction(
                    canvasState,
                    buttonsState,
                    transformerState,
                    drawerState,
                    colorAndShapeUpdatedState,
                    project,
                    viewModel,
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

            if (projectNotNull) {
                MainCanvas(
                    project = project!!,
                    canvasState = canvasState,
                    buttonsState = buttonsState,
                    transformerState = transformerState,
                    colorAndShapeState = colorAndShapeUpdatedState,
                    onAction = handleAction,
                    getUsedColorList = { viewModel.usedColors }
                )
            }

            if (canvasState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun MainCanvas(
    project: ProjectAndLayers,
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    transformerState: TransformerState,
    colorAndShapeState: ColorAndShapeState,
    getUsedColorList: () -> List<SerializableColor>,
    onAction: (Action) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        val density = LocalDensity.current
        val strokeWidth = remember { with(density) { 2.dp.toPx() } }
        val buttonBarOffset = remember { with(density) { 56.dp.toPx() } }

        val size = this.constraints
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
                columns = project.project.columns,
                rows = project.project.rows,
                scale = scale,
                offset = offset,
                size = size,
                strokeWidth = strokeWidth,
                state = state,
                onTap = { point ->
                    if (canvasState.hasLayersTurnedOn) {
                        when (buttonsState.colorPickerOnState) {
                            true -> canvasState.getCurrentSelection(point)?.let {
                                colorAndShapeState.setColor(it)
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
                                    colorAndShapeState.colorState,
                                    colorAndShapeState.shapeState
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
                            if (buttonsState.eraserSelectedState) null else colorAndShapeState.colorState,
                            colorAndShapeState.shapeState
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
            color = colorAndShapeState.colorState,
            shape = colorAndShapeState.shapeState,
            buttonsState = buttonsState,
            onColorChosen = {
                buttonsState.turnOffEraser()
                colorAndShapeState.setColor(it)
                onAction(Action.AddColorToUsedList(it))
            },
            onShapeChosen = {
                buttonsState.turnOffEraser()
                colorAndShapeState.setShape(it)
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

        var colorPicker by rememberSaveable { mutableStateOf(false) }
        if (colorPicker) {
            ColorPickerDialog(
                color = color,
                usedColors = getUsedColorList(),
                onColorChosen = onColorChosen
            ) {
                colorPicker = false
            }
        }

        var shapePicker by rememberSaveable { mutableStateOf(false) }
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
                true -> R.drawable.ic_colorize_24
                else -> R.drawable.ic_color_lens_24
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
            drawableRes = R.drawable.ic_undo_24
        )

        Spacer(modifier = Modifier.weight(1F))

        IconMenuButton(
            onClick = { onAction(Action.ResetZoom) },
            drawableRes = R.drawable.ic_zoom_out_map_24
        )
    }
}

private fun handleAction(
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    transformerState: TransformerState,
    drawerState: DrawerState,
    colorAndShapeState: ColorAndShapeState,
    project: ProjectAndLayers?,
    viewModel: BoxesViewModel,
    scope: CoroutineScope,
    cc: CoroutineContextProvider,
    rootView: View,
    navController: DestinationsNavigator,
    action: Action
) {
    when (action) {
        is Action.Eraser -> buttonsState.toggleEraserSelected()
        is Action.ColorPicker -> buttonsState.toggleColorPicker()
        is Action.Save -> {
            saveProject(
                canvasState,
                colorAndShapeState,
                project,
                viewModel,
                cc,
                rootView,
                action.autoSave,
                scope
            )
        }
        is Action.SelectLayer -> viewModel.selectLayer(action.layerId)
        is Action.Clear -> if (canvasState.hasLayersTurnedOn) {
            scope.launch {
                viewModel.addToHistory(
                    UserHistory(
                        canvasState.selectedLayer.id,
                        canvasState.getCurrentSelectedLayerSelections(canvasState.selectedLayer.id)
                    )
                )
                canvasState.clear()
            }
        }
        is Action.Undo -> scope.launch {
            canvasState.onUndo(
                canvasState.selectedLayer.id,
                viewModel.getLastHistoryItem(canvasState.selectedLayer.id)
            )
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
                name = action.name,
                index = (project?.layers?.maxOf { it.index } ?: -1) + 1,
                selections = canvasState.selections
            )
        is Action.TurnOnOrOffLayer -> viewModel.setLayerOnOrOff(action.layerId, action.on)
        is Action.AddColorToUsedList -> scope.launch { viewModel.addUsedColor(action.color) }
        is Action.GoToLayerEdit -> project?.project?.id?.let {
            navController.navigate(LayersEditMainDestination(it))
        }
        is Action.Export -> exportCanvas(
            rootView = rootView,
            imageSize = action.size,
            projectId = project?.project?.id ?: 0,
            rows = project?.project?.rows ?: 0,
            columns = project?.project?.columns ?: 0,
            layers = canvasState.layers,
            selections = canvasState.selections,
            cc = cc,
            export = true
        )
    }
}

private fun saveProject(
    canvasState: CanvasState,
    colorAndShapeState: ColorAndShapeState,
    project: ProjectAndLayers?,
    viewModel: BoxesViewModel,
    cc: CoroutineContextProvider,
    rootView: View,
    autoSave: Boolean,
    scope: CoroutineScope
) {
    scope.launch(cc.main) {
        exportCanvas(
            rootView = rootView,
            imageSize = 50F,
            projectId = project?.project?.id ?: 0,
            rows = project?.project?.rows ?: 0,
            columns = project?.project?.columns ?: 0,
            layers = canvasState.layers.map { it.copy(on = true) },
            selections = canvasState.selections,
            cc = cc,
            export = false
        )
    }
    viewModel.save(
        if (autoSave) null else canvasState.boxes.keys.toList(),
        canvasState.selections,
        canvasState.layers.map { it.id to it.on },
        colorAndShapeState.colorState,
        colorAndShapeState.shapeState
    )
}

@Composable
private fun rememberCanvasState(viewModel: BoxesViewModel): CanvasState {
    val layerState = viewModel.layerStateFlow.collectAsStateWithLifecycle(emptyList())
    val pixelsState = viewModel.pixelsFlow.collectAsStateWithLifecycle()
    return remember { CanvasState(layerState, pixelsState) }
}


