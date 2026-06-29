package com.jerry.shapes.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.shapes.R
import com.jerry.shapes.cache.data.Project
import com.jerry.shapes.extensions.readableDateAndTime
import com.jerry.shapes.navigation.Navigator
import com.jerry.shapes.ui.boxes.BoxesNavKey
import com.jerry.shapes.ui.common.AreYouSureDialog
import com.jerry.shapes.ui.common.DefaultContainer
import com.jerry.shapes.ui.common.FadeAnimatedVisibility
import com.jerry.shapes.ui.common.ProjectImage
import com.jerry.shapes.ui.common.unboundClickable
import com.jerry.shapes.ui.create.CreateNavKey
import com.jerry.shapes.util.thumbnailLocation
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import java.io.File
import kotlin.time.Instant

@Composable
fun HomeMain(
    navigator: Navigator,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val itemsFlow by viewModel.projectsFlow.collectAsStateWithLifecycle(null)
    val isLoading by remember { derivedStateOf { itemsFlow?.isLoading ?: false } }
    var editMode by rememberSaveable { mutableStateOf(false) }
    DefaultContainer(
        title = stringResource(R.string.app_name),
        fabListener = {
            navigator.navigate(CreateNavKey())
        },
        appBarActions = {
            val emptyList by remember {
                derivedStateOf {
                    itemsFlow?.isSuccessful == true && (itemsFlow?.data?.isEmpty() ?: true)
                }
            }
            if (!emptyList) {
                EditMenu(editMode) {
                    editMode = !editMode
                }
            }
        },
    ) {
        // If the user is launching for the first time then go
        // ahead and send to the create project screen
        LaunchedEffect(Unit) {
            viewModel.hasLaunchedBefore.collectLatest {
                if (it == null || !it) {
                    navigator.navigate(CreateNavKey())
                    viewModel.setHasLaunched()
                }
            }
        }

        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(150.dp),
            contentPadding =
                PaddingValues(
                    bottom = 84.dp,
                    top = 16.dp,
                    start = 10.dp,
                    end = 10.dp,
                ),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            itemsIndexed(itemsFlow?.data ?: emptyList()) { _, item ->
                ProjectItem(
                    item = item,
                    editMode = editMode,
                    onGoToProject = {
                        editMode = false
                        navigator.navigate(
                            BoxesNavKey(projectId = it, projectName = item.name),
                        )
                    },
                    onDeleteProject = {
                        viewModel.deleteProject(it)
                    },
                )
            }
        }

        val areItemsEmpty by remember {
            derivedStateOf {
                itemsFlow?.isSuccessful == true && itemsFlow?.data != null && itemsFlow!!.data!!.isEmpty()
            }
        }
        if (areItemsEmpty) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier =
                        Modifier
                            .padding(32.dp),
                    text = stringResource(R.string.empty_list_title),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
                Image(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    painter = painterResource(R.drawable.puppy),
                    contentDescription = null,
                )
            }
        }

        FadeAnimatedVisibility(
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
            visible = isLoading,
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ProjectItem(
    item: Project,
    editMode: Boolean,
    onGoToProject: (Long) -> Unit,
    onDeleteProject: (Long) -> Unit,
) {
    Card {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        onGoToProject(item.id)
                    },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var showConfirmationDialog by remember { mutableStateOf(false) }
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                ProjectImageItem(item.id, item.timestamp)
                if (editMode) {
                    Icon(
                        modifier =
                            Modifier
                                .unboundClickable {
                                    showConfirmationDialog = true
                                }.padding(16.dp),
                        painter = painterResource(R.drawable.ic_delete_24),
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null, // todo
                    )
                }
            }

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 24.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                text = item.name,
            )
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                text =
                    stringResource(
                        R.string.last_edited,
                        Instant.fromEpochMilliseconds(item.timestamp).readableDateAndTime,
                    ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (showConfirmationDialog) {
                AreYouSureDialog(
                    title = stringResource(R.string.are_you_sure_project, item.name),
                    dismiss = { showConfirmationDialog = false },
                    onDelete = { onDeleteProject(item.id) },
                )
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.ProjectImageItem(
    projectId: Long,
    memoryKey: Long,
) {
    val context = LocalContext.current
    val imagePath =
        remember(projectId) { context.thumbnailLocation.path + File.separator.toString() + "$projectId.png" }
    ProjectImage(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(this.maxWidth),
        imagePath = imagePath,
        memoryKey = memoryKey.toString(),
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun EditMenu(
    editMode: Boolean,
    onEditClick: () -> Unit,
) {
    Text(
        modifier =
            Modifier
                .unboundClickable {
                    onEditClick()
                }.padding(8.dp),
        text =
            stringResource(
                when (editMode) {
                    true -> R.string.done
                    else -> R.string.edit
                },
            ),
    )
}
