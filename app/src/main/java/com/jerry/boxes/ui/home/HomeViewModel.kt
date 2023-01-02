package com.jerry.boxes.ui.home

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.jerry.boxes.cache.BoxesDao

class HomeViewModel(
    private val boxesDao: BoxesDao
) : ViewModel() {

    val projectsFlow = Pager(PagingConfig(40)) {
        boxesDao.findAllProjects()
    }.flow
}