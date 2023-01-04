package com.jerry.boxes.ui.boxes

sealed class Action {
    object Clear : Action()
    object Eraser : Action()
    object Save : Action()
    object Export : Action()
    object ResetZoom: Action()
    object ShowPngBackground : Action()
}