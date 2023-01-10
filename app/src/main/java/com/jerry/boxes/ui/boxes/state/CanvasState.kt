package com.jerry.boxes.ui.boxes.state

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.unit.Constraints
import com.jerry.boxes.cache.data.Layer
import com.jerry.boxes.cache.data.LayerAndPixel
import com.jerry.boxes.extensions.asList
import com.jerry.boxes.ui.boxes.SerializableColor
import com.jerry.boxes.ui.boxes.generateBoxes
import com.jerry.boxes.ui.boxes.history.HistoryItem
import com.jerry.boxes.ui.boxes.shapes.Shape
import kotlin.math.max
import kotlin.math.min

@Stable
class CanvasState(layersState: State<List<Layer>>) {
    private val _boxes = mutableStateMapOf<Point, RectF>()
    private val _selections =
        mutableStateMapOf<Point, SnapshotStateMap<Long, SerializableColor?>?>()

    val layers by layersState

    val boxes = _boxes as Map<Point, RectF>
    val selections = _selections as Map<Point, Map<Long, SerializableColor?>?>

    val max get() = layers.filter { it.on }.maxBy { it.index }
    val hasLayersTurnedOn get() = layers.any { it.on }

    private var currentDragHistory: MutableMap<Point, SerializableColor?> = mutableMapOf()

    fun clear() {
        _selections
            .filter { it.value != null }
            .forEach {
                _selections.getOrDefault(it.key, mutableStateMapOf())?.apply {
                    layers.filter { it.on }.map { it.id }.forEach { layerId ->
                        put(layerId, null)
                    }
                }
            }
    }

    fun getCurrentSelectedLayerSelections(): Map<Point, Map<Long, SerializableColor?>?> {
        val selectedLayers = layers.filter { it.on }.map { it.id }
        return _selections.map { it.key to it.value?.filter { data -> selectedLayers.contains(data.key) } }
            .toMap()
    }

    fun getTapHistoryItem(point: Point, layerId: Long) =
        HistoryItem.HistoryTapItem(getCurrentSelection(point, layerId), layerId, point)

    fun closeDragHistory(layerId: Long) =
        HistoryItem.HistoryDragItem(layerId, currentDragHistory.toMap()).also {
            currentDragHistory.clear()
        }

    fun addToDragHistory(
        layerId: Long,
        points: List<Point>
    ) {
        points.forEach {
            if (!currentDragHistory.keys.contains(it)) {
                currentDragHistory[it] = getCurrentSelections(points, layerId).get(it)
            }
        }
    }

    fun onUndo(historyItem: HistoryItem?) {
        historyItem?.let { item ->
            when (historyItem) {
                is HistoryItem.HistoryTapItem ->
                    (item as? HistoryItem.HistoryTapItem)?.let {
                        onDrag(item.point.asList, item.layerId, item.color, item.color?.shape)
                    }
                is HistoryItem.HistoryClearItem ->
                    (item as? HistoryItem.HistoryClearItem)?.let { restoreClear(it) }
                is HistoryItem.HistoryDragItem ->
                    (item as? HistoryItem.HistoryDragItem)?.let { restoreDrag(it) }
            }
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

    fun getCurrentSelection(
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
                val test = (item.value?.get(layerId))
                item.key to test
            }.toMap()
    }

    private fun restoreClear(item: HistoryItem.HistoryClearItem) {
        item.data.forEach { map ->
            _selections.getOrPut(map.key) { mutableStateMapOf() }?.apply {
                map.value?.forEach { entries ->
                    put(entries.key, entries.value)
                }
            }
        }
    }

    private fun restoreDrag(historyItem: HistoryItem.HistoryDragItem) {
        historyItem.points.forEach { (point, selection) ->
            _selections.getOrPut(point) { mutableStateMapOf() }
                ?.put(historyItem.layerId, selection)
        }
    }
}