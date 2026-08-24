package com.jerry.bit.shapes.ui.create

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.bit.shapes.cache.BoxesDao
import com.jerry.bit.shapes.cache.data.Layer
import com.jerry.bit.shapes.cache.data.Project
import com.jerry.bit.shapes.ui.shapes.Shape
import com.jerry.bit.shapes.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.time.Clock

class CreateViewModel(
    private val boxesDao: BoxesDao,
) : ViewModel() {
    private val _uiState = MutableSharedFlow<Resource<Long>>(0, 1, BufferOverflow.DROP_OLDEST)
    val uiState = _uiState.asSharedFlow()

    private val projectId = MutableStateFlow<Long?>(null)

    val isSave get() = projectId.value != null

    val projectFlow =
        projectId.flatMapLatest { projectId ->
            boxesDao.takeIf { projectId != null }?.getProjectFlowById(projectId!!) ?: emptyFlow()
        }

    fun init(projectId: Long?) {
        this.projectId.value = projectId
    }

    fun saveProject(
        name: String,
        columns: Int,
        rows: Int,
    ) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, error ->
                _uiState.tryEmit(Resource.error(error))
            },
        ) {
            val id =
                when (projectId.value) {
                    null -> {
                        val projectId =
                            boxesDao.insertProject(
                                Project(
                                    name = name,
                                    columns = columns,
                                    rows = rows,
                                    currentColor = Color.Green.toArgb(),
                                    currentShape = Shape.Box,
                                    showGrid = true,
                                    showPngBg = false,
                                    timestamp = Clock.System.now().toEpochMilliseconds(),
                                ),
                            )
                        boxesDao.insertLayer(Layer(projectId = projectId, index = 0, name = "Layer 1", on = true))
                        projectId
                    }
                    else -> {
                        boxesDao.updateProject(name, columns, rows, projectId.value!!)
                        projectId.value
                    }
                }
            _uiState.tryEmit(Resource.done(id))
        }
    }
}
