package com.jerry.boxes.ui.boxes

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.jerry.boxes.R
import com.jerry.boxes.ui.boxes.state.ButtonsState
import com.jerry.boxes.ui.boxes.state.CanvasState
import com.jerry.boxes.ui.common.IconMenuButton
import com.jerry.boxes.ui.common.IconSelectableMenuButton
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
            ButtonHeader(R.string.layers)
        }

        items(
            items = canvasState.layers,
            key = { it.id }) { layer ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = layer.name
                )
                Switch(
                    checked = layer.on,
                    onCheckedChange = {
                        onAction(Action.TurnOnOrOffLayer(it, layer.id))
                    })
            }
        }

        item {
            if (canvasState.layers.size < 5) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                    onClick = {
                        onAction(Action.AddLayer)
                    }) {
                    Text(stringResource(R.string.add_layer))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

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
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        text = stringResource(title)
    )
    Divider(modifier = Modifier.padding(horizontal = 16.dp))
}
