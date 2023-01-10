package com.jerry.boxes.ui.boxes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.R
import com.jerry.boxes.extensions.asSerializableColor
import com.jerry.boxes.ui.boxes.shapes.Shape
import com.jerry.boxes.ui.common.pngBackground
import com.jerry.boxes.ui.common.unboundClickable

@Composable
fun ColorPickerDialog(
    color: SerializableColor,
    usedColors: List<SerializableColor>,
    onColorChosen: (SerializableColor) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        var currentColor by remember(color) { mutableStateOf(color) }
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val showTopSpace by remember { derivedStateOf { usedColors.isNotEmpty() } }
            if (showTopSpace) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
            }
            val size = with(LocalDensity.current) { 10.dp.toPx() }
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(5)
            ) {
                items(usedColors) {
                    Box {
                        Box(
                            modifier = Modifier
                                .unboundClickable {
                                    onColorChosen(it)
                                    onDismiss()
                                }
                                .padding(16.dp)
                                .size(34.dp)
                                .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface)
                                .pngBackground(true, size)
                                .background(color = it.color)
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
            ClassicColorPicker(
                color = color.color,
                modifier = Modifier
                    .height(250.dp)
                    .padding(horizontal = 16.dp),
                onColorChanged = { color: HsvColor ->
                    currentColor = color.asSerializableColor
                }
            )
            Button(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                onClick = {
                    onColorChosen(currentColor)
                    onDismiss()
                }) {
                Text(text = stringResource(R.string.set_color))
            }
            OutlinedButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.close))
            }
        }
    }
}

@Composable
fun ShapePickerDialog(
    color: SerializableColor,
    onShapeChosen: (Shape) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        val shapes = remember { Shape.values().groupBy { it.group }.flatMap { it.value } }
        LazyVerticalGrid(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentPadding = PaddingValues(16.dp),
            columns = GridCells.Fixed(COLUMN_COUNT),
        ) {
            items(
                items = shapes,
                key = { item -> item.ordinal },
                span = { item ->
                    val groupSize = shapes.count { it.group == item.group }
                    val indexInGroup = shapes.filter { it.group == item.group }.indexOf(item) + 1
                    val end = indexInGroup % COLUMN_COUNT != 0 && groupSize == indexInGroup
                    GridItemSpan(
                        when (end) {
                            true -> COLUMN_COUNT - (indexInGroup - 1)
                            else -> 1
                        }
                    )
                }
            ) {
                Box {
                    ShapeOption(
                        color = color,
                        shape = it
                    ) {
                        onShapeChosen(it)
                        onDismiss()
                    }
                }
            }
        }
    }
}

private const val COLUMN_COUNT = 4
private const val COLOR_COLUMN_COUNT = 10