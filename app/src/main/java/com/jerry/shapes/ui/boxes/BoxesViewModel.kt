package com.jerry.shapes.ui.boxes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Point
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
import com.jerry.shapes.extensions.addIfNotFound
import com.jerry.shapes.extensions.adjust
import com.jerry.shapes.extensions.filterNotNullValues
import com.jerry.shapes.extensions.findDominateColor
import com.jerry.shapes.extensions.groupByQuadrant
import com.jerry.shapes.extensions.isNotOutside
import com.jerry.shapes.extensions.logError
import com.jerry.shapes.extensions.quadrant
import com.jerry.shapes.extensions.quadrants
import com.jerry.shapes.extensions.safeLet
import com.jerry.shapes.repository.BoxesRepository
import com.jerry.shapes.ui.boxes.data.LayerState
import com.jerry.shapes.ui.boxes.data.UiEvent
import com.jerry.shapes.ui.boxes.history.UserHistory
import com.jerry.shapes.ui.boxes.state.enums.Direction
import com.jerry.shapes.ui.shapes.Shape
import com.jerry.shapes.util.CoroutineContextProvider
import com.jerry.shapes.util.ExportType
import com.jerry.shapes.util.ImmutableList
import com.jerry.shapes.util.Resource
import com.jerry.shapes.util.SavedHandle
import com.jerry.shapes.util.generateBoxes
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds

