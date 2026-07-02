package com.jerry.shapes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.shapes.cache.BoxesDao
import com.jerry.shapes.datastore.AppDataStore
import com.jerry.shapes.util.Resource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val boxesDao: BoxesDao,
    private val appDataStore: AppDataStore,
) : ViewModel() {
    val projectsFlow =
        boxesDao
            .findAllProjects()
            .map { Resource.done(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, Resource.loading())

    val showOnboarding = appDataStore.showOnboarding

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            boxesDao.deleteProject(projectId)
        }
    }

    fun setShowOnboarding(show: Boolean) {
        viewModelScope.launch {
            appDataStore.setShowOnboarding(show)
        }
    }
}
