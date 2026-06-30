package com.jerry.shapes.ui.boxes.data

import android.graphics.Point
import android.net.Uri
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.ui.boxes.history.UserHistory
import com.jerry.shapes.ui.boxes.state.enums.Direction
import com.jerry.shapes.ui.boxes.state.enums.TapType
import com.jerry.shapes.ui.shapes.Shape
import com.jerry.shapes.util.ExportType

sealed interface Action {
    data object Clear : Action

    data object Eraser : Action

    data object ResetZoom : Action

    data object ShowPngBackground : Action

    data object ShowGrid : Action

    data object SelectTool : Action

    data object Edit : Action

    data object Undo : Action

    data object GoToLayerEdit : Action

    data object ClearSelect : Action

    data class ImageImport(
        val uri: Uri,
        val layerId: Long,
    ) : Action

    data class SetColor(
        val color: ColorAndShape,
    ) : Action

    data class SetShape(
        val shape: Shape,
    ) : Action

    data class Move(
        val layerId: Long,
        val direction: Direction,
    ) : Action

    data class Fill(
        val point: Point,
        val layerId: Long,
    ) : Action

    data class SetTapType(
        val tapType: TapType,
    ) : Action

    data class Export(
        val size: Int,
        val exportType: ExportType,
    ) : Action

    data class AddLayer(
        val name: String,
    ) : Action

    data class SelectLayer(
        val layerId: Long,
    ) : Action

    data class AddColorToUsedList(
        val color: ColorAndShape,
    ) : Action

    data class AddToHistory(
        val historyItem: UserHistory,
    ) : Action

    data class TurnOnOrOffLayer(
        val on: Boolean,
        val layerId: Long,
    ) : Action

    data class Save(
        val autoSave: Boolean,
    ) : Action
}
