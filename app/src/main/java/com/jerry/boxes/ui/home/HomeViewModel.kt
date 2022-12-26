package com.jerry.boxes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.jerry.boxes.cache.BoxesDao
import com.jerry.boxes.cache.data.Project
import kotlinx.coroutines.launch

class HomeViewModel(
    private val boxesDao: BoxesDao
) : ViewModel() {

    val projectsFlow = Pager(PagingConfig(40)) {
        boxesDao.findAllProjects()
    }.flow

    fun addProject(name: String) {
        viewModelScope.launch {
            boxesDao.insertProject(Project(name, 12, 20))
        }
    }
}