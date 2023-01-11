package com.jerry.boxes.ui.boxes

import android.graphics.Point
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.jerry.boxes.cache.BoxesDao
import com.jerry.boxes.cache.BoxesDatabase
import com.jerry.boxes.cache.data.History
import com.jerry.boxes.cache.data.HistoryItem
import com.jerry.boxes.cache.data.Layer
import com.jerry.boxes.cache.data.Pixel
import com.jerry.boxes.extensions.addIfNotFound
import com.jerry.boxes.extensions.safeLet
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.history.UserHistory
import com.jerry.boxes.ui.boxes.shapes.Shape
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.util.SavedHandle
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant

class BoxesViewModel(
    private val handle: SavedStateHandle,
    private val boxesDao: BoxesDao,
    private val boxesDatabase: BoxesDatabase
) : ViewModel() {

    private var layerStateHandle by SavedHandle<MutableMap<Long, Boolean>?>(
        handle,
        LAYER_LIST_STATE,
        null
    )

    private var selectedLayerStateHandle by SavedHandle<Long?>(
        handle,
        SELECTED_LAYER,
        null
    )

    private val layerState = handle.getStateFlow<MutableMap<Long, Boolean>?>(
        LAYER_LIST_STATE,
        null
    )

    private val selectedLayerStateFlow = handle.getStateFlow<Long?>(SELECTED_LAYER, null)

    private var usedColorsHandle by SavedHandle<MutableList<SerializableColor>>(
        handle,
        USED_COLORS_STATE,
        mutableListOf()
    )

    private val projectId = BoxesMainDestination.argsFrom(handle).projectId

    private val colorsMutex = Mutex()
    val usedColors get() = usedColorsHandle as List<SerializableColor>

    val projectFlow = boxesDao.getFullProjectFlowById(projectId)
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )

    private val layerFlow =
        projectFlow.map { it?.layers?.map { layerAndPixel -> layerAndPixel.layer } }
    val layerStateFlow =
        combine(layerState, layerFlow, selectedLayerStateFlow) { state, layers, selectedLayer ->
            if (layerStateHandle == null) {
                layerStateHandle =
                    layers?.filter { it.on }?.associate { it.id to it.on }?.toMutableMap()
            }
            val selected = selectedLayer ?: layers?.lastOrNull { it.on }?.id

            layers?.forEach {
                state?.putIfAbsent(it.id, it.on)
            }

            layers?.map {
                val isOn = state?.getOrDefault(it.id, it.on) == true
                LayerUi(it.id,
                    it.projectId,
                    it.index,
                    it.name,
                    on = isOn,
                    selected = selected == it.id,
                    visibilityEnabled = !(isOn && state?.count { it.value } == 1),
                    showControls = layers.size > 1
                )
            } ?: emptyList()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), emptyList())

    fun setLayerOnOrOff(layerId: Long, on: Boolean) {
        if (!on && (layerStateHandle?.count { it.value } ?: 0) <= 1) return
        layerStateHandle = (layerStateHandle?.toMutableMap() ?: mutableMapOf()).apply {
            put(layerId, on)
        }
        if (!on && (layerId == selectedLayerStateHandle || selectedLayerStateHandle == null)) {
            selectedLayerStateHandle = layerStateFlow.value.lastOrNull { it.on }?.id
        }
    }

    suspend fun addToHistory(userHistory: UserHistory) {
        viewModelScope.launch {
            updateDatabase {
                updateHistory(userHistory.layerId, userHistory.points)
            }
        }
    }

    suspend fun getLastHistoryItem(layerId: Long): List<HistoryItem> {
        val max = boxesDao.findMaxIndexForHistory(layerId)
        val history = boxesDao.findMaxHistory(layerId, max)
        return (history?.let {
            boxesDao.findAllHistoryItems(history.id)
        } ?: emptyList()).also {
            history?.let { boxesDao.deleteHistory(it.id) }
        }
    }

    suspend fun addUsedColor(color: SerializableColor) {
        colorsMutex.withLock {
            usedColorsHandle?.addIfNotFound(color)
            if ((usedColorsHandle?.size ?: 0) > 10) {
                usedColorsHandle?.removeFirst()
            }
        }
    }

    fun selectLayer(layerId: Long) {
        selectedLayerStateHandle = layerId
    }

    fun addLayer(
        index: Int,
        selections: Map<Point, Map<Long, SerializableColor?>?>
    ) {
        viewModelScope.launch {
            updateDatabase {
                saveProject(selections = selections)
                boxesDao.insertLayer(Layer(projectId, index, "Layer ${index + 1}", true))
            }
        }
    }

    fun save(
        boxes: List<Point>? = null,
        selections: Map<Point, Map<Long, SerializableColor?>?>,
        layers: List<Pair<Long, Boolean>>,
        currentColor: SerializableColor,
        currentShape: Shape
    ) {
        viewModelScope.launch {
            updateDatabase {
                saveProject(boxes, selections, currentColor, currentShape)
                layers.forEach {
                    boxesDao.turnOnOrOffLayer(it.second, it.first)
                }
            }
        }
    }

    private suspend fun updateDatabase(block: suspend () -> Unit) {
        boxesDatabase.withTransaction {
            block()
        }
    }

    private suspend fun saveProject(
        boxes: List<Point>? = null,
        selections: Map<Point, Map<Long, SerializableColor?>?>,
        currentColor: SerializableColor? = null,
        currentShape: Shape? = null
    ) {
        val now = Instant.now().toEpochMilli()
        val list =
            selections.filterKeys { boxes?.contains(it) ?: true }.filterValues { it != null }
                .flatMap { point ->
                    point.value?.filterValues { it != null }?.map {
                        Pixel(
                            it.key,
                            point.key.x,
                            point.key.y,
                            it.value!!.hue,
                            it.value!!.saturation,
                            it.value!!.value,
                            it.value!!.alpha,
                            it.value!!.shape,
                            now
                        )
                    } ?: emptyList()
                }
        boxesDao.insertAllPixels(list)
        boxesDao.deletePixelsFromProject(projectId, now)
        safeLet(currentColor, currentShape) { color, shape ->
            boxesDao.updateProjectColorAndShape(
                color.color.toArgb(),
                shape,
                projectId
            )
        }
    }

    private suspend fun updateHistory(layerId: Long, points: Map<Point, SerializableColor?>) {
        val index = boxesDao.findMaxIndexForHistory(layerId)
        val historyId = boxesDao.insertHistory(History(layerId, index + 1))
        points.forEach { (point, color) ->
            boxesDao.insertHistoryItem(
                HistoryItem(
                    historyId,
                    point.x,
                    point.y,
                    color?.color?.toArgb(),
                    color?.shape
                )
            )
        }
        if (index >= MAX_HISTORY_PER_LAYER) {
            val min = boxesDao.findMinIndexForHistory(layerId)
            boxesDao.cleanHistory(min, layerId)
            boxesDao.updateIndicies(layerId)
        }
    }

    companion object {
        private const val LAYER_LIST_STATE = "LAYER_LIST_STATE"
        private const val HISTORY_STATE = "HISTORY_STATE"
        private const val USED_COLORS_STATE = "USED_COLOR_STATE"
        private const val SELECTED_LAYER = "SELECTED_LAYER"

        private const val MAX_HISTORY_PER_LAYER = 20
    }
}