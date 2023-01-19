package com.jerry.boxes.ui.boxes

import android.graphics.Point
import com.jerry.boxes.ui.boxes.history.UserHistory
import com.jerry.boxes.ui.boxes.state.enums.Direction
import com.jerry.boxes.ui.boxes.state.enums.TapType
import com.jerry.boxes.ui.shapes.Shape

sealed class Action {
    object Clear : Action()
    object Eraser : Action()
    object ResetZoom: Action()
    object ShowPngBackground : Action()
    object ShowGrid : Action()
    object SelectTool : Action()
    object Edit : Action()
    object Undo : Action()
    object GoToLayerEdit : Action()
    object ClearSelect : Action()
    data class SetColor(val color: ColorAndShape) : Action()
    data class SetShape(val shape: Shape) : Action()
    data class Move(val direction: Direction) : Action()
    data class Fill(val point: Point, val layerId: Long) : Action()
    data class SetTapType(val tapType: TapType) : Action()
    data class Export(val size: Float) : Action()
    data class AddLayer(val name: String): Action()
    data class SelectLayer(val layerId: Long) : Action()
    data class AddColorToUsedList(val color: ColorAndShape) : Action()
    data class AddToHistory(val historyItem: UserHistory) : Action()
    data class TurnOnOrOffLayer(val on: Boolean, val layerId: Long) : Action()
    data class Save(val autoSave: Boolean) : Action()
}