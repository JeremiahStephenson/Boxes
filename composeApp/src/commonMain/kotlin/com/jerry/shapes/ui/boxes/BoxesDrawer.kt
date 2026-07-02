package com.jerry.shapes.ui.boxes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jerry.shapes.cache.data.Project
import com.jerry.shapes.ui.boxes.data.Action
import com.jerry.shapes.ui.boxes.data.LayerState
import com.jerry.shapes.ui.boxes.state.ButtonsState
import com.jerry.shapes.ui.boxes.state.CanvasState
import com.jerry.shapes.ui.boxes.state.enums.TapType
import com.jerry.shapes.ui.common.IconMenuButton
import com.jerry.shapes.ui.common.IconSelectableMenuButton
import com.jerry.shapes.ui.common.SetNameDialog
import com.jerry.shapes.util.ArrangementLastItem
import com.jerry.shapes.util.ExportType
import boxes.composeapp.generated.resources.Res
import boxes.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DrawerMenu(
    modifier: Modifier = Modifier,
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    getProject: () -> Project,
    onAction: (Action) -> Unit,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(8.dp),
        verticalArrangement = remember { ArrangementLastItem() },
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp),
                    text = stringResource(Res.string.layers),
                )
                Spacer(modifier = Modifier.weight(1F))
                IconMenuButton(
                    padding = PaddingValues(8.dp),
                    onClick = { onAction(Action.GoToLayerEdit) },
                    drawableRes = Res.drawable.ic_edit_24,
                    contentDescription = stringResource(Res.string.edit_layer_options),
                )
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }

        items(
            items = canvasState.layers,
            key = { it.id },
        ) { layer ->
            LayerItem(
                layer = layer,
                onAction = onAction,
            )
        }

        item {
            AddLayerBtn(
                canvasState = canvasState,
                onAction = onAction,
            )
        }

        item {
            ButtonSection(Res.string.tools) {
                val isPickerOn by remember { derivedStateOf { buttonsState.tapTypeState == TapType.PICKER } }
                IconSelectableMenuButton(
                    onClick = { onAction(Action.SetTapType(TapType.PICKER)) },
                    isSelected = { isPickerOn },
                    drawableResOn = Res.drawable.ic_colorize_24,
                    contentDescription = stringResource(Res.string.toggle_color_picker),
                )
                val isFillOn by remember { derivedStateOf { buttonsState.tapTypeState == TapType.FILL } }
                IconSelectableMenuButton(
                    onClick = { onAction(Action.SetTapType(TapType.FILL)) },
                    isSelected = { isFillOn },
                    drawableResOn = Res.drawable.ic_format_color_fill_24,
                    contentDescription = stringResource(Res.string.toggle_fill),
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.Eraser) },
                    isSelected = { buttonsState.eraserSelectedState },
                    drawableResOn = Res.drawable.ic_eraser_on_24,
                    contentDescription = stringResource(Res.string.toggle_eraser),
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.SelectTool) },
                    isSelected = { buttonsState.selectToolSelectedState },
                    drawableResOn = Res.drawable.ic_select_all_24,
                    contentDescription = stringResource(Res.string.select_and_move),
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.ShowGrid) },
                    isSelected = { getProject().showGrid },
                    drawableResOn = Res.drawable.ic_grid_on_24,
                    contentDescription = stringResource(Res.string.toggle_grid_visibility),
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.ShowPngBackground) },
                    isSelected = { getProject().showPngBg },
                    drawableResOn = Res.drawable.ic_opacity_on_24,
                    contentDescription = stringResource(Res.string.toggle_opacity_bg),
                )
            }
        }

        item {
            ButtonSection(Res.string.other) {
                IconMenuButton(
                    onClick = { onAction(Action.Edit) },
                    drawableRes = Res.drawable.ic_edit_24,
                    contentDescription = stringResource(Res.string.edit_project),
                )
                IconMenuButton(
                    onClick = { onAction(Action.Save(false)) },
                    drawableRes = Res.drawable.ic_save_24,
                    contentDescription = stringResource(Res.string.save_project),
                )
                var exportDialog by rememberSaveable {
                    mutableStateOf<ExportType?>(null)
                }
                exportDialog?.let {
                    ExportDialog(
                        it,
                        onExport = { size, isExport -> onAction(Action.Export(size, isExport)) },
                    ) {
                        exportDialog = null
                    }
                }
                IconMenuButton(
                    onClick = { exportDialog = ExportType.FILE },
                    drawableRes = Res.drawable.ic_image_24,
                    contentDescription = stringResource(Res.string.save_to_png),
                )
                IconMenuButton(
                    onClick = { exportDialog = ExportType.SHARE },
                    drawableRes = Res.drawable.ic_share_24,
                    contentDescription = stringResource(Res.string.share_with_people),
                )
            }
        }

        item {
            ButtonSection(Res.string.experimental) {
                IconMenuButton(
                    onClick = {
                        // TODO: launcher for multiplatform image import
                    },
                    drawableRes = Res.drawable.ic_upload_file_24,
                    contentDescription = "",
                )
            }
        }

        item {
            ButtonSection(Res.string.clear) {
                IconMenuButton(
                    onClick = { onAction(Action.Clear) },
                    drawableRes = Res.drawable.ic_auto_renew,
                    contentDescription = stringResource(Res.string.clear_layer),
                )
            }
        }
    }
}

@Composable
private fun AddLayerBtn(
    canvasState: CanvasState,
    onAction: (Action) -> Unit,
) {
    var showNameDialog by rememberSaveable { mutableStateOf(false) }
    if (canvasState.layers.size < 10) {
        OutlinedButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
            onClick = {
                showNameDialog = true
            },
        ) {
            Text(stringResource(Res.string.add_layer))
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    if (showNameDialog) {
        SetNameDialog(
            existingName =
                stringResource(
                    Res.string.layer_hint,
                    (canvasState.layers.firstOrNull()?.index ?: 0) + 2,
                ),
            dismiss = { showNameDialog = false },
            onName = {
                onAction(Action.AddLayer(it))
            },
        )
    }
}

@Composable
private fun LayerItem(
    layer: LayerState,
    onAction: (Action) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .run {
                    when (layer.showControls) {
                        true ->
                            clickable {
                                onAction(Action.TurnOnOrOffLayer(true, layer.id))
                                onAction(Action.SelectLayer(layer.id))
                            }
                        else -> this
                    }
                }.background(
                    when (layer.selected && layer.showControls) {
                        true -> MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4F)
                        else -> Color.Transparent
                    },
                ).padding(start = 16.dp)
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier =
                Modifier
                    .weight(1F)
                    .padding(
                        top = if (layer.showControls) 0.dp else 16.dp,
                        bottom = if (layer.showControls) 0.dp else 8.dp,
                    ),
            text = layer.name,
        )
        if (layer.showControls) {
            IconSelectableMenuButton(
                onClick = { onAction(Action.TurnOnOrOffLayer(!layer.on, layer.id)) },
                isSelected = { layer.on },
                drawableResOn = Res.drawable.ic_visibility_off_24,
                drawableResOff = Res.drawable.ic_visibility_on_24,
                isEnabled = { layer.visibilityEnabled },
                contentDescription = stringResource(Res.string.toggle_layer_visibility),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ButtonSection(
    title: StringResource,
    content: @Composable () -> Unit,
) {
    ButtonHeader(title = title)
    FlowRow(content = {
        content()
    })
}

@Composable
private fun ButtonHeader(
    title: StringResource,
) {
    Text(
        modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
        text = stringResource(title),
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}
