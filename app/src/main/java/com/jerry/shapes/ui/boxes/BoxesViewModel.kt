package com.jerry.shapes.ui.boxes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Point
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.cache.data.Project
import com.jerry.shapes.extensions.*
import com.jerry.shapes.repository.BoxesRepository
import com.jerry.shapes.ui.boxes.data.LayerUi
import com.jerry.shapes.ui.boxes.data.UiEvent
import com.jerry.shapes.ui.boxes.history.UserHistory
import com.jerry.shapes.ui.destinations.BoxesMainDestination
import com.jerry.shapes.ui.shapes.Shape
import com.jerry.shapes.util.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

class BoxesViewModel(
    private val handle: SavedStateHandle,
    private val boxesRepository: BoxesRepository,
    private val cc: CoroutineContextProvider,
    private val analytics: FirebaseAnalytics
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
    val usedColors get() = ImmutableList(usedColorsHandle as List<ColorAndShape>)

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
            Resource.loading(SnapshotStateMap())
        )

    private val _loading = MutableStateFlow(false)
    val loadingState = _loading.asStateFlow()

    private val _uiEventFlow = MutableSharedFlow<UiEvent>(0, 1, BufferOverflow.DROP_OLDEST)
    val uiEventFlow = _uiEventFlow.asSharedFlow()

    private var fillJob: Job? = null

    val layersVisibilityList = mutableStateMapOf<Long, MutableState<Boolean>>()
    val layersOrderStateList = mutableStateListOf<Long>()

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
            }.onEach {
                val current = layersVisibilityList.get(it.id)?.value
                if (current != it.on) {
                    layersVisibilityList.getOrPut(it.id) { mutableStateOf(it.on) }
                        .apply { this.value = it.on }
                }
            }.also {
                val order = it.sortedBy { layer -> layer.index }.map { layer -> layer.id }
                if (order != layersOrderStateList) {
                    layersOrderStateList.clear()
                    layersOrderStateList.addAll(order)
                }
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

    suspend fun addUsedColor(color: ColorAndShape) {
        colorsMutex.withLock {
            usedColorsHandle?.addIfNotFound(color)
            if ((usedColorsHandle?.size ?: 0) > 10) {
                usedColorsHandle?.removeFirst()
            }
        }
    }

    private var undoJob: Job? = null
    fun onUndo(layerId: Long?) {
        if (undoJob?.isActive == true) return
        undoJob = viewModelScope.launch(cc.io) {
            if (layerId == null) return@launch
            val layer = pixelsFlow.value.data?.getOrPut(layerId) { mutableStateMapOf() }
            val quads = boxesRepository.getLastHistoryItem(layerId).quadrants
            quads.forEach {
                val quad = layer?.getOrPut(it.key) { mutableStateMapOf() }
                val map = it.value.associate { historyItem ->
                    Point(historyItem.x, historyItem.y) to historyItem.color?.let { color ->
                        ColorAndShape(Color(color), historyItem.shape ?: Shape.Box)
                    }
                }
                quad?.keys?.removeAll(map.keys)
                quad?.putAll(map.filterNotNullValues())
            }
        }
    }

    private var exportJob: Job? = null
    fun export(
        project: Project,
        selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
        layers: Collection<LayerUi>,
        imageSize: Int,
        exportType: ExportType
    ) {
        if (exportJob?.isActive == true) return
        exportJob = viewModelScope.launch(cc.io) {
            _loading.value = true
            try {
                val path = boxesRepository.export(
                    project,
                    project.name,
                    selections,
                    layers,
                    imageSize,
                    exportType
                )
                path?.let {
                    _uiEventFlow.emit(UiEvent.Export(it, exportType))
                }
            } catch (t: Throwable) {
                _uiEventFlow.emit(UiEvent.Error(t.message))
                analytics.logError(t)
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
        selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>
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
                    _uiEventFlow.emit(UiEvent.Error(t.message))
                    _loading.value = false
                }
            }
        }
    }

    fun saveProject(
        project: Project,
        boxes: List<Point>? = null,
        selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
        layers: Collection<LayerUi>
    ) {
        try {
            boxesRepository.save(project, boxes, selections, layers)
        } catch (t: Throwable) {
            analytics.logError(t)
            _uiEventFlow.tryEmit(UiEvent.Error(t.message))
        }
    }

    fun importImage(context: Context, layerId: Long, columns: Int?, rows: Int?, uri: Uri) {
        if (uri.path.isNullOrEmpty() || columns == null || rows == null) return
        viewModelScope.launch(cc.io) {
            _loading.value = true
            try {
                val bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    MediaStore.Images.Media.getBitmap(
                        context.contentResolver,
                        uri
                    )
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
                }

                val dimens = when {
                    columns < rows -> {
                        val newRows = ceil((columns.toFloat() / bitmap.width.toFloat()) * bitmap.height.toFloat())
                        columns to newRows.toInt()
                    }
                    rows < columns -> {
                        val newCols = ceil((rows.toFloat() / bitmap.height.toFloat()) * bitmap.width.toFloat())
                        newCols.toInt() to rows
                    }
                    else -> {
                        when (bitmap.height <= bitmap.width) {
                            true -> {
                                val newRows = ceil((columns.toFloat() / bitmap.width.toFloat()) * bitmap.height.toFloat())
                                columns to newRows.toInt()
                            }
                            else -> {
                                val newCols = ceil((rows.toFloat() / bitmap.height.toFloat()) * bitmap.width.toFloat())
                                newCols.toInt() to rows
                            }
                        }
                    }
                }
                val boxSize = min(
                    bitmap.width.toFloat() / dimens.first.toFloat(),
                    bitmap.height.toFloat() / dimens.second.toFloat()
                )
                val boxes = generateBoxes(dimens.first, dimens.second, boxSize, 0F, 0F)
                val points = HashMap<Point, ColorAndShape>()
                boxes.forEach {
                    val region = it.value
                    val point = it.key
                    bitmap.findDominateColor(region).let { color ->
                        points[point] = ColorAndShape(Color(color))
                    }
                }
                val layer = pixelsFlow.value.data?.getOrPut(layerId) { mutableStateMapOf() }
                layer?.let { l ->
                    val quads = points.keys.groupByQuadrant
                    quads.forEach { (quad, list) ->
                        l.getOrPut(quad) { mutableStateMapOf() }.keys.removeAll(list.toSet())
                        list.associateWith { points[it] }.filterNotNullValues()
                            .let { l.getOrPut(quad) { mutableStateMapOf() }.putAll(it) }
                    }
                }
            } catch (t: Throwable) {
                _uiEventFlow.emit(UiEvent.Error(t.message))
            }
            _loading.value = false
        }
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
        val currentColor = pixelsFlow.value.data?.get(layerId)?.get(point.quadrant)?.get(point)
        if (currentColor == newColor) return
        while (iterator.isNotEmpty()) {
            iterator.peek()?.let { p ->
                if (p.isNotOutside(columns, rows) &&
                    !fillMap.contains(p) && pixelsFlow.value.data?.get(layerId)?.get(p.quadrant)
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
        val currentSelection =
            layer?.let { l -> fillMap.associateWith { l[it.quadrant]?.get(it) } } ?: emptyMap()
        val history = HashMap<Point, ColorAndShape?>()
        history.putAll(currentSelection)
        val quadrants = fillMap.groupByQuadrant
        quadrants.forEach { (quad, list) ->
            layer?.get(quad)?.keys?.removeAll(list.toSet())
        }
        quadrants.forEach { (quad, list) ->
            layer?.getOrPut(quad) { mutableStateMapOf() }?.putAll(list.map { it to newColor })
        }
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
