package com.jerry.boxes.ui.boxes.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*

@Stable
class LayerState(private val layerList: LayerList) {
    val layersList by mutableStateOf(layerList)
    val hasLayersTurnedOn get() = layersList.hasLayersTurnedOn
    val max get() = layersList.max
    val turnedOnIds get() = layersList.turnedOnIds
}
