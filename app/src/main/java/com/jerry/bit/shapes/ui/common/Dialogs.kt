package com.jerry.bit.shapes.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jerry.bit.shapes.R

@Composable
fun SetNameDialog(
    existingName: String,
    dismiss: () -> Unit,
    hint: String? = null,
    onName: (String) -> Unit,
) {
    Dialog(onDismissRequest = dismiss) {
        val scrollState = rememberScrollState()
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                text =
                    buildAnnotatedString {
                        append(stringResource(R.string.name))
                        append(":")
                    },
                textAlign = TextAlign.Center,
            )
            var name by remember { mutableStateOf(existingName) }
            var nameError by remember { mutableStateOf(false) }
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 0.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    isError = nameError,
                    label = { Text(text = hint.orEmpty()) },
                    onValueChange = {
                        if (it.isNotEmpty()) {
                            nameError = false
                        }
                        if (it.length < 30) {
                            name = it
                        }
                    },
                )
                Text(
                    text = if (nameError) stringResource(R.string.name_required) else "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1F),
                    onClick = {
                        when (name.trim().isEmpty()) {
                            true -> nameError = true
                            else -> {
                                onName(name.trim())
                                dismiss()
                            }
                        }
                    },
                ) {
                    Text(text = stringResource(R.string.save))
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    modifier = Modifier,
                    onClick = dismiss,
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        }
    }
}

@Composable
fun AreYouSureDialog(
    title: String,
    dismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    Dialog(onDismissRequest = dismiss) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                text = title,
                textAlign = TextAlign.Center,
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDelete()
                    dismiss()
                },
            ) {
                Text(text = stringResource(R.string.yes))
            }
            Button(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                onClick = dismiss,
            ) {
                Text(text = stringResource(R.string.no))
            }
        }
    }
}
