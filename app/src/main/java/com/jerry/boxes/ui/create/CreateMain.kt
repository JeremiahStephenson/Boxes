package com.jerry.boxes.ui.create

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chargemap.compose.numberpicker.NumberPicker
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.common.FadeAnimatedVisibility
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.ui.destinations.CreateMainDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

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
            })
    }
}

@Composable
private fun CreateForm(
    project: Project? = null,
    onSave: (String, Int, Int) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = buildAnnotatedString {
                append(stringResource(R.string.name))
                append(":")
            },
            style = MaterialTheme.typography.titleLarge
        )
        var text by rememberSaveable(project) { mutableStateOf(project?.name.orEmpty()) }
        var columnValue by remember(project) { mutableStateOf(project?.columns ?: 16) }
        var rowValue by remember(project) { mutableStateOf(project?.rows ?: 16) }
        var nameError by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                isError = nameError,
                onValueChange = {
                    if (it.isNotEmpty()) {
                        nameError = false
                    }
                    if (it.length <= 50) {
                        text = it
                    }
                })
            FadeAnimatedVisibility(visible = nameError) {
                Text(
                    text = stringResource(R.string.name_required),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            ProjectNumberPicker(
                title = stringResource(R.string.columns),
                value = columnValue,
                onValueChange = { columnValue = it }
            )
            ProjectNumberPicker(
                title = stringResource(R.string.rows),
                value = rowValue,
                onValueChange = { rowValue = it }
            )
        }

        Spacer(modifier = Modifier.weight(1F))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            onClick = {
                when (text.trim().isEmpty()) {
                    true -> nameError = true
                    else -> onSave(text.trim(), columnValue, rowValue)
                }
            }) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
private fun RowScope.ProjectNumberPicker(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.weight(1F)) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = buildAnnotatedString {
                append(title)
                append(":")
            },
            style = MaterialTheme.typography.titleLarge
        )
        NumberPicker(
            value = value,
            range = 1..200,
            onValueChange = {
                onValueChange(it)
            },
            dividersColor = MaterialTheme.colorScheme.primary,
            textStyle = TextStyle.Default.copy(MaterialTheme.colorScheme.onBackground)
        )
    }
}