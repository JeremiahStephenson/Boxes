package com.jerry.boxes.ui.boxes.state

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.unit.Constraints
import com.jerry.boxes.cache.data.Layer
import com.jerry.boxes.cache.data.LayerAndPixel
import com.jerry.boxes.ui.boxes.SerializableColor
import com.jerry.boxes.ui.boxes.shapes.Shape
import com.jerry.boxes.ui.boxes.generateBoxes
import kotlin.math.max
import kotlin.math.min

@Stable
class CanvasState() {
    private val _boxes = mutableStateMapOf<Point, RectF>()
    private val _selections =
        mutableStateMapOf<Point, SnapshotStateMap<Long, SerializableColor?>?>()
    private val _layers = mutableStateListOf<Layer>()

    val boxes = _boxes as Map<Point, RectF>
    val selections = _selections as Map<Point, Map<Long, SerializableColor?>?>
    val layers = _layers as List<Layer>

    val max get() = _layers.filter { it.on }.maxBy { it.index }
    val hasLayersTurnedOn get() = _layers.any { it.on }
    val turnedOnIds get() = _layers.filter { it.on }.map { it.id }

    fun setLayers(layerList: List<Layer>) {
        _layers.clear()
        _layers.addAll(layerList)
    }

    fun clear(layers: List<Long>) {
        _selections
            .filter { it.value != null }
            .forEach {
                _selections[it.key] = _selections.getOrDefault(it.key, mutableStateMapOf())?.apply {
                    layers.forEach { layerId ->
                        put(layerId, null)
                    }
                }
            }
    }

    fun onTap(
        point: Point,
        layerId: Long,
        currentColor: SerializableColor,
        currentShape: Shape
    ) {
        val colorAndShape = currentColor.copy(shape = currentShape)
        val selection = _selections[point]?.get(layerId)
        _selections[point] = _selections.getOrDefault(point, mutableStateMapOf())?.apply {
            put(
                layerId, when (selection == colorAndShape) {
                    true -> null
                    else -> colorAndShape
                }
            )
        }
    }

    fun onDrag(
        point: Point,
        layerId: Long,
        currentColor: SerializableColor,
        currentShape: Shape,
        erasing: Boolean
    ) {
        _selections[point] = _selections.getOrDefault(point, mutableStateMapOf())?.apply {
            put(
                layerId, when (erasing) {
                    true -> null
                    else -> currentColor.copy(shape = currentShape)
                }
            )
        }
    }

    fun fillInSelections(layers: List<LayerAndPixel>) {
        _selections.clear()
        _selections.putAll(
            layers.flatMap {
                it.pixels
            }.groupBy {
                Point(it.x, it.y)
            }.mapValues {
                it.value.associateTo(SnapshotStateMap()) { pixel ->
                    pixel.layerId to pixel.asSerializableColor
                }
            })
    }

    fun fillInBoxes(
        size: Constraints,
        offset: Float,
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
                xOffSet,
                offset + yOffSet
            )
        )
    }
}