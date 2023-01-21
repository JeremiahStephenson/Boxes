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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.LayerAndPixel
import com.jerry.boxes.extensions.asList
import com.jerry.boxes.ui.boxes.SelectionsBoxes
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.generateBoxes
import com.jerry.boxes.ui.boxes.generateSelectionsMap
import com.jerry.boxes.ui.common.*
import com.jerry.boxes.util.ImmutableList
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import kotlin.math.max
import kotlin.math.roundToInt

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
                key = { _, layer -> layer.layer.id }
            ) { index, layer ->
                LayerItem(
                    layer = layer,
                    columns = projectState!!.project.columns,
                    rows = projectState!!.project.rows,
                    showDownArrow = {
                        layer.layer.index > 0
                    },
                    showUpArrow = {
                        val max = projectState?.layers?.maxBy { it.layer.index }?.layer?.index ?: 0
                        layer.layer.index < max
                    },
                    showDivider = {
                        index > 0
                    },
                    onMoveItem = { lyr, position ->
                        viewModel.changeLayerIndex(
                            projectState!!.layers.map { it.layer },
                            lyr,
                            position
                        )
                    },
                    onDeleteItem = {
                        viewModel.deleteLayer(
                            projectState!!.layers.map { it.layer },
                            layer.layer.id
                        )
                    },
                    showDeleteBtn = {
                        projectState!!.layers.size > 1
                    },
                    onLayerName = {
                        viewModel.setLayerName(layer.layer.id, it)
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
    layer: LayerAndPixel,
    columns: Int,
    rows: Int,
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
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            text = layer.layer.name,
            style = MaterialTheme.typography.titleLarge,
            color = LocalContentColor.current
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            CanvasItem(
                columns = columns,
                rows = rows,
                layer = layer
            )
            Spacer(modifier = Modifier.weight(1F))
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(CANVAS_SIZE)
            ) {
                IconMenuButton(
                    modifier = Modifier.alpha(if (showUpArrow()) 1F else 0F),
                    onClick = { onMoveItem(layer.layer.id, layer.layer.index + 1) },
                    drawableRes = R.drawable.ic_arrow_upward_24,
                    contentDescription = stringResource(R.string.move_layer_up)
                )
                Spacer(modifier = Modifier.weight(1F))
                IconMenuButton(
                    modifier = Modifier.alpha(if (showDownArrow()) 1F else 0F),
                    onClick = { onMoveItem(layer.layer.id, layer.layer.index - 1) },
                    drawableRes = R.drawable.ic_arrow_downward_24,
                    contentDescription = stringResource(R.string.move_layer_down)
                )
            }
        }
        var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
        var showNameDialog by rememberSaveable { mutableStateOf(false) }
        ButtonRow(
            onShowDeleteDialog = { showDeleteDialog = true },
            onShowNameDialog = { showNameDialog = true },
            showDeleteBtn = showDeleteBtn
        )

        if (showDeleteDialog) {
            AreYouSureDialog(
                title = stringResource(R.string.are_you_sure_layer, layer.layer.name),
                dismiss = { showDeleteDialog = false },
                onDelete = onDeleteItem
            )
        }

        if (showNameDialog) {
            SetNameDialog(
                existingName = layer.layer.name,
                dismiss = { showNameDialog = false },
                onName = onLayerName
            )
        }
    }
}

@Composable
private fun ButtonRow(
    showDeleteBtn: () -> Boolean,
    onShowNameDialog: () -> Unit,
    onShowDeleteDialog: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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

@Composable
private fun CanvasItem(
    columns: Int,
    rows: Int,
    layer: LayerAndPixel
) {
    val boxSize = with(LocalDensity.current) { CANVAS_SIZE.toPx() }
    val size = remember { boxSize / max(columns, rows) }

    val width = with(LocalDensity.current) { (size * columns).toDp() }
    val height = with(LocalDensity.current) { (size * rows).toDp() }

    val newBoxes = remember(layer) {
        generateBoxes(
            columns,
            rows,
            size.roundToInt().toFloat(),
            0F,
            0F
        )
    }

    val layers = remember(layer) {
        ImmutableList(
            listOf(
                LayerUi(
                    layer.layer.id,
                    layer.layer.projectId,
                    layer.layer.index,
                    layer.layer.name,
                    on = true,
                    selected = true,
                    visibilityEnabled = true,
                    showControls = true
                )
            )
        )
    }

    val selections = remember(layer) { generateSelectionsMap(layer.asList) }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .width(width)
            .height(height)
            .pngBackground(
                visible = true,
                size = with(LocalDensity.current) { 10.dp.toPx() }
            )
    ) {
        SelectionsBoxes(
            scale = 1F,
            offset = Offset.Zero,
            boxes = newBoxes,
            selections = selections,
            layers = layers
        )
    }
}

private val CANVAS_SIZE = 150.dp
