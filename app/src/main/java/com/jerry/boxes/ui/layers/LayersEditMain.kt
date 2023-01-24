package com.jerry.boxes.ui.layers

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jerry.boxes.R
import com.jerry.boxes.ui.common.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun LayersEditMain(
    projectId: Long,
    navController: DestinationsNavigator,
    viewModel: LayersEditViewModel = koinViewModel()
) {
    DefaultContainer(title = stringResource(R.string.edit)) {
        val projectState by viewModel.projectFlow.collectAsStateWithLifecycle()
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = projectState?.layers ?: emptyList(),
                key = { _, layer -> layer.id }
            ) { index, layer ->
                LayerItem(
                    layer = layer,
                    showDownArrow = {
                        layer.index > 0
                    },
                    showUpArrow = {
                        val max = projectState?.layers?.maxBy { it.index }?.index ?: 0
                        layer.index < max
                    },
                    showDivider = {
                        index > 0
                    },
                    onMoveItem = { lyr, position ->
                        viewModel.changeLayerIndex(
                            projectState!!.layers,
                            lyr,
                            position
                        )
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
                    }
                )
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
    showUpArrow: () -> Boolean,
    showDownArrow: () -> Boolean,
    showDivider: () -> Boolean,
    showDeleteBtn: () -> Boolean,
    onDeleteItem: () -> Unit,
    onLayerName: (String) -> Unit,
    onMoveItem: (Long, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateItemPlacement()
    ) {
        if (showDivider()) {
            Divider()
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = layer.name,
            style = MaterialTheme.typography.titleLarge,
            color = LocalContentColor.current
        )
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
                        visible = true,
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
            Column(
                modifier = Modifier.height(CANVAS_SIZE)
            ) {
                IconMenuButton(
                    modifier = Modifier.alpha(if (showUpArrow()) 1F else 0F),
                    onClick = { onMoveItem(layer.id, layer.index + 1) },
                    drawableRes = R.drawable.ic_arrow_upward_24,
                    contentDescription = stringResource(R.string.move_layer_up)
                )
                Spacer(modifier = Modifier.weight(1F))
                IconMenuButton(
                    modifier = Modifier.alpha(if (showDownArrow()) 1F else 0F),
                    onClick = { onMoveItem(layer.id, layer.index - 1) },
                    drawableRes = R.drawable.ic_arrow_downward_24,
                    contentDescription = stringResource(R.string.move_layer_down)
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
