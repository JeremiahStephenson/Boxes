package com.jerry.boxes.ui.boxes

sealed class Action {
    object Clear : Action()
    object Eraser : Action()
    object Export : Action()
    object ResetZoom: Action()
    object ShowPngBackground : Action()
    object Edit : Action()
    data class Save(val autoSave: Boolean) : Action()
}