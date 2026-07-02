package com.jerry.shapes.ui.boxes

import androidx.compose.runtime.Composable
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.ui.shapes.Shape
import com.jerry.shapes.util.ImmutableList
import com.jerry.shapes.util.ExportType

@Composable
expect fun ColorPickerDialog(
    color: ColorAndShape,
    usedColors: ImmutableList<ColorAndShape>,
    onColorChosen: (ColorAndShape) -> Unit,
    onDismiss: () -> Unit,
)

@Composable
expect fun ShapePickerDialog(
    color: ColorAndShape,
    numberOfBoxes: Int,
    onShapeChosen: (Shape) -> Unit,
    onDismiss: () -> Unit,
)

@Composable
expect fun ExportDialog(
    export: ExportType,
    onExport: (Int, ExportType) -> Unit,
    onDismiss: () -> Unit,
)
