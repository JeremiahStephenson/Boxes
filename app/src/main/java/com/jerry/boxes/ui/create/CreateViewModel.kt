package com.jerry.boxes.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.boxes.cache.BoxesDao
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CreateViewModel(
    private val boxesDao: BoxesDao
) : ViewModel() {

    private val _uiState = MutableSharedFlow<Resource<Long>>(0, 1, BufferOverflow.DROP_OLDEST)
    val uiState = _uiState.asSharedFlow()

    fun addProject(name: String, columns: Int, rows: Int) {
        viewModelScope.launch(CoroutineExceptionHandler { _, error ->
            _uiState.tryEmit(Resource.error(error))
        }) {
            val createdId = boxesDao.insertProject(Project(name, columns, rows))
            _uiState.tryEmit(Resource.done(createdId))
        }
    }
}