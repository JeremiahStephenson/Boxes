package com.jerry.boxes.ui.boxes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.view.View
import androidx.core.graphics.applyCanvas
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.util.storeImage
import kotlin.math.max
import kotlin.math.roundToInt

// todo clean this up and add error handling
fun exportCanvas(
    context: Context,
    projectId: Long,
    export: Boolean,
    rows: Int,
    columns: Int,
    imageSize: Float,
    layers: List<LayerUi>,
    selections: Map<Long, Map<Point, ColorAndShape>>
) {
    val boxSize = max(imageSize / columns.toFloat(), imageSize / rows.toFloat())
    val newBoxes = generateBoxes(columns, rows, boxSize.roundToInt().toFloat(), 0F, 0F)
    val bitmap = Bitmap.createBitmap(
        columns * boxSize.roundToInt(),
        rows * boxSize.roundToInt(),
        Bitmap.Config.ARGB_8888
    )

    bitmap.applyCanvas {
        drawShapes(layers, selections, newBoxes)
    }

    bitmap.storeImage(context, export, projectId.toString())
}
