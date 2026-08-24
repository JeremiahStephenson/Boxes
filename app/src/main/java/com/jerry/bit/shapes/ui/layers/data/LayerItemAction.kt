package com.jerry.bit.shapes.ui.layers.data

sealed interface LayerItemAction {
    data object OnDeleteItem : LayerItemAction

    @JvmInline
    value class OnLayerName(
        val name: String,
    ) : LayerItemAction

    data object OnDragEnd : LayerItemAction
}
