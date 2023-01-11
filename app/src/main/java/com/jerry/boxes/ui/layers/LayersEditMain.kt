package com.jerry.boxes.ui.layers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.IconMenuButton
import com.jerry.boxes.ui.common.pngBackground
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
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
                    showUpArrow = {
                        layer.layer.index > 0
                    },
                    showDownArrow = {
                        val max = projectState?.layers?.maxBy { it.layer.index }?.layer?.index ?: 0
                        layer.layer.index < max
                    },
                    showDivider = {
                        index > 0
                    },
                    onDirection = { lyr, position ->
                        viewModel.changeLayerIndex(
                            projectState!!.layers.map { it.layer },
                            lyr,
                            position
                        )
                    }
                )
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
    onDirection: (Long, Int) -> Unit
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
        Row(modifier = Modifier.fillMaxWidth()) {
            CanvasItem(
                columns = columns,
                rows = rows,
                layer = layer
            )
            Spacer(modifier = Modifier.weight(1F))
            Column(modifier = Modifier
                .padding(top = 16.dp)
                .height(CANVAS_SIZE)
            ) {
                IconMenuButton(
                    modifier = Modifier.alpha(if (showUpArrow()) 1F else 0F),
                    onClick = {
                        onDirection(layer.layer.id, layer.layer.index - 1)
                    },
                    drawableRes = R.drawable.ic_baseline_arrow_upward_24
                )
                Spacer(modifier = Modifier.weight(1F))
                IconMenuButton(
                    modifier = Modifier.alpha(if (showDownArrow()) 1F else 0F),
                    onClick = {
                        onDirection(layer.layer.id, layer.layer.index + 1)
                    },
                    drawableRes = R.drawable.ic_baseline_arrow_downward_24
                )
            }
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
            size,
            0F,
            0F
        )
    }

    val layers = remember(layer) {
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
    }

    val selections = remember(layer) { generateSelectionsMap(layer.asList) }

    Box(
        modifier = Modifier
            .padding(16.dp)
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