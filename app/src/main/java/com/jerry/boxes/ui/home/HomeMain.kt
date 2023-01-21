package com.jerry.boxes.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.ui.common.AreYouSureDialog
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.unboundClickable
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.ui.destinations.CreateMainDestination
import com.jerry.boxes.util.thumbnailLocation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun HomeMain(
    navController: DestinationsNavigator,
    viewModel: HomeViewModel = koinViewModel()
) {
    val items by viewModel.projectsFlow.collectAsStateWithLifecycle(emptyList())
    var editMode by rememberSaveable { mutableStateOf(false) }
    DefaultContainer(
        title = stringResource(R.string.app_name),
        fabListener = {
            navController.navigate(CreateMainDestination())
        },
        appBarActions = {
            val emptyList by remember { derivedStateOf { items.isEmpty() } }
            if (!emptyList) {
                EditMenu(editMode) {
                    editMode = !editMode
                }
            }
        }
    ) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(125.dp),
            contentPadding = PaddingValues(
                bottom = 84.dp,
                top = 16.dp,
                start = 8.dp,
                end = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(items) { _, item ->
                ProjectItem(
                    item = item,
                    editMode = editMode,
                    onGoToProject = {
                        editMode = false
                        navController.navigate(
                            BoxesMainDestination(it, item.name)
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

@Composable
private fun ProjectItem(
    item: Project,
    editMode: Boolean,
    onGoToProject: (Long) -> Unit,
    onDeleteProject: (Long) -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onGoToProject(item.id)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var showConfirmationDialog by remember { mutableStateOf(false) }
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                ProjectImage(item.id, item.timestamp)
                if (editMode) {
                    Icon(
                        modifier = Modifier
                            .unboundClickable {
                                showConfirmationDialog = true
                            }
                            .padding(16.dp),
                        painter = painterResource(R.drawable.ic_delete_24),
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null // todo
                    )
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                style = MaterialTheme.typography.titleLarge,
                text = item.name
            )
            if (showConfirmationDialog) {
                AreYouSureDialog(
                    title = stringResource(R.string.are_you_sure_project, item.name),
                    dismiss = { showConfirmationDialog = false },
                    onDelete = { onDeleteProject(item.id) }
                )
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.ProjectImage(projectId: Long, memoryKey: Long) {
    val context = LocalContext.current
    val imagePath =
        remember(projectId) { context.thumbnailLocation.path + File.separator.toString() + "$projectId.png" }
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(this.maxWidth),
        model = ImageRequest.Builder(context)
            .diskCacheKey(memoryKey.toString())
            .data(imagePath)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Fit
    )
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
