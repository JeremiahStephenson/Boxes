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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant

class BoxesViewModel(
    handle: SavedStateHandle,
    private val boxesDao: BoxesDao,
    private val boxesDatabase: BoxesDatabase
) : ViewModel() {

    private val projectId = BoxesMainDestination.argsFrom(handle).projectId

    val projectFlow = boxesDao.getFullProjectFlowById(projectId)
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )

    fun save(
        boxes: List<Point>? = null,
        selections: Map<Point, SerializableColor?>
    ) {
        viewModelScope.launch {
            boxesDatabase.withTransaction {
                val now = Instant.now().toEpochMilli()
                val list =
                    selections.filterKeys { boxes?.contains(it) ?: true }.filterValues { it != null }.map {
                        Pixel(
                            projectId,
                            it.key.x,
                            it.key.y,
                            it.value!!.hue,
                            it.value!!.saturation,
                            it.value!!.value,
                            it.value!!.alpha,
                            now
                        )
                    }
                boxesDao.insertAllPixels(list)
                boxesDao.deletePixelsFromProject(projectId, now)
            }
        }
    }
}