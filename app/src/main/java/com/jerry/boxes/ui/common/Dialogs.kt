package com.jerry.boxes.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jerry.boxes.R

@Composable
fun SetNameDialog(
    existingName: String,
    dismiss: () -> Unit,
    onName: (String) -> Unit
) {
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
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                text = buildAnnotatedString {
                    append(stringResource(R.string.name))
                    append(":")
                },
                textAlign = TextAlign.Center
            )
            var name by remember { mutableStateOf(existingName) }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                value = name,
                onValueChange = {
                    name = it
                })
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = dismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                onClick = {
                    onName(name)
                    dismiss()
                }
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}

@Composable
fun AreYouSureDialog(
    title: String,
    dismiss: () -> Unit,
    onDelete: () -> Unit
) {
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
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                text = title,
                textAlign = TextAlign.Center
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDelete()
                    dismiss()
                }
            ) {
                Text(text = stringResource(R.string.yes))
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                onClick = dismiss
            ) {
                Text(text = stringResource(R.string.no))
            }
        }
    }
}