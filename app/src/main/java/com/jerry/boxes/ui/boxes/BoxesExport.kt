package com.jerry.boxes.ui.boxes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import androidx.core.graphics.applyCanvas
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.util.storeImage
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

// todo clean this up and add error handling
fun exportCanvas(
    context: Context,
    name: String,
    export: Boolean,
    rows: Int,
    columns: Int,
    imageSize: Int,
    layers: List<LayerUi>,
    selections: Map<Long, Map<Point, ColorAndShape>>
): String? {
    val boxSize = ceil(min(imageSize / columns.toFloat(), imageSize / rows.toFloat())).toInt()
    val newBoxes = generateBoxes(columns, rows, boxSize.toFloat(), 0F, 0F)
    return try {
        val bitmap = Bitmap.createBitmap(
            columns * boxSize,
            rows * boxSize,
            Bitmap.Config.ARGB_8888
        )

        bitmap.applyCanvas {
            drawShapes(layers, selections, newBoxes)
        }

        bitmap.storeImage(context, export, name)
    } catch (t: Throwable) {
        // todo
        null
    }
}
