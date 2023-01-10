package com.jerry.boxes.ui.boxes

import com.jerry.boxes.ui.boxes.history.HistoryItem

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
    object ColorPicker : Action()
    data class AddColorToUsedList(val color: SerializableColor) : Action()
    data class AddToHistory(val historyItem: HistoryItem) : Action()
    data class TurnOnOrOffLayer(val on: Boolean, val layerId: Long) : Action()
    data class Save(val autoSave: Boolean) : Action()
}