package com.jerry.boxes.ui.boxes.state

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class LayerList(val layers: ArrayList<Int>) : Serializable {
    val max get() = layers.max()
}