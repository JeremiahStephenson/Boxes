package com.jerry.boxes.ui.boxes

import com.jerry.boxes.ui.boxes.history.History

sealed class Action {
    object Clear : Action()
    object Eraser : Action()
    object Export : Action()
    object ResetZoom: Action()
    object ShowPngBackground : Action()
    object ShowGrid : Action()
    object Edit : Action()
    object AddLayer: Action()
    object Undo : Action()
    data class AddToHistory(val history: History) : Action()
    data class TurnOnOrOffLayer(val on: Boolean, val layerId: Long) : Action()
    data class Save(val autoSave: Boolean) : Action()
}