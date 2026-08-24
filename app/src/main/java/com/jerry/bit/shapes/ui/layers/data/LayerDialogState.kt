package com.jerry.bit.shapes.ui.layers.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue

@Stable
data class LayerDialogState(
    private val showDeleteDialog: Boolean = false,
    private val showNameDialog: Boolean = false,
) {
    var showDeleteDialogState by mutableStateOf(false)
        private set

    var showNameDialogState by mutableStateOf(false)
        private set

    fun setShowDeleteDialog(visible: Boolean) {
        showDeleteDialogState = visible
    }

    fun setShowNameDialog(visible: Boolean) {
        showNameDialogState = visible
    }

    companion object {
        val SAVER =
            listSaver(
                save = { item ->
                    listOf(item.showDeleteDialogState, item.showNameDialogState)
                },
                restore = { state ->
                    LayerDialogState(
                        showDeleteDialog = state[0],
                        showNameDialog = state[1],
                    )
                },
            )
    }
}
