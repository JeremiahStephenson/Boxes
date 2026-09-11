package com.jerry.bit.shapes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.bit.shapes.cache.BoxesDao
import com.jerry.bit.shapes.datastore.AppDataStore
import com.jerry.bit.shapes.repository.BoxesRepository
import com.jerry.bit.shapes.util.CoroutineContextProvider
import com.jerry.bit.shapes.util.ProjectSeeder
import com.jerry.bit.shapes.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val boxesDao: BoxesDao,
    private val appDataStore: AppDataStore,
    private val boxesRepository: BoxesRepository,
    private val cc: CoroutineContextProvider,
) : ViewModel() {
    private val projectSeeder = ProjectSeeder(boxesDao)

    val projectsFlow =
        boxesDao
            .findAllProjects()
            .map { Resource.done(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, Resource.loading())

    val hasLaunchedBefore = appDataStore.hasLaunchedBefore

    val isImporting: StateFlow<Boolean> field = MutableStateFlow(false)
    val importEvents: Flow<Result<Long>> field = MutableSharedFlow<Result<Long>>(extraBufferCapacity = 1)
    val copiedProjectId: StateFlow<Long?> field = MutableStateFlow(null)
    val pasteEvents: Flow<Result<Long>> field = MutableSharedFlow<Result<Long>>(extraBufferCapacity = 1)

    fun importProject(uri: android.net.Uri) {
        if (isImporting.value) return
        viewModelScope.launch(cc.io) {
            isImporting.value = true
            importEvents.emit(runCatching { boxesRepository.importProject(uri) })
            isImporting.value = false
        }
    }

    fun copyProject(projectId: Long) {
        copiedProjectId.value = projectId
    }

    fun clearCopiedProject() {
        copiedProjectId.value = null
    }

    fun pasteProject() {
        val projectId = copiedProjectId.value ?: return
        if (isImporting.value) return
        viewModelScope.launch(cc.io) {
            isImporting.value = true
            pasteEvents.emit(runCatching { boxesRepository.duplicateProject(projectId) })
            isImporting.value = false
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            boxesDao.deleteProject(projectId)
            if (copiedProjectId.value == projectId) {
                copiedProjectId.value = null
            }
        }
    }

    fun setHasLaunched() {
        viewModelScope.launch {
            appDataStore.setHasLaunched()
        }
    }

    fun seedProjects() {
        viewModelScope.launch {
            projectSeeder.seedProjects()
        }
    }
}
