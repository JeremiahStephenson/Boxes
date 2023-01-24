package com.jerry.shapes.ui.layers

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.jerry.shapes.cache.BoxesDao
import com.jerry.shapes.cache.BoxesDatabase
import com.jerry.shapes.ui.boxes.generateBitmap
import com.jerry.shapes.ui.boxes.generateSelections
import com.jerry.shapes.ui.destinations.BoxesMainDestination
import com.jerry.shapes.ui.layers.data.LayerEditUi
import com.jerry.shapes.ui.layers.data.ProjectUi
import com.jerry.shapes.util.CoroutineContextProvider
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

    private val cachedBitmaps = HashMap<Long, Bitmap>()

    private val projectId = BoxesMainDestination.argsFrom(handle).projectId

    val projectFlow = boxesDao.getFullProjectFlowById(projectId)
        .filterNotNull()
        .map {
            val layers = it.layers.sortedByDescending { layer -> layer.layer.index }.map { layer ->
                LayerEditUi(
                    layer.layer.id,
                    layer.layer.index,
                    layer.layer.name,
                    cachedBitmaps.getOrPut(
                        layer.layer.id
                    ) {
                        generateBitmap(
                            it.project.rows,
                            it.project.columns,
                            200,
                            layer.layer.id,
                            generateSelections(layer.pixels)
                        )
                    }
                )
            }
            ProjectUi(it.project.id, it.project.name, layers)
            // it.copy(layers = it.layers.sortedByDescending { layer -> layer.layer.index })
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )

    fun setLayerIndicies(
        indicies: List<Pair<Long, Int>>
    ) {
        viewModelScope.launch(cc.io) {
            boxesDatabase.withTransaction {
                indicies.forEach {
                    boxesDao.setLayerIndex(it.first, it.second)
                }
            }
        }
    }

    fun deleteLayer(
        layers: List<LayerEditUi>,
        layerId: Long
    ) {
        viewModelScope.launch(cc.io) {
            boxesDatabase.withTransaction {
                val deletingLayer = layers.firstOrNull { it.id == layerId }
                deletingLayer?.let {
                    for (i in it.index + 1 until layers.size) {
                        val layer = layers.firstOrNull { it.index == i }
                        layer?.let {
                            boxesDao.setLayerIndex(it.id, i - 1)
                        }
                    }
                }
                boxesDao.deleteLayer(layerId)
            }
        }
    }

    fun setLayerName(layerId: Long, name: String) {
        viewModelScope.launch(cc.io) {
            boxesDatabase.withTransaction {
                boxesDao.setLayerName(layerId, name)
            }
        }
    }
}
