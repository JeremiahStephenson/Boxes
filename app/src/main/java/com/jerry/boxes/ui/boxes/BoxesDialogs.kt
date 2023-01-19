package com.jerry.boxes.ui.boxes

import android.content.res.Configuration
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.R
import com.jerry.boxes.extensions.asColorAndShape
import com.jerry.boxes.ui.common.pngBackground
import com.jerry.boxes.ui.common.unboundClickable
import com.jerry.boxes.ui.shapes.Shape

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColorPickerDialog(
    color: ColorAndShape,
    usedColors: List<ColorAndShape>,
    onColorChosen: (ColorAndShape) -> Unit,
    onDismiss: () -> Unit
) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = (isPortrait)
        )
    ) {
        var currentColor by remember(color) { mutableStateOf(color) }

        Column(
            modifier = Modifier
                .run {
                    when (!isPortrait) {
                        true -> width(500.dp)
                        else -> this
                    }
                }
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {

            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {

                val size = with(LocalDensity.current) { 10.dp.toPx() }
                val height = remember {
                    when (isPortrait) {
                        true -> 250.dp
                        else -> 200.dp
                    }
                }

                val showTopSpace by remember { derivedStateOf { usedColors.isNotEmpty() } }

                if (!isPortrait && showTopSpace) {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .weight(1F)
                            .height(height),
                        columns = GridCells.Fixed(4)
                    ) {
                        items(usedColors) {
                            ColorBox(
                                size = size,
                                color = it,
                                onColorChosen = onColorChosen,
                                onDismiss = onDismiss
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1F),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (isPortrait) {
                        if (showTopSpace) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(16.dp)
                            )
                        }
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxWidth(),
                            columns = GridCells.Fixed(5)
                        ) {
                            items(usedColors) {
                                ColorBox(
                                    size = size,
                                    color = it,
                                    onColorChosen = onColorChosen,
                                    onDismiss = onDismiss
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
                            .height(height)
                            .padding(horizontal = 16.dp),
                        onColorChanged = { color: HsvColor ->
                            currentColor = color.asColorAndShape
                        }
                    )
                }
            }

            when (isPortrait) {
                true ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ColorPickerButtonsColumn(
                            onColorChosen = { onColorChosen(currentColor) },
                            onDismiss = onDismiss
                        )
                    }
                else ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ColorPickerButtonsRow(
                            onColorChosen = { onColorChosen(currentColor) },
                            onDismiss = onDismiss
                        )
                    }
            }
        }
    }
}

@Composable
private fun RowScope.ColorPickerButtonsRow(
    onColorChosen: () -> Unit,
    onDismiss: () -> Unit
) {
    SetColorButton(
        modifier = Modifier.weight(1F),
        onColorChosen = onColorChosen,
        onDismiss = onDismiss
    )
    CloseColorButton(
        modifier = Modifier
            .weight(1F)
            .padding(vertical = 16.dp),
        onDismiss = onDismiss
    )
}

@Composable
private fun ColumnScope.ColorPickerButtonsColumn(
    onColorChosen: () -> Unit,
    onDismiss: () -> Unit
) {
    SetColorButton(
        modifier = Modifier.fillMaxWidth(),
        onColorChosen = onColorChosen,
        onDismiss = onDismiss
    )
    CloseColorButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        onDismiss = onDismiss
    )
}

@Composable
private fun SetColorButton(
    modifier: Modifier,
    onColorChosen: () -> Unit,
    onDismiss: () -> Unit
) {
    Button(
        modifier = Modifier
            .padding(
                vertical = 16.dp,
                horizontal = 16.dp
            )
            .then(modifier),
        onClick = {
            onColorChosen()
            onDismiss()
        }) {
        Text(text = stringResource(R.string.set_color))
    }
}

@Composable
private fun CloseColorButton(
    modifier: Modifier,
    onDismiss: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .then(modifier),
        onClick = onDismiss
    ) {
        Text(text = stringResource(R.string.close))
    }
}

@Composable
private fun ColorBox(
    size: Float,
    color: ColorAndShape,
    onColorChosen: (ColorAndShape) -> Unit,
    onDismiss: () -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .unboundClickable {
                    onColorChosen(color)
                    onDismiss()
                }
                .padding(16.dp)
                .size(34.dp)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface)
                .pngBackground(true, size)
                .background(color = color.color)
        )
    }
}

@Composable
fun ShapePickerDialog(
    color: ColorAndShape,
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
                            true -> COLUMN_COUNT - ((indexInGroup - 1) % COLUMN_COUNT)
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