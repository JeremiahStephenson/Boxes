package com.jerry.boxes.ui.boxes

import android.graphics.Point
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
import com.jerry.boxes.extensions.isNotOutside
import com.jerry.boxes.extensions.safeLet
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.history.UserHistory
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.ui.shapes.Shape
import com.jerry.boxes.util.CoroutineContextProvider
import com.jerry.boxes.util.DataResource
import com.jerry.boxes.util.SavedHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.time.Instant
import java.util.*

class BoxesViewModel(
    private val handle: SavedStateHandle,
    private val boxesDao: BoxesDao,
    private val boxesDatabase: BoxesDatabase,
    private val cc: CoroutineContextProvider
) : ViewModel() {

    private var layerStateHandle by SavedHandle<MutableMap<Long, Boolean>?>(
        handle,
        LAYER_LIST_STATE,
        null
    )

    private val layerState = handle.getStateFlow<MutableMap<Long, Boolean>?>(
        LAYER_LIST_STATE,
        null
    )

    private var selectedLayerStateHandle by SavedHandle<Long?>(
        handle,
        SELECTED_LAYER,
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

    val projectFlow = boxesDao.getProjectAndLayersFlowById(projectId)
        .filterNotNull()
        .map { it.copy(layers = it.layers.sortedByDescending { layer -> layer.index }) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(1000),
            null
        )

    val pixelsFlow = boxesDao.getProjectPixelsFlow(projectId)
        .map {
            //delay(2000)
            Timber.d("ProjectTest - pixels")
            DataResource.done(generateSelections(it))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            DataResource.loading(SnapshotStateMap())
        )

    private val _loading = MutableStateFlow(false)
    val loadingState = _loading.asStateFlow()

    private var fillJob: Job? = null

    private val layerFlow = projectFlow.map { it?.layers }
    val layerStateFlow =
        combine(layerState, layerFlow, selectedLayerStateFlow) { state, layers, selectedLayer ->
            if (layerStateHandle == null) {
                layerStateHandle =
                    layers?.filter { it.on }?.associate { it.id to it.on }?.toMutableMap()
            }

            // Remove any layers in the state that are not here anymore
            safeLet(state?.map { it.key }, layers?.map { it.id }) { states, lays ->
                val diff = states.filterNot { lays.contains(it) }
                diff.forEach {
                    state?.remove(it)
                    if (selectedLayer == it) {
                        // The selected layer was deleted so we need
                        // to automatically assign another layer
                        selectedLayerStateHandle = layers?.firstOrNull()?.id
                        selectedLayerStateHandle?.let { id ->
                            setLayerOnOrOff(id, true)
                        }
                    }
                }
            }

            val selected =
                if (selectedLayer != null && layers?.any { it.id == selectedLayer } == true) {
                    selectedLayer
                } else {
                    layers?.firstOrNull { it.on }?.id
                }

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

    val historyCountFlow = layerStateFlow.flatMapLatest { layers ->
        layers.firstOrNull { it.selected }?.let { boxesDao.layerHistoryCount(it.id) } ?: emptyFlow()
    }

    fun setLayerOnOrOff(layerId: Long, on: Boolean) {
        if (!on && (layerStateHandle?.count { it.value } ?: 0) <= 1) return
        layerStateHandle = (layerStateHandle?.toMutableMap() ?: mutableMapOf()).apply {
            put(layerId, on)
        }
        if (!on && (layerId == selectedLayerStateHandle || selectedLayerStateHandle == null)) {
            selectedLayerStateHandle = layerStateFlow.value.firstOrNull { it.on }?.id
        }
    }

    suspend fun addToHistory(userHistory: UserHistory) {
        if (userHistory.points.isEmpty()) return
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
        name: String,
        index: Int,
        selections: Map<Long, Map<Point, SerializableColor?>?>
    ) {
        viewModelScope.launch {
            updateDatabase {
                saveProject(selections = selections)
                selectLayer(boxesDao.insertLayer(Layer(projectId, index, name, true)))
            }
        }
    }

    fun save(
        boxes: List<Point>? = null,
        selections: Map<Long, Map<Point, SerializableColor>>,
        layers: List<Pair<Long, Boolean>>,
        currentColor: SerializableColor,
        currentShape: Shape,
        showGrid: Boolean,
        showPngBg: Boolean
    ) {
        viewModelScope.launch {
            updateDatabase {
                saveProject(boxes, selections, currentColor, currentShape, showGrid, showPngBg)
                layers.forEach {
                    boxesDao.turnOnOrOffLayer(it.second, it.first)
                }
            }
        }
    }

    fun fill(
        point: Point,
        layerId: Long,
        currentColor: SerializableColor,
        currentShape: Shape,
        columns: Int,
        rows: Int,
    ) {
        if (fillJob?.isActive == true) return
        fillJob = viewModelScope.launch(cc.io) {
            _loading.value = true
            withTimeout(FILL_TIMEOUT) {
                try {
                    fillInArea(point, layerId, currentColor, currentShape, columns, rows)
                    _loading.value = false
                } catch (t: Throwable) {
                    // todo handle this
                    _loading.value = false
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
        selections: Map<Long, Map<Point, SerializableColor?>?>,
        currentColor: SerializableColor? = null,
        currentShape: Shape? = null,
        showGrid: Boolean? = null,
        showPngBg: Boolean? = null
    ) {
        val now = Instant.now().toEpochMilli()

        val list = selections.flatMap { layer ->
            layer.value?.filterKeys { boxes?.contains(it) ?: true }?.map {
                Pixel(
                    layer.key,
                    it.key.x,
                    it.key.y,
                    it.value!!.color.toArgb(),
                    it.value!!.shape,
                    now
                )
            } ?: emptyList()
        }
        boxesDao.insertAllPixels(list)
        boxesDao.deletePixelsFromProject(projectId, now)
        safeLet(currentColor, currentShape, showGrid, showPngBg) { color, shape, grid, png ->
            boxesDao.updateProjectColorAndShape(
                color.color.toArgb(),
                shape,
                projectId,
                grid,
                png
            )
        }
    }

    private suspend fun fillInArea(
        point: Point,
        layerId: Long,
        color: SerializableColor,
        shape: Shape,
        columns: Int,
        rows: Int
    ) {
        val fillMap = HashSet<Point>()
        val iterator = LinkedList<Point>().apply { add(point) }
        val newColor = color.copy(shape = shape)
        val currentColor = pixelsFlow.value.data?.get(layerId)?.get(point)
        if (currentColor == newColor) return
        while (iterator.isNotEmpty()) {
            iterator.peek()?.let { p ->
                if (p.isNotOutside(columns, rows) &&
                    !fillMap.contains(p) && pixelsFlow.value.data?.get(layerId)
                        ?.get(p) == currentColor
                ) {
                    fillMap.add(p)
                    iterator.add(Point(p.x - 1, p.y))
                    iterator.add(Point(p.x + 1, p.y))
                    iterator.add(Point(p.x, p.y - 1))
                    iterator.add(Point(p.x, p.y + 1))
                }
            }
            iterator.pop()
        }
        val layer = pixelsFlow.value.data?.getOrPut(layerId) { SnapshotStateMap() }
        val currentSelection = layer?.let { l -> fillMap.associateWith { l[it] } } ?: emptyMap()
        val history = HashMap<Point, SerializableColor?>()
        history.putAll(currentSelection)
        layer?.keys?.removeAll(fillMap)
        layer?.putAll(fillMap.map { it to newColor })
        if (history.isNotEmpty()) {
            addToHistory(UserHistory(layerId, history))
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
        private const val USED_COLORS_STATE = "USED_COLOR_STATE"
        private const val SELECTED_LAYER = "SELECTED_LAYER"

        private const val MAX_HISTORY_PER_LAYER = 20

        private const val FILL_TIMEOUT = 10000L
    }
}