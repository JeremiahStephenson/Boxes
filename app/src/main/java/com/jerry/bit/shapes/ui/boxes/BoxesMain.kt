package com.jerry.bit.shapes.ui.boxes

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.jerry.bit.shapes.R
import com.jerry.bit.shapes.cache.data.ColorAndShape
import com.jerry.bit.shapes.cache.data.Project
import com.jerry.bit.shapes.extensions.openImage
import com.jerry.bit.shapes.extensions.openShareSheet
import com.jerry.bit.shapes.navigation.Navigator
import com.jerry.bit.shapes.ui.boxes.data.Action
import com.jerry.bit.shapes.ui.boxes.data.UiEvent
import com.jerry.bit.shapes.ui.boxes.history.UserHistory
import com.jerry.bit.shapes.ui.boxes.state.ButtonsState
import com.jerry.bit.shapes.ui.boxes.state.CanvasState
import com.jerry.bit.shapes.ui.boxes.state.SelectionState
import com.jerry.bit.shapes.ui.boxes.state.TransformerState
import com.jerry.bit.shapes.ui.boxes.state.enums.ActiveTool
import com.jerry.bit.shapes.ui.boxes.state.enums.Direction
import com.jerry.bit.shapes.ui.boxes.state.enums.TapType
import com.jerry.bit.shapes.ui.common.DefaultContainer
import com.jerry.bit.shapes.ui.common.DrawerContainer
import com.jerry.bit.shapes.ui.common.FadeAnimatedVisibility
import com.jerry.bit.shapes.ui.common.IconMenuButton
import com.jerry.bit.shapes.ui.common.ShapeOption
import com.jerry.bit.shapes.ui.common.unboundClickable
import com.jerry.bit.shapes.ui.create.CreateNavKey
import com.jerry.bit.shapes.ui.howto.HowToNavKey
import com.jerry.bit.shapes.ui.layers.LayersEditNavKey
import com.jerry.bit.shapes.ui.shapes.Shape
import com.jerry.bit.shapes.util.ExportType
import com.jerry.bit.shapes.util.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun BoxesMain(
    projectId: Long,
    projectName: String?,
    navigator: Navigator,
    viewModel: BoxesViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.init(projectId)
    }

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
    val canvasState = rememberCanvasState(viewModel)

    val buttonsState =
        rememberSaveable(saver = ButtonsState.SAVER) {
            ButtonsState(
                eraserSelected = false,
                selectToolSelected = false,
            )
        }

    val selectionState = rememberSaveable(saver = SelectionState.SAVER) { SelectionState() }
    val isFirstProjectGuideAvailable by
        viewModel.isFirstProjectGuideAvailable.collectAsStateWithLifecycle(initialValue = false)
    var showFirstProjectGuide by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(isFirstProjectGuideAvailable) {
        if (isFirstProjectGuideAvailable) {
            showFirstProjectGuide = true
            viewModel.markFirstProjectGuideShown()
        }
    }
    DefaultContainer(
        title = project?.name ?: projectName ?: "",
        appBarActions = {
            if (showFirstProjectGuide) {
                Icon(
                    modifier =
                        Modifier
                            .unboundClickable {
                                navigator.navigate(HowToNavKey)
                            }.padding(16.dp),
                    painter = painterResource(R.drawable.ic_help_24),
                    contentDescription = null,
                )
            }
            Icon(
                modifier =
                    Modifier
                        .unboundClickable {
                            navigator.navigate(LayersEditNavKey(projectId))
                        }.padding(16.dp),
                painter = painterResource(R.drawable.ic_layers_24),
                contentDescription = null,
            )
            Icon(
                modifier =
                    Modifier
                        .unboundClickable {
                            scope.launch {
                                when (drawerState.isOpen) {
                                    true -> drawerState.close()
                                    else -> drawerState.open()
                                }
                            }
                        }.padding(16.dp),
                painter = painterResource(R.drawable.ic_menu_24),
                contentDescription = null,
            )
        },
    ) {
        val transformerState =
            rememberSaveable(saver = TransformerState.SAVER) { TransformerState() }
        val handleAction: (Action) -> Unit =
            remember {
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
                        navigator,
                        it,
                    )
                }
            }
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        ) {
            DrawerContainer(
                drawerState = drawerState,
                drawerContent = {
                    if (projectNotNull) {
                        DrawerMenu(
                            buttonsState = buttonsState,
                            onAction = handleAction,
                            canvasState = canvasState,
                            getProject = { project!! },
                        )
                    }
                },
            ) {
                if (projectNotNull) {
                    MainCanvas(
                        project = project!!,
                        canvasState = canvasState,
                        buttonsState = buttonsState,
                        selectionState = selectionState,
                        transformerState = transformerState,
                        onAction = handleAction,
                        getUsedColorList = { viewModel.usedColors },
                    )
                }
            }
            FadeAnimatedVisibility(
                modifier = Modifier.align(Alignment.Center),
                visible = canvasState.isLoading,
            ) {
                CircularProgressIndicator()
            }
            val snackBarHostState = remember { SnackbarHostState() }
            SnackBarImageLocator(snackBarHostState = snackBarHostState)
            LaunchedEffect(Unit) {
                viewModel.uiEventFlow.collectLatest {
                    when (it) {
                        is UiEvent.Error ->
                            Toast
                                .makeText(context, it.error ?: genericError, Toast.LENGTH_LONG)
                                .show()

                        is UiEvent.Export ->
                            when (it.exportType) {
                                ExportType.FILE ->
                                    it.filePath?.let { path ->
                                        snackBarHostState.showSnackbar(
                                            path,
                                            duration = SnackbarDuration.Indefinite,
                                        )
                                    }

                                else -> {
                                    it.filePath?.let { context.openShareSheet(it) }
                                }
                            }

                        is UiEvent.MoveSelection -> {
                            selectionState.move(it.direction)
                        }
                        UiEvent.ProjectExported -> {
                            Toast.makeText(context, R.string.project_exported, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.SnackBarImageLocator(snackBarHostState: SnackbarHostState) {
    val context = LocalContext.current
    SnackbarHost(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
        hostState = snackBarHostState,
        snackbar = { snackBarData ->
            Snackbar(
                action = {
                    Row {
                        Button(
                            onClick = {
                                context.openImage(snackBarData.visuals.message)
                                snackBarHostState.currentSnackbarData?.dismiss()
                            },
                        ) {
                            Text(stringResource(R.string.view))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                snackBarHostState.currentSnackbarData?.dismiss()
                            },
                        ) {
                            Text(stringResource(R.string.dismiss))
                        }
                    }
                },
                modifier = Modifier.padding(8.dp),
            ) { Text(text = snackBarData.visuals.message) }
        },
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
    onAction: (Action) -> Unit,
) {
    var size by remember { mutableStateOf(Size(0F, 0F)) }
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val density = LocalDensity.current
        val strokeWidth = remember { with(density) { 2.dp.toPx() } }
        val buttonBarOffset = remember { with(density) { 56.dp.toPx() } }

        LaunchedEffect(project.rows, project.columns, size) {
            canvasState.fillInBoxes(
                size,
                buttonBarOffset,
                project.columns,
                project.rows,
            )
        }

        LifecycleResumeEffect(Unit) {
            onPauseOrDispose {
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
                onSizeChanged = {
                    size = it
                },
                onTap = { point ->
                    if (canvasState.hasLayersTurnedOn) {
                        when (buttonsState.activeToolState) {
                            ActiveTool.EYEDROPPER ->
                                canvasState.getCurrentSelection(point)?.let {
                                    onAction(Action.SetColor(it))
                                    onAction(Action.AddColorToUsedList(it))
                                }

                            ActiveTool.DRAW -> {
                                if (!canvasState.isLoading) {
                                    onAction(
                                        Action.AddToHistory(
                                            canvasState.getTapHistoryItem(point, currentLayer),
                                        ),
                                    )
                                    canvasState.onTap(
                                        point,
                                        currentLayer,
                                        projectState.colorAndShape,
                                        projectState.currentShape,
                                    )
                                }
                            }

                            ActiveTool.ERASER -> {
                                if (!canvasState.isLoading) {
                                    onAction(
                                        Action.AddToHistory(
                                            canvasState.getTapHistoryItem(point, currentLayer),
                                        ),
                                    )
                                    canvasState.onDrag(hashSetOf(point), currentLayer, null)
                                }
                            }

                            ActiveTool.FILL -> onAction(Action.Fill(point, currentLayer))
                            ActiveTool.SELECT -> Unit
                        }
                    }
                },
                onDrag = {
                    if (
                        canvasState.hasLayersTurnedOn &&
                        !canvasState.isLoading &&
                        (
                            buttonsState.activeToolState == ActiveTool.DRAW ||
                                buttonsState.activeToolState == ActiveTool.ERASER
                        )
                    ) {
                        val color =
                            projectState.colorAndShape
                                .copy(shape = projectState.currentShape)
                        canvasState.addToDragHistory(
                            it,
                            currentLayer,
                            if (buttonsState.eraserSelectedState) null else color,
                        )
                        canvasState.onDrag(
                            it,
                            currentLayer,
                            if (buttonsState.eraserSelectedState) null else color,
                        )
                    }
                },
                onDragStart = {},
                onDragEnd = {
                    if (
                        canvasState.hasLayersTurnedOn &&
                        !canvasState.isLoading &&
                        (
                            buttonsState.activeToolState == ActiveTool.DRAW ||
                                buttonsState.activeToolState == ActiveTool.ERASER
                        )
                    ) {
                        onAction(
                            Action.AddToHistory(
                                canvasState.closeDragHistory(currentLayer),
                            ),
                        )
                    }
                },
            )
        }

        ButtonBar(
            getColor = { project.colorAndShape },
            getShape = { project.currentShape },
            buttonsState = buttonsState,
            canvasState = canvasState,
            transformerState = transformerState,
            onColorChosen = {
                buttonsState.eraserSelectedState = false
                onAction(Action.SetColor(it))
                onAction(Action.AddColorToUsedList(it))
            },
            onShapeChosen = {
                buttonsState.eraserSelectedState = false
                onAction(Action.SetShape(it))
            },
            onAction = onAction,
            getUsedColorList = getUsedColorList,
        )

        AdditionalButtonBar(
            canvasState = canvasState,
            buttonsState = buttonsState,
            selectionState = selectionState,
            columns = project.columns,
            rows = project.rows,
            onAction = onAction,
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
    onShapeChosen: (Shape) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .height(56.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8F))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                .fillMaxWidth(),
    ) {
        var colorPicker by rememberSaveable { mutableStateOf(false) }
        if (colorPicker) {
            ColorPickerDialog(
                color = getColor(),
                usedColors = getUsedColorList(),
                onColorChosen = onColorChosen,
            ) {
                colorPicker = false
            }
        }

        var shapePicker by rememberSaveable { mutableStateOf(false) }
        if (shapePicker) {
            ShapePickerDialog(
                color = getColor(),
                numberOfBoxes = canvasState.numberOfBoxes,
                onShapeChosen = onShapeChosen,
            ) {
                shapePicker = false
            }
        }

        ColorSelectorButton(
            onClick = { colorPicker = true },
            color = getColor(),
        )

        ShapeOption(
            shape = getShape(),
            color = getColor(),
            showToolTip = true,
        ) {
            shapePicker = true
        }

        ActiveToolMenuItem(
            buttonsState = buttonsState,
            onAction = onAction,
        )

        Spacer(modifier = Modifier.weight(1F))

        val historyEnabled by remember { derivedStateOf { canvasState.historyCount > 0 } }
        IconMenuButton(
            enabled = historyEnabled,
            onClick = { onAction(Action.Undo) },
            drawableRes = R.drawable.ic_undo_24,
            contentDescription = stringResource(R.string.undo_history),
        )

        Spacer(modifier = Modifier.weight(1F))

        val isTransformed by remember { derivedStateOf { transformerState.scaleState > 1F || transformerState.offsetState != Offset.Zero } }
        IconMenuButton(
            enabled = isTransformed,
            onClick = { onAction(Action.ResetZoom) },
            drawableRes = R.drawable.ic_zoom_out_map_24,
            contentDescription = stringResource(R.string.re_center),
        )
    }
}

@Composable
private fun ActiveToolMenuItem(
    buttonsState: ButtonsState,
    onAction: (Action) -> Unit,
) {
    var toolMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val useCompactMenu =
        !windowSizeClass.isHeightAtLeastBreakpoint(
            WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND,
        )
    val activeToolIcon by remember {
        derivedStateOf {
            when (buttonsState.activeToolState) {
                ActiveTool.DRAW -> R.drawable.ic_brush_24
                ActiveTool.EYEDROPPER -> R.drawable.ic_colorize_24
                ActiveTool.FILL -> R.drawable.ic_format_color_fill_24
                ActiveTool.ERASER -> R.drawable.ic_eraser_on_24
                ActiveTool.SELECT -> R.drawable.ic_select_all_24
            }
        }
    }
    Box {
        IconMenuButton(
            onClick = { toolMenuExpanded = true },
            drawableRes = activeToolIcon,
            contentDescription = stringResource(R.string.choose_active_tool),
        )
        DropdownMenu(
            expanded = toolMenuExpanded,
            onDismissRequest = { toolMenuExpanded = false },
        ) {
            val options =
                listOf(
                    ActiveToolOption(stringResource(R.string.tool_draw), R.drawable.ic_brush_24) {
                        buttonsState.setTapType(TapType.TAP)
                    },
                    ActiveToolOption(
                        stringResource(R.string.tool_eraser),
                        R.drawable.ic_eraser_on_24,
                    ) {
                        if (!buttonsState.eraserSelectedState) onAction(Action.Eraser)
                    },
                    ActiveToolOption(
                        stringResource(R.string.tool_fill),
                        R.drawable.ic_format_color_fill_24,
                    ) {
                        buttonsState.setTapType(TapType.FILL)
                    },
                    ActiveToolOption(
                        stringResource(R.string.tool_eyedropper),
                        R.drawable.ic_colorize_24,
                    ) {
                        buttonsState.setTapType(TapType.PICKER)
                    },
                    ActiveToolOption(
                        stringResource(R.string.tool_select_and_move),
                        R.drawable.ic_select_all_24,
                    ) {
                        if (!buttonsState.selectToolSelectedState) onAction(Action.SelectTool)
                    },
                )
            if (useCompactMenu) {
                Column {
                    options.chunked(2).forEach { rowOptions ->
                        Row {
                            rowOptions.forEach { option ->
                                ActiveToolMenuItem(
                                    option = option,
                                    modifier = Modifier.width(168.dp),
                                    onMenuDismiss = { toolMenuExpanded = false },
                                )
                            }
                        }
                    }
                }
            } else {
                options.forEach { option ->
                    ActiveToolMenuItem(
                        option = option,
                        onMenuDismiss = { toolMenuExpanded = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSelectorButton(
    color: ColorAndShape,
    onClick: () -> Unit,
) {
    Box {
        IconMenuButton(
            onClick = onClick,
            drawableRes = R.drawable.ic_color_lens_24,
            contentDescription = stringResource(R.string.color_selector),
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 7.dp, bottom = 7.dp)
                    .size(14.dp)
                    .background(color.color, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
        )
    }
}

private data class ActiveToolOption(
    val label: String,
    @param:DrawableRes val drawableRes: Int,
    val onClick: () -> Unit,
)

@Composable
private fun ActiveToolMenuItem(
    option: ActiveToolOption,
    modifier: Modifier = Modifier,
    onMenuDismiss: () -> Unit,
) {
    DropdownMenuItem(
        modifier = modifier,
        text = { Text(option.label) },
        onClick = {
            option.onClick()
            onMenuDismiss()
        },
        leadingIcon = {
            Icon(
                painter = painterResource(option.drawableRes),
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun BoxScope.AdditionalButtonBar(
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    selectionState: SelectionState,
    columns: Int,
    rows: Int,
    onAction: (Action) -> Unit,
) {
    val columnsState by rememberUpdatedState(columns)
    val rowsState by rememberUpdatedState(rows)
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (!buttonsState.selectToolSelectedState) return

    val moveControlsState =
        MoveControlsState(
            enabled = selectionState.bottomRightState != null && selectionState.topLeftState != null,
            atLeftEdge =
                (selectionState.bottomRightState?.x ?: 0) <= 0 ||
                    (selectionState.topLeftState?.x ?: 0) <= 0,
            atTopEdge =
                (selectionState.bottomRightState?.y ?: 0) <= 0 ||
                    (selectionState.topLeftState?.y ?: 0) <= 0,
            atBottomEdge =
                (selectionState.bottomRightState?.y ?: 0) >= (rowsState - 1) ||
                    (selectionState.topLeftState?.y ?: 0) >= (rowsState - 1),
            atRightEdge =
                (selectionState.bottomRightState?.x ?: 0) >= (columnsState - 1) ||
                    (selectionState.topLeftState?.x ?: 0) >= (columnsState - 1),
        )
    val onMove: (Direction) -> Unit = { direction ->
        onAction(Action.Move(canvasState.selectedLayer.id, direction))
    }

    if (isLandscape) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                    .padding(end = 8.dp)
                    .size(176.dp)
                    .selectionControlsContainer(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MoveControls(moveControlsState, onMove)
            Row {
                SelectModeButton(onAction)
                ClearSelectionButton(onAction)
            }
        }
    } else {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, start = 8.dp, end = 8.dp)
                    .selectionControlsContainer(28.dp),
            verticalAlignment = Alignment.Top,
        ) {
            SelectModeButton(onAction)
            Spacer(modifier = Modifier.weight(1F))
            MoveControls(moveControlsState, onMove)
            Spacer(modifier = Modifier.weight(1F))
            ClearSelectionButton(onAction)
        }
    }
}

@Composable
private fun Modifier.selectionControlsContainer(cornerRadius: Dp): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return background(
        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.65F),
        shape,
    ).border(
        1.dp,
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8F),
        shape,
    )
}

private data class MoveControlsState(
    val enabled: Boolean,
    val atLeftEdge: Boolean,
    val atTopEdge: Boolean,
    val atBottomEdge: Boolean,
    val atRightEdge: Boolean,
)

@Composable
private fun MoveControls(
    state: MoveControlsState,
    onMove: (Direction) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconMenuButton(
            enabled = !state.atLeftEdge && state.enabled,
            onClick = { onMove(Direction.LEFT) },
            drawableRes = R.drawable.ic_arrow_back_24,
            contentDescription = stringResource(R.string.move_left),
        )
        Column {
            IconMenuButton(
                enabled = !state.atTopEdge && state.enabled,
                onClick = { onMove(Direction.UP) },
                drawableRes = R.drawable.ic_arrow_upward_24,
                contentDescription = stringResource(R.string.move_up),
            )
            IconMenuButton(
                enabled = !state.atBottomEdge && state.enabled,
                onClick = { onMove(Direction.DOWN) },
                drawableRes = R.drawable.ic_arrow_downward_24,
                contentDescription = stringResource(R.string.move_down),
            )
        }
        IconMenuButton(
            enabled = !state.atRightEdge && state.enabled,
            onClick = { onMove(Direction.RIGHT) },
            drawableRes = R.drawable.ic_arrow_forward_24,
            contentDescription = stringResource(R.string.move_right),
        )
    }
}

@Composable
private fun SelectModeButton(onAction: (Action) -> Unit) {
    IconMenuButton(
        onClick = { onAction(Action.SelectTool) },
        drawableRes = R.drawable.ic_select_all_24,
        contentDescription = stringResource(R.string.turn_off_select_and_move),
    )
}

@Composable
private fun ClearSelectionButton(onAction: (Action) -> Unit) {
    IconMenuButton(
        onClick = { onAction(Action.ClearSelect) },
        drawableRes = R.drawable.ic_close_24,
        contentDescription = stringResource(R.string.un_select),
    )
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
    navigator: Navigator,
    action: Action,
) {
    when (action) {
        is Action.Fill -> {
            viewModel.fill(
                action.point,
                action.layerId,
                ColorAndShape(project?.currentColor ?: Color.Green.value.toInt()),
                project?.currentShape ?: Shape.Box,
                project?.columns ?: 0,
                project?.rows ?: 0,
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
                    action.autoSave,
                )
            }
        }

        is Action.SelectLayer -> viewModel.selectLayer(action.layerId)
        is Action.Clear ->
            if (canvasState.hasLayersTurnedOn) {
                scope.launch {
                    viewModel.addToHistory(
                        UserHistory(
                            canvasState.selectedLayer.id,
                            canvasState.getCurrentSelectedLayerSelections(canvasState.selectedLayer.id),
                        ),
                    )
                    canvasState.clear()
                }
            }

        is Action.Undo ->
            scope.launch {
                buttonsState.selectToolSelectedState = false
                viewModel.onUndo(canvasState.selectedLayer.id)
            }

        is Action.AddToHistory ->
            scope.launch {
                viewModel.addToHistory(action.historyItem)
            }

        is Action.ShowPngBackground ->
            viewModel.updateProjectShowPngBg(
                !(project?.showPngBg ?: false),
            )

        is Action.ShowGrid -> viewModel.updateProjectShowGrid(!(project?.showGrid ?: false))
        is Action.SetColor -> viewModel.updateProjectColor(action.color)
        is Action.SetShape -> viewModel.updateProjectShape(action.shape)
        is Action.ResetZoom -> transformerState.reset(scope)
        is Action.Edit -> {
            scope.launch { drawerState.close() }
            navigator.navigate(CreateNavKey(project?.id))
        }

        is Action.AddLayer ->
            viewModel.addLayer(
                name = action.name,
                index = canvasState.layers.maxOf { it.index } + 1,
                canvasState = canvasState,
            )

        is Action.TurnOnOrOffLayer -> viewModel.setLayerOnOrOff(action.layerId, action.on)
        is Action.AddColorToUsedList -> scope.launch { viewModel.addUsedColor(action.color) }
        is Action.GoToLayerEdit ->
            project?.id?.let {
                navigator.navigate(LayersEditNavKey(it))
            }

        is Action.GoToHowTo -> navigator.navigate(HowToNavKey)
        is Action.Export ->
            project?.let {
                viewModel.export(
                    it,
                    canvasState.selections,
                    canvasState.layers,
                    action.size,
                    action.exportType,
                )
            }

        is Action.SelectTool -> buttonsState.toggleSelectTool()
        is Action.ClearSelect -> selectionState.clear()
        is Action.Move -> {
            viewModel.move(
                action.layerId,
                selectionState.topLeftState,
                selectionState.bottomRightState,
                action.direction,
            )
        }

        is Action.ImageImport ->
            viewModel.importImage(
                context,
                action.layerId,
                project?.columns,
                project?.rows,
                action.uri,
            )

        is Action.ExportProject ->
            project?.let {
                viewModel.exportProject(
                    project = it,
                    layers = canvasState.layers,
                    selections = canvasState.selections,
                    destinationFolder = action.destinationFolder,
                )
            }
    }
}

private fun saveProject(
    canvasState: CanvasState,
    project: Project,
    viewModel: BoxesViewModel,
    autoSave: Boolean,
) {
    viewModel.saveProject(
        project,
        canvasState,
        autoSave,
    )
}

@Composable
private fun rememberCanvasState(viewModel: BoxesViewModel): CanvasState {
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
        )
    }
}
