package com.jerry.boxes.ui.layers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.jerry.boxes.cache.BoxesDao
import com.jerry.boxes.cache.BoxesDatabase
import com.jerry.boxes.cache.data.Layer
import com.jerry.boxes.extensions.safeLet
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.util.CoroutineContextProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LayersEditViewModel(
    private val handle: SavedStateHandle,
    private val boxesDao: BoxesDao,
    private val boxesDatabase: BoxesDatabase,
    private val cc: CoroutineContextProvider
) : ViewModel() {

    private val projectId = BoxesMainDestination.argsFrom(handle).projectId

    val projectFlow = boxesDao.getFullProjectFlowById(projectId)
        .filterNotNull()
        .map { it.copy(layers = it.layers.sortedBy { layer -> layer.layer.index }) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )

    fun changeLayerIndex(
        layers: List<Layer>,
        layerId: Long,
        index: Int
    ) {
        viewModelScope.launch(cc.io) {
            boxesDatabase.withTransaction {
                val movedLayer = layers.firstOrNull { it.id == layerId }
                val neighborLayer = layers.firstOrNull { it.index == index }
                safeLet(movedLayer, neighborLayer) { moved, neighbor ->
                    boxesDao.setLayerIndex(neighbor.id, moved.index)
                }
                boxesDao.setLayerIndex(layerId, index)
            }
        }
    }
}