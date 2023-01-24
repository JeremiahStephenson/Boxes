package com.jerry.boxes.ui.layers

data class ProjectUi(
    val id: Long,
    val name: String,
    val layers: List<LayerEditUi>
)