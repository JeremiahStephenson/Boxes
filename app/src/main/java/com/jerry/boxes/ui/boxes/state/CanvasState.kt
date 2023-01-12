package com.jerry.boxes.ui.boxes.state

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Constraints
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.boxes.cache.data.HistoryItem
import com.jerry.boxes.cache.data.LayerAndPixel
import com.jerry.boxes.ui.boxes.SerializableColor
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.generateBoxes
import com.jerry.boxes.ui.boxes.generateSelectionsSnapshot
import com.jerry.boxes.ui.boxes.history.UserHistory
import com.jerry.boxes.ui.shapes.Shape
import kotlin.math.max
import kotlin.math.min

@Stable
class CanvasState(layersState: State<List<LayerUi>>) {
    private val _boxes = mutableStateMapOf<Point, RectF>()
    private val _selections =
        mutableStateMapOf<Point, SnapshotStateMap<Long, SerializableColor?>?>()

    val layers by layersState

    val boxes = _boxes as Map<Point, RectF>
    val selections = _selections as Map<Point, Map<Long, SerializableColor?>?>

    val selectedLayer
        get() = layers.firstOrNull { it.selected }
            ?: layers.filter { it.on }.maxByOrNull { it.index } ?: layers.first()
    val hasLayersTurnedOn get() = layers.any { it.on }

    private var currentDragHistory: MutableMap<Point, SerializableColor?> = mutableMapOf()

    fun clear() {
        _selections
            .filter { it.value != null }
            .forEach {
                _selections.getOrDefault(it.key, mutableStateMapOf())?.apply {
                    layers.filter { layer -> layer.selected }.map { layer -> layer.id }
                        .forEach { layerId ->
                            put(layerId, null)
                        }
                }
            }
    }

    fun getCurrentSelectedLayerSelections(layerId: Long) = _selections
        .filter { it.value?.containsKey(layerId) == true }
        .mapValues { it.value?.values?.firstOrNull() }

    fun getTapHistoryItem(point: Point, layerId: Long) =
        UserHistory(layerId, mapOf(point to getCurrentSelection(point, layerId)))

    fun closeDragHistory(layerId: Long) =
        UserHistory(layerId, currentDragHistory.toMap()).also {
            currentDragHistory.clear()
        }

    fun addToDragHistory(
        layerId: Long,
        points: List<Point>
    ) {
        val filtered = points.filter { !currentDragHistory.keys.contains(it) }
        val currentSelection = getCurrentSelections(filtered, layerId)
        filtered.forEach {
            if (!currentDragHistory.keys.contains(it)) {
                currentDragHistory[it] = currentSelection[it]
            }
        }
    }

    fun onUndo(layerId: Long?, historyItems: List<HistoryItem>) {
        if (layerId == null) return
        historyItems.forEach { item ->
            _selections.getOrPut(Point(item.x, item.y)) { mutableStateMapOf() }
                ?.put(layerId, item.color?.let {
                    with(HsvColor.from(Color(it))) {
                        SerializableColor(
                            this.hue,
                            this.saturation,
                            this.value,
                            this.alpha,
                            item.shape ?: Shape.Box
                        )
                    }
                })
        }
    }

    fun onTap(
        point: Point,
        layerId: Long,
        currentColor: SerializableColor,
        currentShape: Shape
    ): SerializableColor? {
        val colorAndShape = currentColor.copy(shape = currentShape)
        val selection = when (_selections[point]?.get(layerId) == colorAndShape) {
            true -> null
            else -> colorAndShape
        }
        _selections.getOrPut(point) { mutableStateMapOf() }?.apply {
            put(layerId, selection)
        }
        return selection
    }

    fun onDrag(
        points: Collection<Point>,
        layerId: Long,
        currentColor: SerializableColor?,
        currentShape: Shape?
    ) {
        _selections.putAll(points.map {
            it to _selections.getOrDefault(it, mutableStateMapOf())?.apply {
                put(layerId, currentColor?.copy(shape = currentShape ?: Shape.Box))
            }
        })
    }

    fun fillInSelections(layers: List<LayerAndPixel>) {
        _selections.clear()
        _selections.putAll(generateSelectionsSnapshot(layers))
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

    private fun getCurrentSelection(
        point: Point,
        layerId: Long
    ): SerializableColor? {
        return _selections[point]?.get(layerId)
    }

    fun getCurrentSelection(point: Point): SerializableColor? {
        val turnedOnLayers = layers.filter { it.on }.sortedBy { it.index }.reversed()
        return turnedOnLayers.firstOrNull { _selections[point]?.get(it.id) != null }
            ?.let { getCurrentSelection(point, it.id) }
    }

    private fun getCurrentSelections(
        points: List<Point>,
        layerId: Long
    ): Map<Point, SerializableColor?> {
        return _selections
            .filter { points.contains(it.key) }
            .map { item ->
                item.key to (item.value?.get(layerId))
            }.toMap()
    }
}