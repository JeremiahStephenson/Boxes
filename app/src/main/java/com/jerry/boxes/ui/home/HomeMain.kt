package com.jerry.boxes.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.ui.common.AreYouSureDialog
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.unboundClickable
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.ui.destinations.CreateMainDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeMain(
    navController: DestinationsNavigator,
    viewModel: HomeViewModel = koinViewModel()
) {
    var editMode by rememberSaveable { mutableStateOf(false) }
    DefaultContainer(
        title = stringResource(R.string.app_name),
        fabListener = {
            navController.navigate(CreateMainDestination())
        },
        appBarActions = {
            EditMenu(editMode) {
                editMode = !editMode
            }
        }
    ) {
        val items = viewModel.projectsFlow.collectAsLazyPagingItems()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 84.dp)
        ) {
            itemsIndexed(items) { index, item ->
                if (index > 0) {
                    Divider()
                }
                item?.let { project ->
                    ProjectItem(
                        item = project,
                        editMode = editMode,
                        onGoToProject = {
                            editMode = false
                            navController.navigate(
                                BoxesMainDestination(it)
                            )
                        },
                        onDeleteProject = {
                            viewModel.deleteProject(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectItem(
    item: Project,
    editMode: Boolean,
    onGoToProject: (Long) -> Unit,
    onDeleteProject: (Long) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1F)
                .clickable {
                    onGoToProject(item.id)
                }
                .padding(horizontal = 16.dp, vertical = 24.dp),
            style = MaterialTheme.typography.titleLarge,
            text = item.name
        )

        var showConfirmationDialog by remember { mutableStateOf(false) }
        if (showConfirmationDialog) {
            AreYouSureDialog(
                title = stringResource(R.string.are_you_sure_project, item.name),
                dismiss = { showConfirmationDialog = false },
                onDelete = { onDeleteProject(item.id) }
            )
        }
        if (editMode) {
            Icon(
                modifier = Modifier
                    .unboundClickable {
                        showConfirmationDialog = true
                    }
                    .padding(16.dp),
                painter = painterResource(R.drawable.ic_baseline_delete_24),
                contentDescription = null // todo
            )
        }
    }
}

@Composable
private fun EditMenu(
    editMode: Boolean,
    onEditClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .unboundClickable {
                onEditClick()
            }
            .padding(8.dp),
        text = stringResource(
            when (editMode) {
                true -> R.string.done
                else -> R.string.edit
            }
        )
    )
}