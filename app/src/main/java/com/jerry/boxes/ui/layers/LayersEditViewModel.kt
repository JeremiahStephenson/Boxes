package com.jerry.boxes.ui.layers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.boxes.cache.BoxesDao
import com.jerry.boxes.cache.BoxesDatabase
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

class LayersEditViewModel(
    private val handle: SavedStateHandle,
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
}