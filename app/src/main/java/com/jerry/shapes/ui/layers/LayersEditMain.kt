package com.jerry.shapes.ui.layers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jerry.shapes.R
import com.jerry.shapes.navigation.Navigator
import com.jerry.shapes.ui.common.AreYouSureDialog
import com.jerry.shapes.ui.common.DefaultContainer
import com.jerry.shapes.ui.common.IconMenuButton
import com.jerry.shapes.ui.common.IconSelectableMenuButton
import com.jerry.shapes.ui.common.ProjectImage
import com.jerry.shapes.ui.common.SetNameDialog
import com.jerry.shapes.ui.common.pngBackground
import com.jerry.shapes.ui.layers.data.LayerDialogState
import com.jerry.shapes.ui.layers.data.LayerEditUi
import com.jerry.shapes.ui.layers.data.LayerItemAction
import com.jerry.shapes.ui.layers.data.LayerUiAction
import org.koin.androidx.compose.koinViewModel
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun LayersEditMain(
    projectId: Long,
    navigator: Navigator,
    viewModel: LayersEditViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.init(projectId)
    }

    var showOpacity by rememberSaveable { mutableStateOf(false) }
    DefaultContainer(
        title = stringResource(R.string.edit_layers),
        appBarActions = {
            IconSelectableMenuButton(
                onClick = { showOpacity = !showOpacity },
                isSelected = { showOpacity },
                contentDescription = stringResource(R.string.toggle_opacity_bg),
                drawableResOn = R.drawable.ic_opacity_off_24,
                drawableResOff = R.drawable.ic_opacity_on_24,
                tint = LocalContentColor.current,
            )
        },
    ) {
        val projectState by viewModel.projectFlow.collectAsStateWithLifecycle()
        val list by rememberUpdatedState(
            remember(projectState) {
                mutableStateListOf(
                    *((projectState?.layers ?: emptyList()).toTypedArray()),
                )
            },
        )
        val lazyListState = rememberLazyListState()
        val state =
            rememberReorderableLazyListState(
                lazyListState = lazyListState,
                onMove = { from, to ->
                    val item = list[from.index]
                    list.remove(item)
                    list.add(to.index, item)
                },
            )

        LazyColumn(
            state = lazyListState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)),
        ) {
            itemsIndexed(
                items = list,
                key = { _, layer -> layer.id },
            ) { index, layer ->
                ReorderableItem(state = state, key = layer.id) { _ ->
                    LayerItem(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .animateItem(),
                        layer = layer,
                        onLayerUiAction = { action ->
                            when (action) {
                                LayerUiAction.ShowDivider -> index > 0
                                LayerUiAction.ShowDeleteBtn -> projectState!!.layers.size > 1
                                LayerUiAction.ShowReorderBtn -> list.size > 1
                                LayerUiAction.ShowOpacity -> showOpacity
                            }
                        },
                        onLayerItemAction = { action ->
                            when (action) {
                                is LayerItemAction.OnLayerName -> {
                                    viewModel.setLayerName(layer.id, action.name)
                                }
                                LayerItemAction.OnDeleteItem -> {
                                    viewModel.deleteLayer(
                                        projectState!!.layers,
                                        layer.id,
                                    )
                                }
                                LayerItemAction.OnDragEnd -> {
                                    viewModel.setLayerIndicies(
                                        list.map {
                                            it.id to (
                                                (list.size - 1) -
                                                    list.indexOf(
                                                        it,
                                                    )
                                            )
                                        },
                                    )
                                }
                            }
                        },
                    )
                }
            }
        }
        val projectNotNull by remember { derivedStateOf { projectState != null } }
        if (!projectNotNull) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReorderableCollectionItemScope.LayerItem(
    modifier: Modifier = Modifier,
    layer: LayerEditUi,
    onLayerUiAction: (LayerUiAction) -> Boolean,
    onLayerItemAction: (LayerItemAction) -> Unit,
) {
    Column(modifier = modifier) {
        if (onLayerUiAction(LayerUiAction.ShowDivider)) {
            HorizontalDivider()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier =
                    Modifier
                        .weight(1F)
                        .padding(16.dp),
                text = layer.name,
                style = MaterialTheme.typography.titleLarge,
                color = LocalContentColor.current,
            )
            if (onLayerUiAction(LayerUiAction.ShowReorderBtn)) {
                IconMenuButton(
                    modifier = Modifier.draggableHandle(onDragStopped = { onLayerItemAction(LayerItemAction.OnDragEnd) }),
                    onClick = { /* no op */ },
                    contentDescription = stringResource(R.string.drag_layer),
                    drawableRes = R.drawable.ic_drag_indicator_24,
                )
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
        ) {
            val context = LocalContext.current
            val request =
                ImageRequest
                    .Builder(context)
                    .data(layer.image)
                    .crossfade(true)
                    .build()
            ProjectImage(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .size(CANVAS_SIZE)
                        .pngBackground(
                            visible = onLayerUiAction(LayerUiAction.ShowOpacity),
                            size = with(LocalDensity.current) { 10.dp.toPx() },
                        ),
                imageRequest = request,
                contentScale = ContentScale.Fit,
            )

            val layerDialogState =
                rememberSaveable(saver = LayerDialogState.SAVER) {
                    LayerDialogState()
                }

            ButtonRow(
                onShowDeleteDialog = { layerDialogState.setShowDeleteDialog(true) },
                onShowNameDialog = { layerDialogState.setShowNameDialog(true) },
                showDeleteBtn = { onLayerUiAction(LayerUiAction.ShowDeleteBtn) },
            )

            LayerDialogs(
                state = layerDialogState,
                layerName = { layer.name },
                onLayerItemAction = onLayerItemAction,
            )
        }
    }
}

@Composable
private fun LayerDialogs(
    state: LayerDialogState,
    layerName: () -> String,
    onLayerItemAction: (LayerItemAction) -> Unit,
) {
    if (state.showDeleteDialogState) {
        AreYouSureDialog(
            title = stringResource(R.string.are_you_sure_layer, layerName()),
            dismiss = { state.setShowDeleteDialog(false) },
            onDelete = { onLayerItemAction(LayerItemAction.OnDeleteItem) },
        )
    }

    if (state.showNameDialogState) {
        SetNameDialog(
            existingName = layerName(),
            dismiss = { state.setShowNameDialog(false) },
            onName = { onLayerItemAction(LayerItemAction.OnLayerName(it)) },
        )
    }
}

@Composable
private fun RowScope.ButtonRow(
    showDeleteBtn: () -> Boolean,
    onShowNameDialog: () -> Unit,
    onShowDeleteDialog: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .height(CANVAS_SIZE)
                .weight(1F),
    ) {
        IconMenuButton(
            onClick = onShowNameDialog,
            drawableRes = R.drawable.ic_edit_24,
            contentDescription = stringResource(R.string.change_layer_name),
        )
        if (showDeleteBtn()) {
            IconMenuButton(
                onClick = onShowDeleteDialog,
                drawableRes = R.drawable.ic_delete_24,
                contentDescription = stringResource(R.string.delete_layer),
            )
        }
    }
}

private val CANVAS_SIZE = 200.dp
