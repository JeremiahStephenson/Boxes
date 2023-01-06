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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.extensions.asSerializableColor
import com.jerry.boxes.ui.boxes.shapes.Shape
import com.jerry.boxes.ui.common.unboundClickable

@Composable
fun ShapeOption(
    shape: Shape,
    color: SerializableColor? = null,
    onClick: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val shapeColor = color ?: HsvColor.from(onSurface).asSerializableColor
    val size = with(LocalDensity.current) { 24.dp.toPx() }
    val stroke = with(LocalDensity.current) { 1.dp.toPx() }
    Canvas(
        modifier = Modifier
            .unboundClickable {
                onClick()
            }
            .padding(16.dp)
            .size(26.dp)
    ) {
        drawCustomShape(
            RectF(0F, 0F, size, size),
            SerializableColor(
                shapeColor.hue,
                shapeColor.saturation,
                shapeColor.value,
                shapeColor.alpha,
                shape
            )
        )
        drawRect(
            color = onSurface.copy(alpha = 0.4F),
            style = Stroke(stroke),
            size = Size(size, size)
        )
    }
}