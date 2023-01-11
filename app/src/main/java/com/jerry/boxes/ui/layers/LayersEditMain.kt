package com.jerry.boxes.ui.layers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import com.jerry.boxes.ui.boxes.state.CanvasState
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.pngBackground
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import kotlin.math.max

@Destination
@Composable
fun LayersEditMain(
    projectId: Long,
    navController: DestinationsNavigator,
    viewModel: LayersEditViewModel = koinViewModel()
) {
    DefaultContainer(title = stringResource(R.string.edit)) {
        val projectState by viewModel.projectFlow.collectAsStateWithLifecycle()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(projectState?.layers ?: emptyList(), key = { layer -> layer.layer.id }) { layer ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        text = layer.layer.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = LocalContentColor.current
                    )
                    CanvasItem(
                        columns = projectState!!.project.columns,
                        rows = projectState!!.project.rows,
                        layer = layer
                    )
                }
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

    val canvasState = CanvasState(remember { mutableStateOf(emptyList()) })

    LaunchedEffect(layer) {
        canvasState.fillInSelections(layer.asList)
    }

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
            selections = canvasState.selections,
            layers = layers
        )
    }
}

private val CANVAS_SIZE = 200.dp