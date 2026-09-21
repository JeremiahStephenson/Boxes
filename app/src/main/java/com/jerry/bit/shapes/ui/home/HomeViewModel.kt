package com.jerry.bit.shapes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.bit.shapes.cache.BoxesDao
import com.jerry.bit.shapes.datastore.AppDataStore
import com.jerry.bit.shapes.repository.BoxesRepository
import com.jerry.bit.shapes.util.CoroutineContextProvider
import com.jerry.bit.shapes.util.ProjectSeeder
import com.jerry.bit.shapes.util.Resource
import kotlinx.coroutines.Job
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
    val isImporting: StateFlow<Boolean> field = MutableStateFlow(false)
    val copiedProjectId: StateFlow<Long?> field = MutableStateFlow(null)

    val hasLaunchedBefore = appDataStore.hasLaunchedBefore
    val projectEvents: Flow<ProjectTransferEvent> field =
        MutableSharedFlow<ProjectTransferEvent>(extraBufferCapacity = 1)

    private var importJob: Job? = null

    fun importProject(uri: android.net.Uri) {
        if (importJob?.isActive == true || isImporting.value) return
        importJob =
            viewModelScope.launch(cc.io) {
                isImporting.value = true
                projectEvents.emit(
                    ProjectTransferEvent.Imported(runCatching { boxesRepository.importProject(uri) }),
                )
                isImporting.value = false
            }
    }

    fun copyProject(projectId: Long) {
        copiedProjectId.value = projectId
    }

    fun clearCopiedProject() {
        copiedProjectId.value = null
    }

    private var pasteJob: Job? = null

    fun pasteProject() {
        val projectId = copiedProjectId.value ?: return
        if (pasteJob?.isActive == true || isImporting.value) return
        pasteJob =
            viewModelScope.launch(cc.io) {
                isImporting.value = true
                projectEvents.emit(
                    ProjectTransferEvent.Pasted(runCatching { boxesRepository.duplicateProject(projectId) }),
                )
                isImporting.value = false
            }
    }

    private var deleteJob: Job? = null

    fun deleteProject(projectId: Long) {
        if (deleteJob?.isActive == true) return
        deleteJob =
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
