package com.jerry.boxes.ui.boxes

import android.graphics.Point
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.boxes.repository.BoxesRepository
import com.jerry.boxes.cache.data.HistoryItem
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.extensions.addIfNotFound
import com.jerry.boxes.extensions.isNotOutside
import com.jerry.boxes.extensions.safeLet
import com.jerry.boxes.ui.boxes.data.ColorAndShape
import com.jerry.boxes.ui.boxes.data.Export
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.history.UserHistory
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.ui.shapes.Shape
import com.jerry.boxes.util.CoroutineContextProvider
import com.jerry.boxes.util.DataResource
import com.jerry.boxes.util.SavedHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import java.util.*

class BoxesViewModel(
    private val handle: SavedStateHandle,
    private val boxesRepository: BoxesRepository,
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

    private var usedColorsHandle by SavedHandle<MutableList<ColorAndShape>>(
        handle,
        USED_COLORS_STATE,
        mutableListOf()
    )

    private val projectId = BoxesMainDestination.argsFrom(handle).projectId

    private val colorsMutex = Mutex()
    val usedColors get() = usedColorsHandle as List<ColorAndShape>

    val projectFlow = boxesRepository.getProjectFlowById(projectId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(1000),
            null
        )

    val pixelsFlow = boxesRepository.getPixelsFlow(projectId)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            DataResource.loading(SnapshotStateMap())
        )

    private val _loading = MutableStateFlow(false)
    val loadingState = _loading.asStateFlow()

    private val _exportedFlow = MutableSharedFlow<Export>(0, 1, BufferOverflow.DROP_OLDEST)
    val exportedFlow = _exportedFlow.asSharedFlow()

    private var fillJob: Job? = null

    private val layerFlow = boxesRepository.getLayersFlow(projectId)
    val layerStateFlow =
        combine(layerState, layerFlow, selectedLayerStateFlow) { state, layers, selectedLayer ->
            if (layerStateHandle == null) {
                layerStateHandle =
                    layers.filter { it.on }.associate { it.id to it.on }.toMutableMap()
            }

            // Remove any layers in the state that are not here anymore
            safeLet(state?.map { it.key }, layers.map { it.id }) { states, lays ->
                val diff = states.filterNot { lays.contains(it) }
                diff.forEach {
                    state?.remove(it)
                    if (selectedLayer == it) {
                        // The selected layer was deleted so we need
                        // to automatically assign another layer
                        selectedLayerStateHandle = layers.firstOrNull()?.id
                        selectedLayerStateHandle?.let { id ->
                            setLayerOnOrOff(id, true)
                        }
                    }
                }
            }

            val selected =
                if (selectedLayer != null && layers.any { it.id == selectedLayer }) {
                    selectedLayer
                } else {
                    layers.firstOrNull { it.on }?.id
                }

            layers.forEach {
                state?.putIfAbsent(it.id, it.on)
            }

            layers.map {
                val isOn = state?.getOrDefault(it.id, it.on) == true
                LayerUi(
                    it.id,
                    it.projectId,
                    it.index,
                    it.name,
                    on = isOn,
                    selected = selected == it.id,
                    visibilityEnabled = !(isOn && state?.count { it.value } == 1),
                    showControls = layers.size > 1
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), emptyList())

    val historyCountFlow = layerStateFlow.flatMapLatest { layers ->
        layers.firstOrNull { it.selected }?.let { boxesRepository.getLayerHistoryCount(it.id) }
            ?: emptyFlow()
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

    fun updateProjectShape(shape: Shape) {
        viewModelScope.launch {
            boxesRepository.updateProjectShape(projectId, shape)
        }
    }

    fun updateProjectColor(color: ColorAndShape) {
        viewModelScope.launch {
            boxesRepository.updateProjectColor(projectId, color)
        }
    }

    fun updateProjectShowGrid(showGrid: Boolean) {
        viewModelScope.launch {
            boxesRepository.updateProjectShowGrid(projectId, showGrid)
        }
    }

    fun updateProjectShowPngBg(showPngBg: Boolean) {
        viewModelScope.launch {
            boxesRepository.updateProjectShowPngBg(projectId, showPngBg)
        }
    }

    suspend fun addToHistory(userHistory: UserHistory) {
        if (userHistory.points.isEmpty()) return
        viewModelScope.launch {
            boxesRepository.updateHistory(userHistory.layerId, userHistory.points)
        }
    }

    suspend fun getLastHistoryItem(layerId: Long): List<HistoryItem> {
        return boxesRepository.getLastHistoryItem(layerId)
    }

    suspend fun addUsedColor(color: ColorAndShape) {
        colorsMutex.withLock {
            usedColorsHandle?.addIfNotFound(color)
            if ((usedColorsHandle?.size ?: 0) > 10) {
                usedColorsHandle?.removeFirst()
            }
        }
    }

    private var exportJob: Job? = null
    fun export(
        project: Project,
        selections: Map<Long, Map<Point, ColorAndShape>>,
        layers: List<LayerUi>,
        imageSize: Int,
        isExport: Boolean
    ) {
        if (exportJob?.isActive == true) return
        exportJob = viewModelScope.launch(cc.io) {
            _loading.value = true
            try {
                val path = boxesRepository.export(project, project.name, selections, layers, imageSize, true)
                path?.let { _exportedFlow.emit(Export(it, null, isExport)) }
            } catch (t: Throwable) {
                _exportedFlow.emit(Export(null, t.message, isExport))
            }
            _loading.value = false
        }
    }

    fun selectLayer(layerId: Long) {
        selectedLayerStateHandle = layerId
    }

    fun addLayer(
        name: String,
        index: Int,
        selections: Map<Long, Map<Point, ColorAndShape?>?>
    ) {
        viewModelScope.launch {
            selectLayer(boxesRepository.addLayer(projectId, name, index, selections))
        }
    }

    fun fill(
        point: Point,
        layerId: Long,
        currentColor: ColorAndShape,
        currentShape: Shape,
        columns: Int,
        rows: Int
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

    fun saveProject(
        project: Project,
        boxes: List<Point>? = null,
        selections: Map<Long, Map<Point, ColorAndShape>>,
        layers: List<LayerUi>
    ) {
        boxesRepository.save(project, boxes, selections, layers)
    }

    private suspend fun fillInArea(
        point: Point,
        layerId: Long,
        color: ColorAndShape,
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
        val history = HashMap<Point, ColorAndShape?>()
        history.putAll(currentSelection)
        layer?.keys?.removeAll(fillMap)
        layer?.putAll(fillMap.map { it to newColor })
        if (history.isNotEmpty()) {
            addToHistory(UserHistory(layerId, history))
        }
    }

    companion object {
        private const val LAYER_LIST_STATE = "LAYER_LIST_STATE"
        private const val USED_COLORS_STATE = "USED_COLOR_STATE"
        private const val SELECTED_LAYER = "SELECTED_LAYER"

        private const val FILL_TIMEOUT = 10000L
    }
}
