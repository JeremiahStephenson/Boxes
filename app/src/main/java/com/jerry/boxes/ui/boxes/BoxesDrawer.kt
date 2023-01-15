package com.jerry.boxes.ui.boxes

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.jerry.boxes.R
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.state.ButtonsState
import com.jerry.boxes.ui.boxes.state.CanvasState
import com.jerry.boxes.ui.boxes.state.TapType
import com.jerry.boxes.ui.common.IconMenuButton
import com.jerry.boxes.ui.common.IconSelectableMenuButton
import com.jerry.boxes.ui.common.SetNameDialog
import com.jerry.boxes.util.ArrangementLastItem

@Composable
fun DrawerMenu(
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    onAction: (Action) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = remember { ArrangementLastItem() },
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    text = stringResource(R.string.layers)
                )
                Spacer(modifier = Modifier.weight(1F))
                IconMenuButton(
                    padding = PaddingValues(8.dp),
                    onClick = { onAction(Action.GoToLayerEdit) },
                    drawableRes = R.drawable.ic_edit_24
                )
            }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        }

        items(
            items = canvasState.layers,
            key = { it.id }) { layer ->
            LayerItem(
                layer = layer,
                onAction = onAction
            )
        }

        item {
            AddLayerBtn(
                canvasState = canvasState,
                onAction = onAction
            )
        }

        item {
            ButtonSection(R.string.tools) {
                IconSelectableMenuButton(
                    onClick = { onAction(Action.Eraser) },
                    isSelected = { buttonsState.eraserSelectedState },
                    drawableResOn = R.drawable.ic_eraser_on_24,
                    drawableResOff = R.drawable.ic_eraser_off_24
                )
                IconMenuButton(
                    onClick = { onAction(Action.SetTapType(TapType.PICKER)) },
                    drawableRes = R.drawable.ic_colorize_24
                )
                IconMenuButton(
                    onClick = { onAction(Action.SetTapType(TapType.FILL)) },
                    drawableRes = R.drawable.ic_format_color_fill_24
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.ShowGrid) },
                    isSelected = { buttonsState.showGridState },
                    drawableResOn = R.drawable.ic_grid_on_24,
                    drawableResOff = R.drawable.ic_grid_off_24
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.ShowPngBackground) },
                    isSelected = { buttonsState.showPngBackgroundState },
                    drawableResOn = R.drawable.ic_opacity_on_24,
                    drawableResOff = R.drawable.ic_opacity_off_24
                )
            }
        }

        item {
            ButtonSection(R.string.save) {
                IconMenuButton(
                    onClick = { onAction(Action.Save(false)) },
                    drawableRes = R.drawable.ic_save_24
                )
                IconMenuButton(
                    onClick = { onAction(Action.Edit) },
                    drawableRes = R.drawable.ic_edit_24
                )
            }
        }

        item {
            // todo add dialog with options
            ButtonSection(R.string.export) {
                IconMenuButton(
                    onClick = { onAction(Action.Export(1000F)) },
                    drawableRes = R.drawable.ic_image_24
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
private fun AddLayerBtn(
    canvasState: CanvasState,
    onAction: (Action) -> Unit
) {
    var showNameDialog by rememberSaveable { mutableStateOf(false) }
    if (canvasState.layers.size < 5) {
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            onClick = {
                showNameDialog = true
            }) {
            Text(stringResource(R.string.add_layer))
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    if (showNameDialog) {
        SetNameDialog(
            existingName = "",
            dismiss = { showNameDialog = false },
            onName = {
                onAction(Action.AddLayer(it))
            }
        )
    }
}

@Composable
private fun LayerItem(
    layer: LayerUi,
    onAction: (Action) -> Unit
) {
    Row(
        modifier = Modifier
            .run {
                when (layer.showControls) {
                    true -> clickable {
                        onAction(Action.TurnOnOrOffLayer(true, layer.id))
                        onAction(Action.SelectLayer(layer.id))
                    }
                    else -> this
                }
            }
            .background(when (layer.selected && layer.showControls) {
                true -> MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4F)
                else -> Color.Transparent
            })
            .padding(start = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1F)
                .padding(
                    top = if (layer.showControls) 0.dp else 16.dp,
                    bottom = if (layer.showControls) 0.dp else 8.dp
                ),
            text = layer.name
        )
        if (layer.showControls) {
            IconSelectableMenuButton(
                onClick = { onAction(Action.TurnOnOrOffLayer(!layer.on, layer.id)) },
                isSelected = { layer.on },
                drawableResOn = R.drawable.ic_visibility_on_24,
                drawableResOff = R.drawable.ic_visibility_off_24,
                isEnabled = { layer.visibilityEnabled }
            )
        }
    }
}

@Composable
private fun switchColors(layer: LayerUi) = when {
    layer.on -> {
        when (layer.visibilityEnabled) {
            true -> MaterialTheme.colorScheme.surface
            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5F)
        }
    }
    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5F)
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
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        text = stringResource(title)
    )
    Divider(modifier = Modifier.padding(horizontal = 16.dp))
}
