package com.jerry.bit.shapes.ui.boxes.data

enum class DragMode {
    NONE,
    TRANSFORM,
    DRAW,
    ;

    val isTransforming get() = this == TRANSFORM
    val isDrawing get() = this == DRAW
}
