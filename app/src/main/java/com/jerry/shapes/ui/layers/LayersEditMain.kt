package com.jerry.shapes.ui.layers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jerry.shapes.R
import com.jerry.shapes.ui.common.*
import com.jerry.shapes.ui.layers.data.LayerEditUi
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.burnoutcrew.reorderable.*
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun LayersEditMain(
    projectId: Long,
    navController: DestinationsNavigator,
    viewModel: LayersEditViewModel = koinViewModel()
) {
    var showOpacity by rememberSaveable { mutableStateOf(false) }
    DefaultContainer(
        title = stringResource(R.string.edit),
        appBarActions = {
            IconSelectableMenuButton(
                onClick = { showOpacity = !showOpacity },
                isSelected = { showOpacity },
                contentDescription = stringResource(R.string.toggle_opacity_bg),
                drawableResOn = R.drawable.ic_opacity_off_24,
                drawableResOff = R.drawable.ic_opacity_on_24,
                tint = LocalContentColor.current
            )
        }
    ) {
        val projectState by viewModel.projectFlow.collectAsStateWithLifecycle()
        val list by rememberUpdatedState(
            remember(projectState) {
                mutableStateListOf(
                    *((projectState?.layers ?: emptyList()).toTypedArray())
                )
            }
        )
        val state = rememberReorderableLazyListState(
            onMove = { from, to ->
                val item = list[from.index]
                list.remove(item)
                list.add(to.index, item)
            },
            onDragEnd = { _, _ ->
                viewModel.setLayerIndicies(list.map { it.id to ((list.size - 1) - list.indexOf(it)) })
            }
        )
        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .reorderable(state)
                .detectReorderAfterLongPress(state)
                .fillMaxSize()
        ) {
            itemsIndexed(
                items = list,
                key = { _, layer -> layer.id }
            ) { index, layer ->
                ReorderableItem(state, key = layer.id) { isDragging ->
                    LayerItem(
                        state = state,
                        layer = layer,
                        showOpacity = { showOpacity },
                        showDivider = {
                            index > 0
                        },
                        onDeleteItem = {
                            viewModel.deleteLayer(
                                projectState!!.layers,
                                layer.id
                            )
                        },
                        showDeleteBtn = {
                            projectState!!.layers.size > 1
                        },
                        onLayerName = {
                            viewModel.setLayerName(layer.id, it)
                        },
                        showReorderBtn = {
                            list.size > 1
                        }
                    )
                }
            }
        }
        val projectNotNull by remember { derivedStateOf { projectState != null } }
        if (!projectNotNull) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.LayerItem(
    layer: LayerEditUi,
    state: ReorderableLazyListState,
    showOpacity: () -> Boolean,
    showReorderBtn: () -> Boolean,
    showDivider: () -> Boolean,
    showDeleteBtn: () -> Boolean,
    onDeleteItem: () -> Unit,
    onLayerName: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateItemPlacement()
    ) {
        if (showDivider()) {
            Divider()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1F)
                    .padding(16.dp),
                text = layer.name,
                style = MaterialTheme.typography.titleLarge,
                color = LocalContentColor.current
            )
            if (showReorderBtn()) {
                IconMenuButton(
                    modifier = Modifier
                        .detectReorder(state),
                    onClick = { /* no op */ },
                    contentDescription = stringResource(R.string.drag_layer),
                    drawableRes = R.drawable.ic_drag_indicator_24
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(CANVAS_SIZE)
                    .pngBackground(
                        visible = showOpacity(),
                        size = with(LocalDensity.current) { 10.dp.toPx() }
                    ),
                model = layer.image,
                contentDescription = null
            )
            var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
            var showNameDialog by rememberSaveable { mutableStateOf(false) }
            ButtonRow(
                onShowDeleteDialog = { showDeleteDialog = true },
                onShowNameDialog = { showNameDialog = true },
                showDeleteBtn = showDeleteBtn
            )

            if (showDeleteDialog) {
                AreYouSureDialog(
                    title = stringResource(R.string.are_you_sure_layer, layer.name),
                    dismiss = { showDeleteDialog = false },
                    onDelete = onDeleteItem
                )
            }

            if (showNameDialog) {
                SetNameDialog(
                    existingName = layer.name,
                    dismiss = { showNameDialog = false },
                    onName = onLayerName
                )
            }
        }
    }
}

@Composable
private fun RowScope.ButtonRow(
    showDeleteBtn: () -> Boolean,
    onShowNameDialog: () -> Unit,
    onShowDeleteDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .height(CANVAS_SIZE)
            .weight(1F)
    ) {
        IconMenuButton(
            onClick = onShowNameDialog,
            drawableRes = R.drawable.ic_edit_24,
            contentDescription = stringResource(R.string.change_layer_name)
        )
        if (showDeleteBtn()) {
            IconMenuButton(
                onClick = onShowDeleteDialog,
                drawableRes = R.drawable.ic_delete_24,
                contentDescription = stringResource(R.string.delete_layer)
            )
        }
    }
}

private val CANVAS_SIZE = 200.dp
