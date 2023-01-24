package com.jerry.boxes.ui.create

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.repository.BoxesRepository
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.FadeAnimatedVisibility
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.ui.destinations.CreateMainDestination
import com.jerry.boxes.util.ImmutableList
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.min

@Destination
@Composable
fun CreateMain(
    projectId: Long? = null,
    navController: DestinationsNavigator,
    viewModel: CreateViewModel = koinViewModel()
) {
    val projectState by viewModel.projectFlow.collectAsStateWithLifecycle(null)
    DefaultContainer(
        title = when (projectState) {
            null -> stringResource(R.string.add_new_project)
            else -> stringResource(R.string.editing_project, projectState!!.name)
        }
    ) {
        val context = LocalContext.current
        val error = stringResource(R.string.error_save)
        LaunchedEffect(Unit) {
            viewModel.uiState.collectLatest {
                // Should only be error or done
                when (it.isError) {
                    true -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    else -> when (viewModel.isSave) {
                        true -> navController.popBackStack()
                        else -> navController.navigate(BoxesMainDestination(it.data!!, null)) {
                            popUpTo(CreateMainDestination.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
        CreateForm(
            project = projectState,
            onSave = { text, columns, rows ->
                viewModel.saveProject(text, columns, rows)
            }
        )
    }
}

@Composable
private fun CreateForm(
    project: Project? = null,
    onSave: (String, Int, Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        var text by rememberSaveable(project) { mutableStateOf(project?.name.orEmpty()) }
        var columnValue by remember(project) { mutableStateOf(project?.columns ?: 16) }
        var rowValue by remember(project) { mutableStateOf(project?.rows ?: 16) }
        var nameError by remember { mutableStateOf(false) }
        var columnError by remember { mutableStateOf(false) }
        var rowError by remember { mutableStateOf(false) }

        val sizes = remember {
            ImmutableList(
                listOf(
                    16 to 16,
                    10 to 10,
                    25 to 25,
                    32 to 32,
                    64 to 64,
                    10 to 20,
                    20 to 10
                )
            )
        }

        val scope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 86.dp),
            state = scrollState
        ) {
            item {
                NameItem(
                    text = text,
                    nameError = nameError,
                    onTextChange = {
                        text = it
                    },
                    onNameError = {
                        nameError = true
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProjectNumberPicker(
                        modifier = Modifier.padding(end = 8.dp),
                        title = stringResource(R.string.columns),
                        value = columnValue,
                        error = columnError,
                        onError = { columnError = it },
                        onValueChange = { columnValue = it }
                    )
                    ProjectNumberPicker(
                        modifier = Modifier.padding(start = 8.dp),
                        title = stringResource(R.string.rows),
                        value = rowValue,
                        error = rowError,
                        onError = { rowError = it },
                        onValueChange = { rowValue = it }
                    )
                }
            }

            items(sizes.items) { value ->
                LayerItem(
                    value = value,
                    columnValue = columnValue,
                    rowValue = rowValue,
                    onSizeChange = { columns, rows ->
                        columnValue = columns
                        rowValue = rows
                    }
                )
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            onClick = {
                when (text.trim().isEmpty()) {
                    true -> {
                        nameError = true
                        scope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                    }
                    else -> if (columnValue in 1..BoxesRepository.MAX_SIDE_SIZE && rowValue in 1..BoxesRepository.MAX_SIDE_SIZE) {
                        onSave(text.trim(), columnValue, rowValue)
                    }
                }
            }
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
private fun LayerItem(
    value: Pair<Int, Int>,
    columnValue: Int,
    rowValue: Int,
    onSizeChange: (Int, Int) -> Unit
) {
    val color = MaterialTheme.colorScheme.onSurface
    val density = LocalDensity.current
    val strokeWidth = with(density) { 0.5.dp.toPx() }
    val (columns, rows) = value
    val matchesState by remember(
        columnValue,
        rowValue
    ) { derivedStateOf { columnValue == columns && rowValue == rows } }
    Column(
        modifier = Modifier
            .padding(top = 32.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(
                when (matchesState) {
                    true -> MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4F)
                    else -> Color.Transparent
                }
            )
            .clickable {
                onSizeChange(columns, rows)
            }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier.size(200.dp)
        ) {
            val boxSize = min(size.width / columns, size.height / rows)
            val offsetY = (size.height - (boxSize * rows)) / 2
            val offsetX = (size.width - (boxSize * columns)) / 2
            for (i in 0..rows) {
                drawLine(
                    strokeWidth = strokeWidth,
                    color = color,
                    start = Offset(offsetX, offsetY + (i * boxSize)),
                    end = Offset(size.width - offsetX, offsetY + (i * boxSize))
                )
            }
            for (i in 0..columns) {
                drawLine(
                    strokeWidth = strokeWidth,
                    color = color,
                    start = Offset(offsetX + (i * boxSize), offsetY),
                    end = Offset(offsetX + (i * boxSize), size.height - offsetY)
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "$columns x $rows"
        )
    }
}

@Composable
private fun NameItem(
    text: String,
    nameError: Boolean,
    onTextChange: (String) -> Unit,
    onNameError: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = buildAnnotatedString {
                append(stringResource(R.string.name))
                append(":")
            },
            style = MaterialTheme.typography.titleLarge
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            isError = nameError,
            onValueChange = {
                if (it.isNotEmpty()) {
                    onNameError()
                }
                if (it.length <= 50) {
                    onTextChange(it)
                }
            }
        )
        FadeAnimatedVisibility(visible = nameError) {
            Text(
                text = stringResource(R.string.name_required),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun RowScope.ProjectNumberPicker(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    error: Boolean,
    onError: (Boolean) -> Unit,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = modifier.weight(1F)) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = buildAnnotatedString {
                append(title)
                append(":")
            },
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(R.string.project_range, 1, BoxesRepository.MAX_SIDE_SIZE),
            style = MaterialTheme.typography.bodySmall
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            value = value.toString(),
            isError = error,
            onValueChange = {
                val num = it.toIntOrNull() ?: 0
                val newValue = when (value == 0 && num > 0) {
                    true -> it.trimEnd('0').toIntOrNull() ?: 0
                    else -> num
                }
                onError(newValue < 1 || newValue > BoxesRepository.MAX_SIDE_SIZE)
                onValueChange(newValue)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        FadeAnimatedVisibility(visible = error) {
            Text(
                text = stringResource(
                    when (value > 200) {
                        true -> R.string.value_too_high
                        else -> R.string.value_too_low
                    }
                ),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
