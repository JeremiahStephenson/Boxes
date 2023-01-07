package com.jerry.boxes.ui.boxes

sealed class Action {
    object Clear : Action()
    object Eraser : Action()
    object Export : Action()
    object ResetZoom: Action()
    object ShowPngBackground : Action()
    object ShowGrid : Action()
    object Edit : Action()
    object AddLayer: Action()
    data class TurnOnOrOffLayer(val on: Boolean, val layerId: Long) : Action()
    data class Save(val autoSave: Boolean) : Action()
}