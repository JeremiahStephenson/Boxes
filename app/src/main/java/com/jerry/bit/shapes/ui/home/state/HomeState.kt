package com.jerry.bit.shapes.ui.home.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import com.jerry.bit.shapes.cache.data.Project
import com.jerry.bit.shapes.util.Resource

@Stable
class HomeState(
    projectsState: State<Resource<List<Project>>?>,
    importingState: State<Boolean>,
    copiedProjectIdState: State<Long?>,
) {
    val projects by projectsState
    val copiedProjectId by copiedProjectIdState
    private val isImporting by importingState

    val isLoading get() = (projects?.isLoading ?: false) || isImporting
}
