package com.jerry.boxes.ui.create

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.boxes.cache.BoxesDao
import com.jerry.boxes.cache.data.Layer
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.ui.boxes.shapes.Shape
import com.jerry.boxes.ui.destinations.CreateMainDestination
import com.jerry.boxes.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class CreateViewModel(
    handle: SavedStateHandle,
    private val boxesDao: BoxesDao
) : ViewModel() {

    private val _uiState = MutableSharedFlow<Resource<Long>>(0, 1, BufferOverflow.DROP_OLDEST)
    val uiState = _uiState.asSharedFlow()

    private val projectId = CreateMainDestination.argsFrom(handle).projectId

    val isSave get() = projectId != null

    val projectFlow =
        boxesDao.takeIf { projectId != null }?.getProjectFlowById(projectId!!) ?: emptyFlow()

    fun saveProject(name: String, columns: Int, rows: Int) {
        viewModelScope.launch(CoroutineExceptionHandler { _, error ->
            _uiState.tryEmit(Resource.error(error))
        }) {
            val id = when (projectId) {
                null -> {
                    val projectId = boxesDao.insertProject(
                        Project(
                            name,
                            columns,
                            rows,
                            Color.Green.toArgb(),
                            Shape.Box
                        )
                    )
                    boxesDao.insertLayer(Layer(projectId, 0, "Layer 1", true))
                    projectId
                }
                else -> {
                    boxesDao.updateProject(name, columns, rows, projectId)
                    projectId
                }
            }
            _uiState.tryEmit(Resource.done(id))
        }
    }
}