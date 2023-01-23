package com.jerry.boxes.ui.boxes.state

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Constraints
import com.jerry.boxes.cache.data.HistoryItem
import com.jerry.boxes.extensions.*
import com.jerry.boxes.ui.boxes.data.ColorAndShape
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.generateBoxes
import com.jerry.boxes.ui.boxes.history.UserHistory
import com.jerry.boxes.ui.boxes.state.enums.Direction
import com.jerry.boxes.ui.shapes.Shape
import com.jerry.boxes.util.DataResource
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Stable
class CanvasState(
    layersState: State<List<LayerUi>>,
    private val layerVisibilityState: SnapshotStateMap<Long, MutableState<Boolean>>,
    private val layerOrderState: SnapshotStateList<Long>,
    private val loadingState: State<Boolean>,
    private val historyCountState: State<Int>,
    private val snapShot: State<DataResource<SnapshotStateMap<Long, SnapshotStateMap<Point, SnapshotStateMap<Point, ColorAndShape>>>>>
) {
    private val _boxes = mutableStateMapOf<Point, RectF>()

    private val _selections get() = snapShot.value.data!!
    val selections get() = snapShot.value.data as Map<Long, Map<Point, Map<Point, ColorAndShape>>>

    val isLoading get() = snapShot.value.isLoading || loadingState.value

    val layers by layersState

    val layersVisibility get() = layerVisibilityState as Map<Long, State<Boolean>>
    val layersOrder get() = layerOrderState as List<Long>
    val historyCount by historyCountState

    val boxes = _boxes as Map<Point, RectF>

    val selectedLayer
        get() = layers.firstOrNull { it.selected }
            ?: layers.filter { it.on }.maxByOrNull { it.index } ?: layers.first()
    val hasLayersTurnedOn get() = layers.any { it.on }

    private var currentDragHistory: MutableMap<Point, ColorAndShape?> = mutableMapOf()

    fun clear() {
        val selectedLayer = layers.firstOrNull { it.selected }
        selectedLayer?.let {
            _selections[it.id]?.forEach { (_, points) ->
                points.clear()
            }
        }
    }

    fun getCurrentSelectedLayerSelections(layerId: Long) = _selections[layerId]?.flatMap {
        it.value.mapValues { entry ->
            entry.key to entry.value
        }.values
    }?.toMap() ?: emptyMap()

    fun getTapHistoryItem(point: Point, layerId: Long) =
        UserHistory(layerId, mapOf(point to getCurrentSelection(point, layerId)))

    fun closeDragHistory(layerId: Long) =
        UserHistory(layerId, currentDragHistory.toMap()).also {
            currentDragHistory.clear()
        }

    fun addToDragHistory(
        points: HashSet<Point>,
        layerId: Long,
        checkColor: ColorAndShape? = null
    ) {
        val filtered = points.filter { !currentDragHistory.keys.contains(it) }.toSet()
        val currentSelection = getCurrentSelections(filtered, layerId, checkColor)
        currentSelection.keys.forEach {
            if (!currentDragHistory.keys.contains(it)) {
                currentDragHistory[it] = currentSelection[it]
            }
        }
    }

    fun onUndo(layerId: Long?, historyItems: List<HistoryItem>) {
        if (layerId == null) return
        val layer = _selections.getOrPut(layerId) { mutableStateMapOf() }
        val quads = historyItems.quadrants
        quads.forEach {
            val quad = layer.getOrPut(it.key) { mutableStateMapOf() }
            val map = it.value.associate { historyItem ->
                Point(historyItem.x, historyItem.y) to historyItem.color?.let { color ->
                    ColorAndShape(Color(color), historyItem.shape ?: Shape.Box)
                }
            }
            quad.keys.removeAll(map.keys)
            quad.putAll(map.filterNotNullValues())
        }
    }

    fun onTap(
        point: Point,
        layerId: Long,
        currentColor: ColorAndShape,
        currentShape: Shape
    ): ColorAndShape? {
        val colorAndShape = currentColor.copy(shape = currentShape)
        val quadrant = point.quadrant
        val selection = when (_selections[layerId]?.get(quadrant)?.get(point) == colorAndShape) {
            true -> null
            else -> colorAndShape
        }
        _selections
            .getOrPut(layerId) {
                mutableStateMapOf()
            }.getOrPut(quadrant) {
                mutableStateMapOf()
            }.apply {
                when (selection) {
                    null -> remove(point)
                    else -> put(point, selection)
                }
            }
        return selection
    }

    fun onDrag(
        points: HashSet<Point>,
        layerId: Long,
        currentColor: ColorAndShape?
    ) {
        val pts = points.groupByQuadrant
        pts.forEach { (quad, list) ->
            _selections
                .getOrPut(layerId) { mutableStateMapOf() }
                .getOrPut(quad) { mutableStateMapOf() }
                .let { map ->
                    when (currentColor) {
                        null -> map.keys.removeAll(list.toSet())
                        else -> map.putAll(list.associateWith { currentColor })
                    }
                }
        }
    }

    fun move(topLeft: Point?, bottomRight: Point?, direction: Direction): UserHistory? {
        if (topLeft == null || bottomRight == null) return null
        val points = HashSet<Point>()
        for (c in min(topLeft.x, bottomRight.x)..max(topLeft.x, bottomRight.x)) {
            for (r in min(topLeft.y, bottomRight.y)..max(topLeft.y, bottomRight.y)) {
                points.add(Point(c, r))
            }
        }
        val layer = _selections[selectedLayer.id]
        val quads = points.groupByQuadrant
        val history = mutableMapOf<Point, ColorAndShape?>()
        quads.forEach { (quad, pnts) ->
            val aggregatedPoints = layer?.get(quad)?.filterKeys { pnts.contains(it) }
            val adjusted = aggregatedPoints?.map { entry ->
                entry.key.adjust(direction) to entry.value
            }?.toMap() ?: emptyMap()
            val merged = aggregatedPoints?.keys?.union(adjusted.keys)?.toSet() ?: emptySet()
            history.putAll(getCurrentSelections(merged, selectedLayer.id, filter = false))
            adjusted.takeIf { it.isNotEmpty() }?.let {
                layer?.get(quad)?.keys?.removeAll(merged)
                layer?.get(quad)?.putAll(adjusted)
            }
        }
        return UserHistory(selectedLayer.id, history)
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
                min.roundToInt().toFloat(),
                xOffSet.roundToInt().toFloat(),
                (offset + yOffSet).roundToInt().toFloat()
            )
        )
    }

    private fun getCurrentSelection(
        point: Point,
        layerId: Long
    ): ColorAndShape? {
        return _selections[layerId]?.get(point.quadrant)?.get(point)
    }

    fun getCurrentSelection(point: Point): ColorAndShape? {
        val turnedOnLayers = layers.filter { it.on }.sortedBy { it.index }.reversed()
        return turnedOnLayers.firstOrNull { _selections[it.id]?.get(point.quadrant)?.get(point) != null }
            ?.let { getCurrentSelection(point, it.id) }
    }

    private fun getCurrentSelections(
        points: Set<Point>,
        layerId: Long,
        checkColor: ColorAndShape? = null,
        filter: Boolean = true
    ): Map<Point, ColorAndShape?> {
        return points.associateWith { _selections[layerId]?.get(it.quadrant)?.get(it) }.run {
            when (filter) {
                true -> filterNot { it.value == checkColor }
                else -> this
            }
        }
    }
}
