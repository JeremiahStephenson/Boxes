package com.jerry.boxes.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.ui.common.AreYouSureDialog
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.pngBackground
import com.jerry.boxes.ui.common.unboundClickable
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.ui.destinations.CreateMainDestination
import com.jerry.boxes.util.thumbnailLocation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import java.io.File

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
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onGoToProject(item.id)
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current
        val imagePath = remember { context.thumbnailLocation.path + File.separator.toString() + "${item.id}.png" }
        val pngSize = with(LocalDensity.current) { 10.dp.toPx() }
        AsyncImage(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .size(100.dp)
                .pngBackground(true, pngSize),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imagePath)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )

        Text(
            modifier = Modifier
                .weight(1F)
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
                painter = painterResource(R.drawable.ic_delete_24),
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