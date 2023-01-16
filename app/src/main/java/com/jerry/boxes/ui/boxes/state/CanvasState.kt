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
import com.jerry.boxes.ui.boxes.SerializableColor
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.generateBoxes
import com.jerry.boxes.ui.boxes.history.UserHistory
import com.jerry.boxes.ui.shapes.Shape
import com.jerry.boxes.util.DataResource
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Stable
class CanvasState(
    layersState: State<List<LayerUi>>,
    private val loadingState: State<Boolean>,
    private val snapShot: State<DataResource<SnapshotStateMap<Long, SnapshotStateMap<Point, SerializableColor>>>>
) {
    private val _boxes = mutableStateMapOf<Point, RectF>()

    private val _selections get() = snapShot.value.data!!
    val selections get() = snapShot.value.data as Map<Long, Map<Point, SerializableColor>>

    val isLoading get() = snapShot.value.isLoading || loadingState.value

    val layers by layersState

    val boxes = _boxes as Map<Point, RectF>

    val selectedLayer
        get() = layers.firstOrNull { it.selected }
            ?: layers.filter { it.on }.maxByOrNull { it.index } ?: layers.first()
    val hasLayersTurnedOn get() = layers.any { it.on }

    private var currentDragHistory: MutableMap<Point, SerializableColor?> = mutableMapOf()

    fun clear() {
        val selectedLayer = layers.firstOrNull { it.selected }
        selectedLayer?.let {
            _selections[it.id]?.clear()
        }
    }

    fun getCurrentSelectedLayerSelections(layerId: Long) =
        _selections[layerId]?.toMap() ?: emptyMap()

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
            _selections.getOrPut(layerId) { mutableStateMapOf() }.apply {
                when (item.color) {
                    null -> this.remove(Point(item.x, item.y))
                    else -> this[Point(item.x, item.y)] = with(HsvColor.from(Color(item.color))) {
                        SerializableColor(
                            this.hue,
                            this.saturation,
                            this.value,
                            this.alpha,
                            item.shape ?: Shape.Box
                        )
                    }
                }
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
        val selection = when (_selections[layerId]?.get(point) == colorAndShape) {
            true -> null
            else -> colorAndShape
        }
        _selections.getOrPut(layerId) { mutableStateMapOf() }.apply {
            when (selection) {
                null -> remove(point)
                else -> put(point, selection)
            }
        }
        return selection
    }

    fun onDrag(
        points: Collection<Point>,
        layerId: Long,
        currentColor: SerializableColor?,
        currentShape: Shape?
    ) {
        _selections.getOrPut(layerId) { mutableStateMapOf() }.let { map ->
            when (val color = currentColor?.copy(shape = currentShape ?: Shape.Box)) {
                null -> points.forEach { map.remove(it) }
                else -> map.putAll(points.associateWith { color })
            }
        }
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
    ): SerializableColor? {
        return _selections[layerId]?.get(point)
    }

    fun getCurrentSelection(point: Point): SerializableColor? {
        val turnedOnLayers = layers.filter { it.on }.sortedBy { it.index }.reversed()
        return turnedOnLayers.firstOrNull { _selections[it.id]?.get(point) != null }
            ?.let { getCurrentSelection(point, it.id) }
    }

    private fun getCurrentSelections(
        points: List<Point>,
        layerId: Long
    ): Map<Point, SerializableColor?> {
        return points.associateWith { _selections[layerId]?.get(it) }
    }
}