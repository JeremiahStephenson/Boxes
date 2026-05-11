package com.jerry.shapes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.shapes.repository.BoxesRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LaunchViewModel(
    private val boxesRepository: BoxesRepository,
) : ViewModel() {
    private val _initFinished = MutableSharedFlow<Unit>(0, 1, BufferOverflow.DROP_OLDEST)
    val initFinished = _initFinished.asSharedFlow()

    fun cleanDb() {
        viewModelScope.launch {
            boxesRepository.deleteInvalidHistoryItems()
            _initFinished.emit(Unit)
        }
    }
}
