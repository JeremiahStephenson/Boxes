package com.jerry.boxes.ui.boxes.state

import androidx.compose.runtime.Immutable
import com.jerry.boxes.cache.data.Layer

@Immutable
data class LayerList(val layers: List<Layer>) {
    val max get() = layers.filter { it.on }.maxBy { it.index }
    val hasLayersTurnedOn get() = layers.any { it.on }
    val turnedOnIds get() = layers.filter { it.on }.map { it.id }
}