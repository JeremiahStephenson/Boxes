package com.jerry.boxes.ui.boxes

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.google.accompanist.flowlayout.FlowRow
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.cache.data.ProjectAndPixel
import com.jerry.boxes.extensions.asList
import com.jerry.boxes.extensions.asSerializableColor
import com.jerry.boxes.ui.boxes.state.*
import com.jerry.boxes.ui.common.*
import com.jerry.boxes.ui.destinations.CreateMainDestination
import com.jerry.boxes.util.ArrangementLastItem
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
    val project = viewModel.projectFlow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val canvasState = remember { CanvasState() }
    val buttonsState by rememberSaveable {
        mutableStateOf(
            ButtonsState(
                eraserSelected = false,
                showPngBackground = false
            )
        )
    }
    val layerState by rememberSaveable(project.value?.project?.layers) {
        mutableStateOf(LayerState(project.value?.project?.layers ?: 1))
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
                    project.value?.project,
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
                    layerCount = project.value?.project?.layers ?: 1,
                )
            }) {
            project.value?.let { project ->
                MainCanvas(
                    project = project,
                    canvasState = canvasState,
                    buttonsState = buttonsState,
                    transformerState = transformerState,
                    layerState = layerState,
                    onAction = handleAction
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
    transformerState: TransformerState,
    layerState: LayerState,
    onAction: (Action) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        val density = LocalDensity.current
        val strokeWidth = remember { with(density) { 2.dp.roundToPx() }.toFloat() }
        val buttonBarOffset = remember { with(density) { 56.dp.roundToPx() } }

        var currentColor by rememberSaveable { mutableStateOf(Color.Green.asSerializableColor) }

        val size = this.constraints
        LaunchedEffect(project) {
            canvasState.fillInSelections(project.pixels)
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
            if (buttonsState.showPngBackgroundState) {
                PngBackground()
            }
            BoxCanvas(
                canvasState = canvasState,
                rows = project.project.rows,
                columns = project.project.columns,
                layers = layerState.selectedLayersState,
                scale = scale,
                offset = offset,
                size = size,
                strokeWidth = strokeWidth,
                state = state,
                showPngBackgroundSelected = { buttonsState.showPngBackgroundState },
                onTap = { canvasState.onTap(it, layerState.selectedLayersState.max, currentColor) },
                onDrag = {
                    canvasState.onDrag(
                        it,
                        layerState.selectedLayersState.max,
                        currentColor,
                        buttonsState.eraserSelectedState
                    )
                }
            )
        }

        ButtonBar(
            color = currentColor,
            onColorChosen = {
                currentColor = it
            },
            onAction = onAction
        )
    }
}

@Composable
private fun DrawerMenu(
    layerCount: Int,
    buttonsState: ButtonsState,
    onAction: (Action) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = remember { ArrangementLastItem() }
    ) {
        item {
            ButtonSection(R.string.tools) {
                IconSelectableMenuButton(
                    onClick = { onAction(Action.Eraser) },
                    isSelected = { buttonsState.eraserSelectedState },
                    drawableResOn = R.drawable.ic_baseline_eraser_on_24,
                    drawableResOff = R.drawable.ic_baseline_eraser_off_24
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.ShowPngBackground) },
                    isSelected = { buttonsState.showPngBackgroundState },
                    drawableResOn = R.drawable.ic_baseline_grid_on_24,
                    drawableResOff = R.drawable.ic_baseline_grid_off_24
                )
            }
        }
        item {
            ButtonHeader(R.string.layers)
        }

        items(layerCount) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = stringResource(R.string.layer_value, it + 1)
                )
                Switch(
                    checked = true,
                    onCheckedChange = {

                    })
            }
        }

        item {
            ButtonSection(R.string.export) {
                IconMenuButton(
                    onClick = { onAction(Action.Export) },
                    drawableRes = R.drawable.ic_baseline_image_24
                )
            }
        }
        item {
            ButtonSection(R.string.save) {
                IconMenuButton(
                    onClick = { onAction(Action.Save(false)) },
                    drawableRes = R.drawable.ic_baseline_save_24
                )
                IconMenuButton(
                    onClick = { onAction(Action.Edit) },
                    drawableRes = R.drawable.ic_baseline_edit_24
                )
            }
        }
        item {
            ButtonSection(R.string.clear) {
                IconMenuButton(
                    onClick = { onAction(Action.Clear) },
                    drawableRes = R.drawable.ic_auto_renew
                )
            }
        }
    }
}

@Composable
private fun ButtonSection(
    @StringRes title: Int,
    content: @Composable () -> Unit
) {
    ButtonHeader(title = title)
    FlowRow(content = content)
}

@Composable
private fun ButtonHeader(@StringRes title: Int) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        text = stringResource(title)
    )
    Divider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun ButtonBar(
    color: SerializableColor,
    onAction: (Action) -> Unit,
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
            onClick = { onAction(Action.ResetZoom) },
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

private fun handleAction(
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    transformerState: TransformerState,
    drawerState: DrawerState,
    viewModel: BoxesViewModel,
    project: Project?,
    scope: CoroutineScope,
    cc: CoroutineContextProvider,
    rootView: View,
    navController: DestinationsNavigator,
    action: Action
) {
    ArrayList<Int>(5)
    when (action) {
        is Action.Eraser -> buttonsState.toggleEraserSelected()
        is Action.Save -> viewModel.save(
            if (action.autoSave) null else canvasState.boxes.keys.toList(),
            canvasState.selections.toMap()
        )
        is Action.Clear -> canvasState.clear()
        is Action.ShowPngBackground -> buttonsState.toggleShowPngBackground()
        is Action.ResetZoom -> transformerState.reset(scope)
        is Action.Edit -> {
            scope.launch { drawerState.close() }
            navController.navigate(CreateMainDestination(project?.id))
        }
        is Action.Export -> exportCanvas(
            rootView = rootView,
            rows = project?.rows ?: 0,
            columns = project?.columns ?: 0,
            layers = LayerList(1.rangeTo(project?.layers ?: 1).toCollection(ArrayList())),
            selections = canvasState.selections,
            cc = cc
        )
    }
}


