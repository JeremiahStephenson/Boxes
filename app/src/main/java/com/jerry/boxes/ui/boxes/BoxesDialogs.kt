package com.jerry.boxes.ui.boxes

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.R
import com.jerry.boxes.extensions.asColorAndShape
import com.jerry.boxes.ui.boxes.data.ColorAndShape
import com.jerry.boxes.ui.common.ShapeOption
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SetColorButton(
                    modifier = Modifier.weight(1F),
                    onColorChosen = { onColorChosen(currentColor) },
                    onDismiss = onDismiss
                )
                Spacer(modifier = Modifier.width(8.dp))
                CloseColorButton(
                    modifier = Modifier,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun SetColorButton(
    modifier: Modifier,
    onColorChosen: () -> Unit,
    onDismiss: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = {
            onColorChosen()
            onDismiss()
        }
    ) {
        Text(text = stringResource(R.string.set_color))
    }
}

@Composable
private fun CloseColorButton(
    modifier: Modifier,
    onDismiss: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
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
            columns = GridCells.Fixed(COLUMN_COUNT)
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
                ShapeOption(
                    shapeSize = 34.dp,
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

@Composable
fun ExportDialog(
    export: Boolean,
    onExport: (Int, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.image_size),
                style = MaterialTheme.typography.titleLarge
            )
            var quality by remember { mutableStateOf(MEDIUM) }
            var qualityError by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = quality.toString(),
                    onValueChange = {
                        val value = it.toIntOrNull() ?: 0
                        qualityError = value < LOWEST_QUALITY || value > HIGHEST_QUALITY
                        quality = value
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Text(
                    text = when (qualityError) {
                        true -> stringResource(
                            when (quality > HIGHEST_QUALITY) {
                                true -> R.string.value_too_high
                                else -> R.string.value_too_low
                            }
                        )
                        else -> ""
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                val isXSmallSelected by remember { derivedStateOf { quality == XSMALL } }
                SizeButton(
                    titleRes = R.string.extra_small,
                    isSelected = { isXSmallSelected }
                ) {
                    quality = XSMALL
                }
                Spacer(modifier = Modifier.size(8.dp))
                val isSmallSelected by remember { derivedStateOf { quality == SMALL } }
                SizeButton(
                    titleRes = R.string.small,
                    isSelected = { isSmallSelected }
                ) {
                    quality = SMALL
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                val isMediumSelected by remember { derivedStateOf { quality == MEDIUM } }
                SizeButton(
                    titleRes = R.string.medium,
                    isSelected = { isMediumSelected }
                ) {
                    quality = MEDIUM
                }
                Spacer(modifier = Modifier.size(8.dp))
                val isLargeSelected by remember { derivedStateOf { quality == LARGE } }
                SizeButton(
                    titleRes = R.string.large,
                    isSelected = { isLargeSelected }
                ) {
                    quality = LARGE
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                val isXLargeSelected by remember { derivedStateOf { quality == XLARGE } }
                SizeButton(
                    titleRes = R.string.extra_large,
                    isSelected = { isXLargeSelected }
                ) {
                    quality = XLARGE
                }
                Spacer(modifier = Modifier.size(8.dp))
                val isXXLargeSelected by remember { derivedStateOf { quality == XXLARGE } }
                SizeButton(
                    titleRes = R.string.extra_extra_large,
                    isSelected = { isXXLargeSelected }
                ) {
                    quality = XXLARGE
                }
            }
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outline
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.weight(1F),
                    onClick = {
                        onExport(quality, export)
                        onDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(
                            when (!export) {
                                true -> R.string.share
                                else -> R.string.export
                            }
                        )
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = stringResource(R.string.close)
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.SizeButton(
    @StringRes titleRes: Int,
    isSelected: () -> Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .weight(1F),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor =
            when (isSelected()) {
                true -> MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4F)
                else -> Color.Transparent
            }
        ),
        onClick = onClick
    ) {
        Text(text = stringResource(titleRes))
    }
}

private const val COLUMN_COUNT = 4
private const val HIGHEST_QUALITY = 12800
private const val LOWEST_QUALITY = 50

private const val XSMALL = 256
private const val SMALL = 512
private const val MEDIUM = 1024
private const val LARGE = 2048
private const val XLARGE = 4096
private const val XXLARGE = 8192
