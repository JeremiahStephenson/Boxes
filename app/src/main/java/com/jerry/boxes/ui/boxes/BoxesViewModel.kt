package com.jerry.boxes.ui.boxes

import android.graphics.Point
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.jerry.boxes.cache.BoxesDao
import com.jerry.boxes.cache.BoxesDatabase
import com.jerry.boxes.cache.data.Layer
import com.jerry.boxes.cache.data.Pixel
import com.jerry.boxes.ui.boxes.history.History
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

    private val layerState = handle.getStateFlow<MutableMap<Long, Boolean>?>(
        LAYER_LIST_STATE,
        null
    )

    private val historyMutex = Mutex()
    private var historyStateHandle
            by SavedHandle<MutableList<History>>(
                handle,
                HISTORY_STATE,
                mutableListOf()
            )

    private val projectId = BoxesMainDestination.argsFrom(handle).projectId

    val projectFlow = boxesDao.getFullProjectFlowById(projectId)
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )

    private val layerFlow = projectFlow.map { it?.layers?.map { it.layer } }
    val layerStateFlow = combine(layerState, layerFlow) { state, layers ->
        if (layerStateHandle == null) {
            layerStateHandle =
                layers?.filter { it.on }?.associate { it.id to it.on }?.toMutableMap()
        }
        layers?.map {
            it.copy(on = state?.getOrDefault(it.id, it.on) == true).apply { id = it.id }
        } ?: emptyList()
    }

    fun setLayerOnOrOff(layerId: Long, on: Boolean) {
        layerStateHandle = (layerStateHandle?.toMutableMap() ?: mutableMapOf()).apply {
            put(layerId, on)
        }
    }

    suspend fun addToHistory(historyItem: History) {
        historyMutex.withLock {
            historyStateHandle?.add(historyItem)
            if ((historyStateHandle?.size ?: 0) > 20) {
                historyStateHandle?.removeAt(0)
            }
        }
    }

    suspend fun undoSelection(): History? {
        return historyMutex.withLock {
            historyStateHandle?.lastOrNull()?.also {
                historyStateHandle?.removeLast()
            }
        }
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
        layers: List<Pair<Long, Boolean>>
    ) {
        viewModelScope.launch {
            updateDatabase {
                saveProject(boxes, selections)
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
        selections: Map<Point, Map<Long, SerializableColor?>?>
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
    }

    companion object {
        private const val LAYER_LIST_STATE = "LAYER_LIST_STATE"
        private const val HISTORY_STATE = "HISTORY_STATE"
    }
}