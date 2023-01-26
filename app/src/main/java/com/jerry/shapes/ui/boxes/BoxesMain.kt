package com.jerry.shapes.ui.boxes

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.shapes.R
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.cache.data.Project
import com.jerry.shapes.extensions.safeLet
import com.jerry.shapes.ui.boxes.data.Action
import com.jerry.shapes.ui.boxes.data.UiEvent
import com.jerry.shapes.ui.boxes.history.UserHistory
import com.jerry.shapes.ui.boxes.state.ButtonsState
import com.jerry.shapes.ui.boxes.state.CanvasState
import com.jerry.shapes.ui.boxes.state.SelectionState
import com.jerry.shapes.ui.boxes.state.TransformerState
import com.jerry.shapes.ui.boxes.state.enums.Direction
import com.jerry.shapes.ui.boxes.state.enums.TapType
import com.jerry.shapes.ui.common.*
import com.jerry.shapes.ui.destinations.CreateMainDestination
import com.jerry.shapes.ui.destinations.LayersEditMainDestination
import com.jerry.shapes.ui.shapes.Shape
import com.jerry.shapes.util.ExportType
import com.jerry.shapes.util.ImmutableList
import com.jerry.shapes.util.openImage
import com.jerry.shapes.util.openShareSheet
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Destination
@Composable
fun BoxesMain(
    projectId: Long,
    projectName: String?,
    navController: DestinationsNavigator,
    viewModel: BoxesViewModel = koinViewModel()
) {
    val project by viewModel.projectFlow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    BackHandler(drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    val projectNotNull by remember { derivedStateOf { project != null } }
    val context = LocalContext.current

    val genericError = stringResource(R.string.generic_error)
    val canvasState = rememberCanvasState(viewModel) {
        Toast.makeText(context, it ?: genericError, Toast.LENGTH_LONG).show()
    }

    val buttonsState = rememberSaveable {
        ButtonsState(
            eraserSelected = false,
            selectToolSelected = false
        )
    }

    val selectionState = rememberSaveable { SelectionState() }
    DefaultContainer(
        title = project?.name ?: projectName ?: "",
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
        val transformerState = remember { TransformerState() }
        val handleAction: (Action) -> Unit = remember {
            {
                handleAction(
                    canvasState,
                    buttonsState,
                    transformerState,
                    drawerState,
                    selectionState,
                    project,
                    viewModel,
                    scope,
                    context,
                    navController,
                    it
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            DrawerContainer(
                drawerState = drawerState,
                drawerContent = {
                    if (projectNotNull) {
                        DrawerMenu(
                            buttonsState = buttonsState,
                            onAction = handleAction,
                            canvasState = canvasState,
                            getProject = { project!! }
                        )
                    }
                }
            ) {
                if (projectNotNull) {
                    MainCanvas(
                        project = project!!,
                        canvasState = canvasState,
                        buttonsState = buttonsState,
                        selectionState = selectionState,
                        transformerState = transformerState,
                        onAction = handleAction,
                        getUsedColorList = { viewModel.usedColors }
                    )
                }
            }
            if (canvasState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            val snackBarHostState = remember { SnackbarHostState() }
            SnackBarImageLocator(snackBarHostState = snackBarHostState)
            LaunchedEffect(Unit) {
                viewModel.uiEventFlow.collectLatest {
                    when (it) {
                        is UiEvent.Error ->
                            Toast.makeText(context, it.error ?: genericError, Toast.LENGTH_LONG)
                                .show()
                        is UiEvent.Export -> when (it.exportType) {
                            ExportType.FILE -> it.filePath?.let { path ->
                                snackBarHostState.showSnackbar(
                                    path,
                                    duration = SnackbarDuration.Indefinite
                                )
                            }
                            else -> {
                                it.filePath?.let { context.openShareSheet(it) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.SnackBarImageLocator(
    snackBarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    SnackbarHost(
        modifier = Modifier.align(Alignment.BottomCenter),
        hostState = snackBarHostState,
        snackbar = { snackBarData ->
            Snackbar(
                action = {
                    Row {
                        Button(
                            onClick = {
                                context.openImage(snackBarData.visuals.message)
                                snackBarHostState.currentSnackbarData?.dismiss()
                            }
                        ) {
                            Text(stringResource(R.string.view))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                snackBarHostState.currentSnackbarData?.dismiss()
                            }
                        ) {
                            Text(stringResource(R.string.dismiss))
                        }
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) { Text(text = snackBarData.visuals.message) }
        }
    )
}

@Composable
private fun MainCanvas(
    project: Project,
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    selectionState: SelectionState,
    transformerState: TransformerState,
    getUsedColorList: () -> ImmutableList<ColorAndShape>,
    onAction: (Action) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val strokeWidth = remember { with(density) { 2.dp.toPx() } }
        val buttonBarOffset = remember { with(density) { 56.dp.toPx() } }

        val size = this.constraints
        val contentOffset = LocalAppBarHeight.current
        val appBarExpanded by remember { derivedStateOf { contentOffset.value == 0F } }
        LaunchedEffect(project.rows, project.columns, appBarExpanded) {
            canvasState.fillInBoxes(
                size,
                buttonBarOffset,
                project.columns,
                project.rows
            )
        }

        LifecycleEffect { _, event ->
            Timber.d("LifecycleTest - $event")
            if (event == Lifecycle.Event.ON_PAUSE) {
                onAction(Action.Save(true))
            }
        }

        val projectState by rememberUpdatedState(project)
        Transformer(transformerState) { scale, offset, state ->
            val currentLayer by remember { derivedStateOf { canvasState.selectedLayer.id } }
            BoxCanvas(
                canvasState = canvasState,
                buttonsState = buttonsState,
                selectionState = selectionState,
                project = project,
                scale = scale,
                offset = offset,
                size = size,
                strokeWidth = strokeWidth,
                state = state,
                onTap = { point ->
                    if (canvasState.hasLayersTurnedOn) {
                        when (buttonsState.tapTypeState) {
                            TapType.PICKER -> canvasState.getCurrentSelection(point)?.let {
                                onAction(Action.SetColor(it))
                            }
                            TapType.TAP -> {
                                onAction(
                                    Action.AddToHistory(
                                        canvasState.getTapHistoryItem(point, currentLayer)
                                    )
                                )
                                canvasState.onTap(
                                    point,
                                    currentLayer,
                                    projectState.colorAndShape,
                                    projectState.currentShape
                                )
                            }
                            TapType.FILL -> onAction(Action.Fill(point, currentLayer))
                        }
                    }
                },
                onDrag = {
                    if (canvasState.hasLayersTurnedOn) {
                        val color = projectState.colorAndShape
                            .copy(shape = projectState.currentShape)
                        canvasState.addToDragHistory(
                            it,
                            currentLayer,
                            if (buttonsState.eraserSelectedState) null else color
                        )
                        canvasState.onDrag(
                            it,
                            currentLayer,
                            if (buttonsState.eraserSelectedState) null else color
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
            getColor = { project.colorAndShape },
            getShape = { project.currentShape },
            buttonsState = buttonsState,
            canvasState = canvasState,
            transformerState = transformerState,
            onColorChosen = {
                buttonsState.turnOffEraser()
                onAction(Action.SetColor(it))
                onAction(Action.AddColorToUsedList(it))
            },
            onShapeChosen = {
                buttonsState.turnOffEraser()
                onAction(Action.SetShape(it))
            },
            onAction = onAction,
            getUsedColorList = getUsedColorList
        )

        AdditionalButtonBar(
            buttonsState = buttonsState,
            selectionState = selectionState,
            columns = project.columns,
            rows = project.rows,
            onAction = onAction
        )
    }
}

@Composable
private fun ButtonBar(
    buttonsState: ButtonsState,
    canvasState: CanvasState,
    transformerState: TransformerState,
    getColor: () -> ColorAndShape,
    getShape: () -> Shape,
    getUsedColorList: () -> ImmutableList<ColorAndShape>,
    onAction: (Action) -> Unit,
    onColorChosen: (ColorAndShape) -> Unit,
    onShapeChosen: (Shape) -> Unit
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8F))
            .fillMaxWidth()
    ) {
        var colorPicker by rememberSaveable { mutableStateOf(false) }
        if (colorPicker) {
            ColorPickerDialog(
                color = getColor(),
                usedColors = getUsedColorList(),
                onColorChosen = onColorChosen
            ) {
                colorPicker = false
            }
        }

        var shapePicker by rememberSaveable { mutableStateOf(false) }
        if (shapePicker) {
            ShapePickerDialog(
                color = getColor(),
                numberOfBoxes = canvasState.boxes.size,
                onShapeChosen = onShapeChosen
            ) {
                shapePicker = false
            }
        }

        IconMenuButton(
            onClick = { colorPicker = true },
            color = getColor(),
            drawableRes = R.drawable.ic_color_lens_24,
            contentDescription = stringResource(R.string.color_selector)
        )

        ShapeOption(
            shape = getShape(),
            color = getColor(),
            showToolTip = true
        ) {
            shapePicker = true
        }

        val tapTypeState by remember {
            derivedStateOf {
                when (buttonsState.tapTypeState) {
                    TapType.PICKER -> R.drawable.ic_colorize_24
                    TapType.FILL -> R.drawable.ic_format_color_fill_24
                    else -> R.drawable.ic_brush_24
                }
            }
        }
        IconMenuButton(
            onClick = { buttonsState.alternateTapType() },
            color = getColor(),
            drawableRes = tapTypeState,
            contentDescription = stringResource(R.string.toggle_tap_tool)
        )

        Spacer(modifier = Modifier.weight(1F))

        val historyEnabled by remember { derivedStateOf { canvasState.historyCount > 0 } }
        IconMenuButton(
            enabled = historyEnabled,
            onClick = { onAction(Action.Undo) },
            drawableRes = R.drawable.ic_undo_24,
            contentDescription = stringResource(R.string.undo_history)
        )

        Spacer(modifier = Modifier.weight(1F))

        val isTransformed by remember { derivedStateOf { transformerState.scale > 1F || transformerState.offset != Offset.Zero } }
        IconMenuButton(
            enabled = isTransformed,
            onClick = { onAction(Action.ResetZoom) },
            drawableRes = R.drawable.ic_zoom_out_map_24,
            contentDescription = stringResource(R.string.re_center)
        )
    }
}

@Composable
private fun AdditionalButtonBar(
    buttonsState: ButtonsState,
    selectionState: SelectionState,
    columns: Int,
    rows: Int,
    onAction: (Action) -> Unit
) {
    val columnsState by rememberUpdatedState(columns)
    val rowsState by rememberUpdatedState(rows)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 56.dp),
        verticalAlignment = Alignment.Top
    ) {
        AnimatedContent(targetState = buttonsState) {
            when {
                buttonsState.selectToolSelectedState -> IconMenuButton(
                    modifier = Modifier,
                    onClick = { onAction(Action.SelectTool) },
                    drawableRes = R.drawable.ic_select_all_24,
                    contentDescription = stringResource(R.string.turn_off_select_and_move)
                )
                buttonsState.eraserSelectedState -> IconMenuButton(
                    modifier = Modifier,
                    onClick = { onAction(Action.Eraser) },
                    drawableRes = R.drawable.ic_eraser_on_24,
                    contentDescription = stringResource(R.string.turn_off_eraser)
                )
            }
        }
        if (buttonsState.selectToolSelectedState) {
            val enabled by remember { derivedStateOf { selectionState.bottomRightState != null && selectionState.topLeftState != null } }
            val isAtLeftEdge by remember {
                derivedStateOf {
                    (selectionState.bottomRightState?.x ?: 0) <= 0 ||
                        (selectionState.topLeftState?.x ?: 0) <= 0
                }
            }
            Spacer(modifier = Modifier.weight(1F))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconMenuButton(
                    enabled = !isAtLeftEdge && enabled,
                    onClick = { onAction(Action.Move(Direction.LEFT)) },
                    drawableRes = R.drawable.ic_arrow_back_24,
                    contentDescription = stringResource(R.string.move_left)
                )
                Column {
                    val isAtTopEdge by remember {
                        derivedStateOf {
                            (selectionState.bottomRightState?.y ?: 0) <= 0 ||
                                (selectionState.topLeftState?.y ?: 0) <= 0
                        }
                    }
                    IconMenuButton(
                        enabled = !isAtTopEdge && enabled,
                        onClick = { onAction(Action.Move(Direction.UP)) },
                        drawableRes = R.drawable.ic_arrow_upward_24,
                        contentDescription = stringResource(R.string.move_up)
                    )
                    val isAtBottomEdge by remember {
                        derivedStateOf {
                            (selectionState.bottomRightState?.y ?: 0) >= (rowsState - 1) ||
                                (selectionState.topLeftState?.y ?: 0) >= (rowsState - 1)
                        }
                    }
                    IconMenuButton(
                        enabled = !isAtBottomEdge && enabled,
                        onClick = { onAction(Action.Move(Direction.DOWN)) },
                        drawableRes = R.drawable.ic_arrow_downward_24,
                        contentDescription = stringResource(R.string.move_down)
                    )
                }
                val isAtRightEdge by remember {
                    derivedStateOf {
                        (selectionState.bottomRightState?.x ?: 0) >= (columnsState - 1) ||
                            (selectionState.topLeftState?.x ?: 0) >= (columnsState - 1)
                    }
                }
                IconMenuButton(
                    enabled = !isAtRightEdge && enabled,
                    onClick = { onAction(Action.Move(Direction.RIGHT)) },
                    drawableRes = R.drawable.ic_arrow_forward_24,
                    contentDescription = stringResource(R.string.move_right)
                )
            }
            Spacer(modifier = Modifier.weight(1F))
            IconMenuButton(
                onClick = { onAction(Action.ClearSelect) },
                drawableRes = R.drawable.ic_close_24,
                contentDescription = stringResource(R.string.un_select)
            )
        }
    }
}

private fun handleAction(
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    transformerState: TransformerState,
    drawerState: DrawerState,
    selectionState: SelectionState,
    project: Project?,
    viewModel: BoxesViewModel,
    scope: CoroutineScope,
    context: Context,
    navController: DestinationsNavigator,
    action: Action
) {
    when (action) {
        is Action.Fill -> {
            viewModel.fill(
                action.point,
                action.layerId,
                ColorAndShape(project?.currentColor?.run { Color(this) } ?: Color.Green),
                project?.currentShape ?: Shape.Box,
                project?.columns ?: 0,
                project?.rows ?: 0
            )
        }
        is Action.Eraser -> buttonsState.toggleEraserSelected()
        is Action.SetTapType -> buttonsState.setTapType(action.tapType)
        is Action.Save -> {
            project?.let {
                saveProject(
                    canvasState,
                    it,
                    viewModel,
                    action.autoSave
                )
            }
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
            buttonsState.turnOffSelectionTool()
            viewModel.onUndo(canvasState.selectedLayer.id)
        }
        is Action.AddToHistory -> scope.launch {
            viewModel.addToHistory(action.historyItem)
        }
        is Action.ShowPngBackground -> viewModel.updateProjectShowPngBg(
            !(project?.showPngBg ?: false)
        )
        is Action.ShowGrid -> viewModel.updateProjectShowGrid(!(project?.showGrid ?: false))
        is Action.SetColor -> viewModel.updateProjectColor(action.color)
        is Action.SetShape -> viewModel.updateProjectShape(action.shape)
        is Action.ResetZoom -> transformerState.reset(scope)
        is Action.Edit -> {
            scope.launch { drawerState.close() }
            navController.navigate(CreateMainDestination(project?.id))
        }
        is Action.AddLayer ->
            viewModel.addLayer(
                name = action.name,
                index = (canvasState.layers.maxOf { it.index } ?: -1) + 1,
                selections = canvasState.selections
            )
        is Action.TurnOnOrOffLayer -> viewModel.setLayerOnOrOff(action.layerId, action.on)
        is Action.AddColorToUsedList -> scope.launch { viewModel.addUsedColor(action.color) }
        is Action.GoToLayerEdit -> project?.id?.let {
            navController.navigate(LayersEditMainDestination(it))
        }
        is Action.Export ->
            project?.let {
                viewModel.export(
                    it,
                    canvasState.selections,
                    canvasState.layers,
                    action.size,
                    action.exportType
                )
            }
        is Action.SelectTool -> buttonsState.toggleSelectTool()
        is Action.ClearSelect -> selectionState.clear()
        is Action.Move -> {
            scope.launch {
                safeLet(
                    selectionState.topLeftState,
                    selectionState.bottomRightState
                ) { tl, br ->
                    canvasState.move(
                        tl,
                        br,
                        action.direction
                    )?.let {
                        viewModel.addToHistory(it)
                        selectionState.move(action.direction)
                    }
                }
            }
        }
    }
}

private fun saveProject(
    canvasState: CanvasState,
    project: Project,
    viewModel: BoxesViewModel,
    autoSave: Boolean
) {
    viewModel.saveProject(
        project,
        if (autoSave) null else canvasState.boxes.keys.toList(),
        canvasState.selections,
        canvasState.layers
    )
}

@Composable
private fun rememberCanvasState(
    viewModel: BoxesViewModel,
    onError: (String?) -> Unit
): CanvasState {
    val layerState = viewModel.layerStateFlow.collectAsStateWithLifecycle(emptyList())
    val pixelsState = viewModel.pixelsFlow.collectAsStateWithLifecycle()
    val loadingState = viewModel.loadingState.collectAsStateWithLifecycle()
    val historyCountState = viewModel.historyCountFlow.collectAsStateWithLifecycle(0)
    return remember {
        CanvasState(
            layerState,
            viewModel.layersVisibilityList,
            viewModel.layersOrderStateList,
            loadingState,
            historyCountState,
            pixelsState,
            onError
        )
    }
}
