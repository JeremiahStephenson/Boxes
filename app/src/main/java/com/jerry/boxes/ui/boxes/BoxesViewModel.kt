package com.jerry.boxes.ui.boxes

import android.graphics.Point
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.jerry.boxes.cache.BoxesDao
import com.jerry.boxes.cache.BoxesDatabase
import com.jerry.boxes.cache.data.Pixel
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.util.SavedHandle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BoxesViewModel(
    handle: SavedStateHandle,
    private val boxesDao: BoxesDao,
    private val boxesDatabase: BoxesDatabase
) : ViewModel() {

    private val projectId = BoxesMainDestination.argsFrom(handle).projectId

    val projectFlow = boxesDao.getProjectFlowById(projectId)
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )

    var boxes by SavedHandle<HashMap<Point, SerializableColor?>?>(handle, "TESTING", null)

    fun save(map: Map<Point, SerializableColor?>) {
        viewModelScope.launch {
            boxesDatabase.withTransaction {
                boxesDao.deletePixelsFromProject(projectId)
                val list = map.filter { it.value != null }.map { Pixel(
                    projectId,
                    it.key.x,
                    it.key.y,
                    it.value!!.hue,
                    it.value!!.saturation,
                    it.value!!.value,
                    it.value!!.alpha
                ) }
                boxesDao.insertAllPixels(list)
            }
        }
    }
}