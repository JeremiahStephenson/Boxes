package com.jerry.shapes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.shapes.cache.BoxesDao
import com.jerry.shapes.datastore.AppDataStore
import kotlinx.coroutines.launch

class HomeViewModel(
    private val boxesDao: BoxesDao,
    private val appDataStore: AppDataStore,
) : ViewModel() {
    val projectsFlow = boxesDao.findAllProjects()

    val hasLaunchedBefore = appDataStore.hasLaunchedBefore

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            boxesDao.deleteProject(projectId)
        }
    }

    fun setHasLaunched() {
        viewModelScope.launch {
            appDataStore.setHasLaunched()
        }
    }
}
