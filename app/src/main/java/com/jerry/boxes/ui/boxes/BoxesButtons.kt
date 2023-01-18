package com.jerry.boxes.ui.boxes

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.jerry.boxes.ui.common.unboundClickable
import com.jerry.boxes.ui.shapes.Shape

@Composable
fun ShapeOption(
    modifier: Modifier = Modifier,
    shape: Shape,
    color: SerializableColor? = null,
    onClick: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val shapeColor = (color ?: SerializableColor(onSurface.toArgb())).copy(shape = shape)
    val size = with(LocalDensity.current) { 24.dp.toPx() }
    val stroke = with(LocalDensity.current) { 1.dp.toPx() }
    Canvas(
        modifier = Modifier
            .unboundClickable {
                onClick()
            }
            .padding(16.dp)
            .size(26.dp)
            .then(modifier)
    ) {
        drawCustomShape(
            RectF(0F, 0F, size, size),
            shapeColor
        )
        drawRect(
            color = onSurface.copy(alpha = 0.4F),
            style = Stroke(stroke),
            size = Size(size, size)
        )
    }
}