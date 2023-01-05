package com.jerry.boxes.ui.boxes.state

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
class LayerState(
    private val layerCount: Int
) : Parcelable {

    @IgnoredOnParcel
    var selectedLayersState by mutableStateOf(LayerList(1.rangeTo(layerCount).toCollection(ArrayList())))
        private set
}