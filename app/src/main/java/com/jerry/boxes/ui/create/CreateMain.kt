package com.jerry.boxes.ui.create

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import com.chargemap.compose.numberpicker.NumberPicker
import com.jerry.boxes.R
import com.jerry.boxes.ui.common.DefaultContainer
import com.jerry.boxes.ui.destinations.BoxesMainDestination
import com.jerry.boxes.ui.destinations.CreateMainDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun CreateMain(
    navController: DestinationsNavigator,
    viewModel: CreateViewModel = koinViewModel()
) {
    DefaultContainer(title = stringResource(R.string.add_new_project)) {
        val context = LocalContext.current
        val error = stringResource(R.string.error_save)
        LaunchedEffect(Unit) {
            viewModel.uiState.collectLatest {
                // Should only be error or done
                when (it.isError) {
                    true -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    else -> navController.navigate(BoxesMainDestination(it.data!!)) {
                        popUpTo(CreateMainDestination.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
        CreateForm(onSave = { text, columns, rows ->
            viewModel.addProject(text, columns, rows)
        })
    }
}

@Composable
private fun CreateForm(onSave: (String, Int, Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
        var text by rememberSaveable { mutableStateOf("") }
        var columnValue by remember { mutableStateOf(0) }
        var rowValue by remember { mutableStateOf(0) }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            value = text,
            onValueChange = {
                text = it
            })

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
                onSave(text, columnValue, rowValue)
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
            range = 1..500,
            onValueChange = {
                onValueChange(it)
            },
            dividersColor = MaterialTheme.colorScheme.primary,
            textStyle = TextStyle.Default.copy(MaterialTheme.colorScheme.onBackground)
        )
    }
}