class BoxesViewModel(
    private val handle: SavedStateHandle,
    private val boxesRepository: BoxesRepository,
    private val cc: CoroutineContextProvider,
    private val analytics: FirebaseAnalytics,
) : ViewModel() {
    private var layerStateHandle by SavedHandle<MutableMap<Long, Boolean>?>(
        handle,
        LAYER_LIST_STATE,
        null,
    )

    private val layerState =
        handle.getStateFlow<MutableMap<Long, Boolean>?>(
            LAYER_LIST_STATE,
            null,
        )

    private var selectedLayerStateHandle by SavedHandle<Long?>(
        handle,
        SELECTED_LAYER,
        null,
    )

    private val selectedLayerStateFlow = handle.getStateFlow<Long?>(SELECTED_LAYER, null)

    private var usedColorsHandle by SavedHandle<MutableList<ColorAndShape>>(
        handle,
        USED_COLORS_STATE,
        mutableListOf(),
    )

    private val projectId = MutableStateFlow<Long?>(null)

    private val colorsMutex = Mutex()
    val usedColors get() = ImmutableList(usedColorsHandle as List<ColorAndShape>)

    val projectFlow =
        projectId
            .filterNotNull()
            .flatMapLatest { projectId ->
                boxesRepository.getProjectFlowById(projectId)
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(1000),
                null,
            )

    val pixelsFlow =
        projectId
            .filterNotNull()
            .flatMapLatest { projectId ->
                boxesRepository
                    .getPixelsFlow(projectId)
            }.stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                Resource.loading(SnapshotStateMap()),
            )

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _uiEventFlow = MutableSharedFlow<UiEvent>(0, 1, BufferOverflow.DROP_OLDEST)
    val uiEventFlow = _uiEventFlow.asSharedFlow()

    private var fillJob: Job? = null

    val layersVisibilityList = mutableStateMapOf<Long, MutableState<Boolean>>()
    val layersOrderStateList = mutableStateListOf<Long>()

    private val layerFlow =
        projectId
            .filterNotNull()
            .flatMapLatest { projectId ->
                boxesRepository.getLayersFlow(projectId)
            }
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

            layers
                .map {
                    val isOn = state?.getOrDefault(it.id, it.on) == true
                    LayerState(
                        it.id,
                        it.projectId,
                        it.index,
                        it.name,
                        on = isOn,
                        selected = selected == it.id,
                        visibilityEnabled = !(isOn && state.values.count() == 1),
                        showControls = layers.size > 1,
                    )
                }.onEach {
                    val current = layersVisibilityList.get(it.id)?.value
                    if (current != it.on) {
                        layersVisibilityList
                            .getOrPut(it.id) { mutableStateOf(it.on) }
                            .apply { this.value = it.on }
                    }
                }.also {
                    val order = it.sortedBy { layer -> layer.index }.map { layer -> layer.id }
                    if (order != layersOrderStateList) {
                        layersOrderStateList.clear()
                        layersOrderStateList.addAll(order)
                    }
                }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val historyCountFlow =
        layerStateFlow.flatMapLatest { layers ->
            layers.firstOrNull { it.selected }?.let { boxesRepository.getLayerHistoryCount(it.id) }
                ?: emptyFlow()
        }

    fun init(projectId: Long) {
        this.projectId.value = projectId
    }

    fun setLayerOnOrOff(
        layerId: Long,
        on: Boolean,
    ) {
        if (!on && (layerStateHandle?.count { it.value } ?: 0) <= 1) return
        layerStateHandle =
            (layerStateHandle?.toMutableMap() ?: mutableMapOf<Long, Boolean>()).apply {
                put(layerId, on)
            }
        if (!on && (layerId == selectedLayerStateHandle || selectedLayerStateHandle == null)) {
            selectedLayerStateHandle = layerStateFlow.value.firstOrNull { it.on }?.id
        }
    }

    fun updateProjectShape(shape: Shape) {
        viewModelScope.launch {
            projectId.value?.let { boxesRepository.updateProjectShape(it, shape) }
        }
    }

    fun updateProjectColor(color: ColorAndShape) {
        viewModelScope.launch {
            projectId.value?.let { boxesRepository.updateProjectColor(it, color) }
        }
    }

    fun updateProjectShowGrid(showGrid: Boolean) {
        viewModelScope.launch {
            projectId.value?.let { boxesRepository.updateProjectShowGrid(it, showGrid) }
        }
    }

    fun updateProjectShowPngBg(showPngBg: Boolean) {
        viewModelScope.launch {
            projectId.value?.let { boxesRepository.updateProjectShowPngBg(it, showPngBg) }
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
                usedColorsHandle?.removeAt(0)
            }
        }
    }

    private var undoJob: Job? = null

    fun onUndo(layerId: Long?) {
        if (undoJob?.isActive == true || layerId == null || _loadingState.value) return
        undoJob =
            viewModelScope.launch(cc.io) {
                _loadingState.value = true
                runCatching {
                    val layer = pixelsFlow.value.data?.getOrPut(layerId) { mutableStateMapOf() }
                    val quads = boxesRepository.getLastHistoryItem(layerId).quadrants
                    quads.forEach {
                        val quad = layer?.getOrPut(it.key) { mutableStateMapOf() }
                        val map =
                            it.value.associate { historyItem ->
                                Point(historyItem.x, historyItem.y) to
                                    historyItem.color?.let { color ->
                                        ColorAndShape(
                                            Color(color),
                                            historyItem.shape ?: Shape.Box,
                                        )
                                    }
                            }
                        quad?.keys?.removeAll(map.keys)
                        quad?.putAll(map.filterNotNullValues())
                    }
                }.onFailure { error ->
                    _uiEventFlow.emit(UiEvent.Error(error.message))
                }
                _loadingState.value = false
            }
    }

    private var exportJob: Job? = null

    fun export(
        project: Project,
        selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
        layers: Collection<LayerState>,
        imageSize: Int,
        exportType: ExportType,
    ) {
        if (exportJob?.isActive == true) return
        exportJob =
            viewModelScope.launch(cc.io) {
                _loadingState.value = true
                runCatching {
                    val path =
                        boxesRepository.export(
                            project,
                            project.name,
                            selections,
                            layers,
                            imageSize,
                            exportType,
                        )
                    path?.let {
                        _uiEventFlow.emit(UiEvent.Export(it, exportType))
                    }
                }.onFailure { error ->
                    _uiEventFlow.emit(UiEvent.Error(error.message))
                    analytics.logError(error)
                }
                _loadingState.value = false
            }
    }

    fun selectLayer(layerId: Long) {
        selectedLayerStateHandle = layerId
    }

    fun addLayer(
        name: String,
        index: Int,
        selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
    ) {
        viewModelScope.launch {
            projectId.value?.let { selectLayer(boxesRepository.addLayer(it, name, index, selections)) }
        }
    }

    fun fill(
        point: Point,
        layerId: Long,
        currentColor: ColorAndShape,
        currentShape: Shape,
        columns: Int,
        rows: Int,
    ) {
        if (fillJob?.isActive == true) return
        fillJob =
            viewModelScope.launch(cc.io) {
                _loadingState.value = true
                withTimeout(timeout = FILL_TIMEOUT) {
                    runCatching {
                        fillInArea(point, layerId, currentColor, currentShape, columns, rows)
                    }.onFailure { error ->
                        _uiEventFlow.emit(UiEvent.Error(error.message))
                    }
                }
                _loadingState.value = false
            }
    }

    fun saveProject(
        project: Project,
        boxes: List<Point>? = null,
        selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
        layers: Collection<LayerState>,
    ) {
        runCatching {
            boxesRepository.save(project, boxes, selections, layers)
        }.onFailure { error ->
            analytics.logError(error)
            _uiEventFlow.tryEmit(UiEvent.Error(error.message))
        }
    }

    fun importImage(
        context: Context,
        layerId: Long,
        columns: Int?,
        rows: Int?,
        uri: Uri,
    ) {
        if (uri.path.isNullOrEmpty() || columns == null || rows == null) return
        viewModelScope.launch(cc.io) {
            _loadingState.value = true
            runCatching {
                val bitmap =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(
                            context.contentResolver,
                            uri,
                        )
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
                    }

                val dimens =
                    when {
                        columns < rows -> {
                            val newRows =
                                ceil((columns.toFloat() / bitmap.width.toFloat()) * bitmap.height.toFloat())
                            columns to newRows.toInt()
                        }

                        rows < columns -> {
                            val newCols =
                                ceil((rows.toFloat() / bitmap.height.toFloat()) * bitmap.width.toFloat())
                            newCols.toInt() to rows
                        }

                        else -> {
                            when (bitmap.height <= bitmap.width) {
                                true -> {
                                    val newRows =
                                        ceil((columns.toFloat() / bitmap.width.toFloat()) * bitmap.height.toFloat())
                                    columns to newRows.toInt()
                                }

                                else -> {
                                    val newCols =
                                        ceil((rows.toFloat() / bitmap.height.toFloat()) * bitmap.width.toFloat())
                                    newCols.toInt() to rows
                                }
                            }
                        }
                    }
                val boxSize =
                    min(
                        bitmap.width.toFloat() / dimens.first.toFloat(),
                        bitmap.height.toFloat() / dimens.second.toFloat(),
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
                        list
                            .associateWith { points[it] }
                            .filterNotNullValues()
                            .let { l.getOrPut(quad) { mutableStateMapOf() }.putAll(it) }
                    }
                }
            }.onFailure { error ->
                _uiEventFlow.emit(UiEvent.Error(error.message))
            }
            _loadingState.value = false
        }
    }

    private var moveJob: Job? = null

    fun move(
        layerId: Long,
        topLeft: Point?,
        bottomRight: Point?,
        direction: Direction,
    ) {
        if (moveJob?.isActive == true || _loadingState.value || topLeft == null || bottomRight == null) return
        moveJob =
            viewModelScope.launch(cc.io) {
                _loadingState.value = true
                runCatching {
                    val points = HashSet<Point>()
                    for (c in min(topLeft.x, bottomRight.x)..max(topLeft.x, bottomRight.x)) {
                        for (r in min(topLeft.y, bottomRight.y)..max(topLeft.y, bottomRight.y)) {
                            points.add(Point(c, r))
                        }
                    }
                    val layer = pixelsFlow.value.data?.get(layerId)
                    val history = mutableMapOf<Point, ColorAndShape?>()

                    val list = layer?.flatMap { it.value.keys }?.filter { points.contains(it) }
                    val adjusted =
                        list
                            ?.map { entry ->
                                entry.adjust(direction) to layer[entry.quadrant]?.get(entry)
                            }?.toMap() ?: emptyMap()
                    val merged = list?.union(adjusted.keys)?.toSet() ?: emptySet()
                    val currentSelection =
                        layer?.let { l -> merged.associateWith { l[it.quadrant]?.get(it) } }
                            ?: emptyMap()
                    history.putAll(currentSelection)

                    adjusted.keys.groupByQuadrant.forEach { (t, u) ->
                        layer?.get(t)?.keys?.removeAll(merged)
                        u
                            .associateWith { adjusted[it] }
                            .filterNotNullValues()
                            .let { layer?.get(t)?.putAll(it) }
                    }
                    addToHistory(UserHistory(layerId, history))
                    _uiEventFlow.emit(UiEvent.MoveSelection(direction))
                }.onFailure { error ->
                    _uiEventFlow.emit(UiEvent.Error(error.message))
                }
                _loadingState.value = false
            }
    }

    private suspend fun fillInArea(
        point: Point,
        layerId: Long,
        color: ColorAndShape,
        shape: Shape,
        columns: Int,
        rows: Int,
    ) {
        val fillMap = HashSet<Point>()
        val iterator = ArrayDeque<Point>().apply { add(point) }
        val newColor = color.copy(shape = shape)
        val currentColor =
            pixelsFlow.value.data
                ?.get(layerId)
                ?.get(point.quadrant)
                ?.get(point)
        if (currentColor == newColor) return
        while (iterator.isNotEmpty()) {
            iterator.firstOrNull()?.let { p ->
                if (p.isNotOutside(columns, rows) &&
                    !fillMap.contains(p) &&
                    pixelsFlow.value.data
                        ?.get(layerId)
                        ?.get(p.quadrant)
                        ?.get(p) == currentColor
                ) {
                    fillMap.add(p)
                    iterator.add(Point(p.x - 1, p.y))
                    iterator.add(Point(p.x + 1, p.y))
                    iterator.add(Point(p.x, p.y - 1))
                    iterator.add(Point(p.x, p.y + 1))
                }
            }
            iterator.removeFirstOrNull()
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

        private val FILL_TIMEOUT = 10.seconds
    }
}
