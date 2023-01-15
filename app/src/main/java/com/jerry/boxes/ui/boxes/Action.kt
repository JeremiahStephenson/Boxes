package com.jerry.boxes.ui.boxes

import android.graphics.Point
import com.jerry.boxes.ui.boxes.history.UserHistory
import com.jerry.boxes.ui.boxes.state.TapType

sealed class Action {
    object Clear : Action()
    object Eraser : Action()
    object ResetZoom: Action()
    object ShowPngBackground : Action()
    object ShowGrid : Action()
    object Edit : Action()
    object Undo : Action()
    object GoToLayerEdit : Action()
    data class Fill(val point: Point, val layerId: Long) : Action()
    data class SetTapType(val tapType: TapType) : Action()
    data class Export(val size: Float) : Action()
    data class AddLayer(val name: String): Action()
    data class SelectLayer(val layerId: Long) : Action()
    data class AddColorToUsedList(val color: SerializableColor) : Action()
    data class AddToHistory(val historyItem: UserHistory) : Action()
    data class TurnOnOrOffLayer(val on: Boolean, val layerId: Long) : Action()
    data class Save(val autoSave: Boolean) : Action()
}