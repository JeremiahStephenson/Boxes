package com.jerry.shapes.ui.boxes

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jerry.shapes.R
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
                    text = stringResource(R.string.layers),
                )
                Spacer(modifier = Modifier.weight(1F))
                IconMenuButton(
                    padding = PaddingValues(8.dp),
                    onClick = { onAction(Action.GoToLayerEdit) },
                    drawableRes = R.drawable.ic_edit_24,
                    contentDescription = stringResource(R.string.edit_layer_options),
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
            ButtonSection(R.string.tools) {
                val isPickerOn by remember { derivedStateOf { buttonsState.tapTypeState == TapType.PICKER } }
                IconSelectableMenuButton(
                    onClick = { onAction(Action.SetTapType(TapType.PICKER)) },
                    isSelected = { isPickerOn },
                    drawableResOn = R.drawable.ic_colorize_24,
                    contentDescription = stringResource(R.string.toggle_color_picker),
                )
                val isFillOn by remember { derivedStateOf { buttonsState.tapTypeState == TapType.FILL } }
                IconSelectableMenuButton(
                    onClick = { onAction(Action.SetTapType(TapType.FILL)) },
                    isSelected = { isFillOn },
                    drawableResOn = R.drawable.ic_format_color_fill_24,
                    contentDescription = stringResource(R.string.toggle_fill),
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.Eraser) },
                    isSelected = { buttonsState.eraserSelectedState },
                    drawableResOn = R.drawable.ic_eraser_on_24,
                    contentDescription = stringResource(R.string.toggle_eraser),
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.SelectTool) },
                    isSelected = { buttonsState.selectToolSelectedState },
                    drawableResOn = R.drawable.ic_select_all_24,
                    contentDescription = stringResource(R.string.select_and_move),
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.ShowGrid) },
                    isSelected = { getProject().showGrid },
                    drawableResOn = R.drawable.ic_grid_on_24,
                    contentDescription = stringResource(R.string.toggle_grid_visibility),
                )
                IconSelectableMenuButton(
                    onClick = { onAction(Action.ShowPngBackground) },
                    isSelected = { getProject().showPngBg },
                    drawableResOn = R.drawable.ic_opacity_on_24,
                    contentDescription = stringResource(R.string.toggle_opacity_bg),
                )
            }
        }

        item {
            ButtonSection(R.string.other) {
                IconMenuButton(
                    onClick = { onAction(Action.Edit) },
                    drawableRes = R.drawable.ic_edit_24,
                    contentDescription = stringResource(R.string.edit_project),
                )
                IconMenuButton(
                    onClick = { onAction(Action.Save(false)) },
                    drawableRes = R.drawable.ic_save_24,
                    contentDescription = stringResource(R.string.save_project),
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
                    drawableRes = R.drawable.ic_image_24,
                    contentDescription = stringResource(R.string.save_to_png),
                )
                IconMenuButton(
                    onClick = { exportDialog = ExportType.SHARE },
                    drawableRes = R.drawable.ic_share_24,
                    contentDescription = stringResource(R.string.share_with_people),
                )
            }
        }

        item {
            ButtonSection(R.string.experimental) {
                val context = LocalContext.current
                val launcher =
                    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                        uri?.let {
                            context.contentResolver.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION,
                            )
                            onAction(Action.ImageImport(it, canvasState.selectedLayer.id))
                        }
                    }
                IconMenuButton(
                    onClick = {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    drawableRes = R.drawable.ic_upload_file_24,
                    contentDescription = "",
                )
            }
        }

        item {
            ButtonSection(R.string.clear) {
                IconMenuButton(
                    onClick = { onAction(Action.Clear) },
                    drawableRes = R.drawable.ic_auto_renew,
                    contentDescription = stringResource(R.string.clear_layer),
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
            Text(stringResource(R.string.add_layer))
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    if (showNameDialog) {
        SetNameDialog(
            existingName =
                stringResource(
                    R.string.layer_hint,
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
                drawableResOn = R.drawable.ic_visibility_off_24,
                drawableResOff = R.drawable.ic_visibility_on_24,
                isEnabled = { layer.visibilityEnabled },
                contentDescription = stringResource(R.string.toggle_layer_visibility),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ButtonSection(
    @StringRes title: Int,
    content: @Composable () -> Unit,
) {
    ButtonHeader(title = title)
    FlowRow(content = {
        content()
    })
}

@Composable
private fun ButtonHeader(
    @StringRes title: Int,
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
