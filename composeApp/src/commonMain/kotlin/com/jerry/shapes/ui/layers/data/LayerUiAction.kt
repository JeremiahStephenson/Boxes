package com.jerry.shapes.ui.layers.data

sealed interface LayerUiAction {
    data object ShowOpacity : LayerUiAction

    data object ShowReorderBtn : LayerUiAction

    data object ShowDivider : LayerUiAction

    data object ShowDeleteBtn : LayerUiAction
}
