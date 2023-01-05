package com.jerry.boxes.ui.boxes.state

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.unit.Constraints
import com.jerry.boxes.cache.data.Pixel
import com.jerry.boxes.ui.boxes.SerializableColor
import com.jerry.boxes.ui.boxes.generateBoxes
import kotlin.math.max
import kotlin.math.min

@Stable
class CanvasState {
    private val _boxes = mutableStateMapOf<Point, RectF>()
    private val _selections = mutableStateMapOf<Point, SnapshotStateMap<Int, SerializableColor?>?>()

    val boxes = _boxes as Map<Point, RectF>
    val selections = _selections as Map<Point, Map<Int, SerializableColor?>?>

    fun clear() {
        _selections
            .filter { it.value != null }
            .forEach {
                _selections[it.key] = null
            }
    }

    fun onTap(
        point: Point,
        layer: Int,
        currentColor: SerializableColor
    ) {
        val selection = _selections[point]?.get(layer)
        _selections[point] = _selections.getOrDefault(point, mutableStateMapOf())?.apply {
            put(layer, when (selection == currentColor) {
                true -> null
                else -> currentColor
            })
        }
    }

    fun onDrag(
        point: Point,
        layer: Int,
        currentColor: SerializableColor,
        erasing: Boolean
    ) {
        _selections[point] = _selections.getOrDefault(point, mutableStateMapOf())?.apply {
            put(layer, when (erasing) {
                true -> null
                else -> currentColor
            })
        }
    }

    fun fillInSelections(pixels: List<Pixel>) {
        _selections.clear()
        _selections.putAll(pixels.groupBy { Point(it.x, it.y) }.mapValues {
            it.value.associateTo(SnapshotStateMap()) { pixel ->
                pixel.layer to SerializableColor(
                    pixel.hue,
                    pixel.saturation,
                    pixel.value,
                    pixel.alpha
                )
            }
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