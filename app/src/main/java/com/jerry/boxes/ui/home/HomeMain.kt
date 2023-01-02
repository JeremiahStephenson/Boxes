package com.jerry.boxes.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.jerry.boxes.R
import com.jerry.boxes.ui.common.DefaultContainer
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
    DefaultContainer(
        title = stringResource(R.string.app_name),
        fabListener = {
            navController.navigate(CreateMainDestination)
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
                                    BoxesMainDestination(project.id)
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