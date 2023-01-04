package com.jerry.boxes.ui.boxes.state

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.unit.Constraints
import com.jerry.boxes.cache.data.Pixel
import com.jerry.boxes.ui.boxes.SerializableColor
import com.jerry.boxes.ui.boxes.generateBoxes
import kotlin.math.max
import kotlin.math.min

@Stable
class CanvasState {
    private val _boxes = mutableStateMapOf<Point, RectF>()
    private val _selections = mutableStateMapOf<Point, SerializableColor?>()

    val boxes = _boxes as Map<Point, RectF>
    val selections = _selections as Map<Point, SerializableColor?>

    fun clear() {
        _selections
            .filter { it.value != null }
            .forEach {
                _selections[it.key] = null
            }
    }

    fun onTap(
        point: Point,
        currentColor: SerializableColor
    ) {
        val selection = _selections[point]
        _selections[point] = when (selection == currentColor) {
            true -> null
            else -> currentColor
        }
    }

    fun onDrag(
        point: Point,
        currentColor: SerializableColor,
        erasing: Boolean
    ) {
        _selections[point] = when (erasing) {
            true -> null
            else -> currentColor
        }
    }

    fun fillInSelections(pixels: List<Pixel>) {
        _selections.clear()
        _selections.putAll(pixels.associate {
            Point(it.x, it.y) to
                    SerializableColor(
                        it.hue,
                        it.saturation,
                        it.value,
                        it.alpha
                    )
        })
    }

    fun fillInBoxes(
        size: Constraints,
        offset: Int,
        columns: Int,
        rows: Int
    ) {
        val maxWidth =
            size.maxWidth / columns.toFloat()
        val maxHeight =
            (size.maxHeight - offset) / rows.toFloat()
        val min = min(maxWidth, maxHeight)

        val yOffSet = max(
            (((size.maxHeight - offset) - (min * rows)) / 2),
            0F
        )
        val xOffSet =
            max(((size.maxWidth - (min * columns)) / 2), 0F)

        _boxes.clear()
        _boxes.putAll(
            generateBoxes(
                columns,
                rows,
                min,
                xOffSet.toInt(),
                offset + yOffSet.toInt()
            )
        )
    }
}