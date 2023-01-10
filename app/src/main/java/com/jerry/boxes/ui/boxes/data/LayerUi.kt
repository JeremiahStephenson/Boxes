package com.jerry.boxes.ui.boxes.data

data class LayerUi(
    val id: Long,
    val projectId: Long,
    val index: Int,
    val name: String,
    val on: Boolean,
    val selected: Boolean,
    val visibilityEnabled: Boolean,
    val showControls: Boolean
)