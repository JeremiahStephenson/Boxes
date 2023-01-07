package com.jerry.boxes.ui.boxes.shapes

enum class Shape(val group: ShapeGroup) {
    Box(ShapeGroup.BASIC),
    Circle(ShapeGroup.BASIC),
    Star(ShapeGroup.BASIC),
    TriangleBottomLeft(ShapeGroup.CORNER),
    TriangleBottomRight(ShapeGroup.CORNER),
    TriangleTopLeft(ShapeGroup.CORNER),
    TriangleTopRight(ShapeGroup.CORNER),
    RectangleLeft(ShapeGroup.RECTANGLE),
    RectangleRight(ShapeGroup.RECTANGLE),
    RectangleTop(ShapeGroup.RECTANGLE),
    RectangleBottom(ShapeGroup.RECTANGLE),
    ArcRight(ShapeGroup.ARC),
    ArcLeft(ShapeGroup.ARC),
    ArcRightInverse(ShapeGroup.ARC),
    ArcLeftInverse(ShapeGroup.ARC),
    BoxTopLeft(ShapeGroup.CORNER),
    BoxBottomLeft(ShapeGroup.CORNER),
    BoxTopRight(ShapeGroup.CORNER),
    BoxBottomRight(ShapeGroup.CORNER),
}