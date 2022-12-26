package com.jerry.boxes.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.jerry.boxes.R
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.destinations.BoxesMainDestination
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
    var showAddDialog by remember { mutableStateOf(false) }
    AddDialog(
        show = showAddDialog,
        dismiss = { showAddDialog = false },
        addProject = { viewModel.addProject(it) }
    )

    DefaultContainer(
        title = stringResource(R.string.app_name),
        fabListener = {
            showAddDialog = true
        }
    ) {
        val items = viewModel.projectsFlow.collectAsLazyPagingItems()
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items) { index, item ->
                if (index > 0) {
                    Divider()
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            item?.let { project ->
                                navController.navigate(
                                    BoxesMainDestination(project)
                                )
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    style = MaterialTheme.typography.titleLarge,
                    text = item?.name.orEmpty()
                )
            }
        }
    }
}

@Composable
private fun AddDialog(
    show: Boolean,
    dismiss: () -> Unit,
    addProject: (String) -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = dismiss) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.set_name),
                    style = MaterialTheme.typography.titleLarge
                )
                var text by rememberSaveable { mutableStateOf("") }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    value = text,
                    onValueChange = {
                        text = it
                    })

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    onClick = {
                        addProject(text)
                        dismiss()
                    }) {
                    Text(text = stringResource(R.string.save))
                }

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    onClick = dismiss
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        }
    }
}