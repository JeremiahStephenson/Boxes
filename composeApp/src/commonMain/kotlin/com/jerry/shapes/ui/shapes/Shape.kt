package com.jerry.shapes.ui.shapes

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.shapes.cache.data.ColorAndShape

interface ShapersInterface {
    fun draw(
        scope: DrawScope,
        pos: Rect,
        color: ColorAndShape,
    )
}

enum class Shape(
    val group: ShapeGroup,
) : ShapersInterface {
    Box(ShapeGroup.BASIC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawBox(pos, color)
        }
    },
    Circle(ShapeGroup.BASIC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawCircle(pos, color)
        }
    },
    Star(ShapeGroup.BASIC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawStar(pos, color)
        }
    },
    TriangleLeft(ShapeGroup.TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleLeft(pos, color)
        }
    },
    TriangleRight(ShapeGroup.TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleRight(pos, color)
        }
    },
    TriangleTop(ShapeGroup.TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleTop(pos, color)
        }
    },
    TriangleBottom(ShapeGroup.TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleBottom(pos, color)
        }
    },
    TriangleBottomLeft(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleBottomLeft(pos, color)
        }
    },
    TriangleBottomRight(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleBottomRight(pos, color)
        }
    },
    TriangleTopLeft(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleTopLeft(pos, color)
        }
    },
    TriangleTopRight(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleTopRight(pos, color)
        }
    },
    RectangleLeft(ShapeGroup.RECTANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawRectangleLeft(pos, color)
        }
    },
    RectangleRight(ShapeGroup.RECTANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawRectangleRight(pos, color)
        }
    },
    RectangleTop(ShapeGroup.RECTANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawRectangleTop(pos, color)
        }
    },
    RectangleBottom(ShapeGroup.RECTANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawRectangleBottom(pos, color)
        }
    },
    ArcCornerRight(ShapeGroup.CORNER_ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawCornerArcRight(pos, color)
        }
    },
    ArcCornerLeft(ShapeGroup.CORNER_ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawCornerArcLeft(pos, color)
        }
    },
    ArcCornerRightInverse(ShapeGroup.CORNER_ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawCornerArcRightInverse(pos, color)
        }
    },
    ArcCornerLeftInverse(ShapeGroup.CORNER_ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawCornerArcLeftInverse(pos, color)
        }
    },
    ArcCornerRightSmall(ShapeGroup.CORNER_ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawCornerArcRightSmall(pos, color)
        }
    },
    ArcCornerLeftSmall(ShapeGroup.CORNER_ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawCornerArcLeftSmall(pos, color)
        }
    },
    ArcCornerRightInverseSmall(ShapeGroup.CORNER_ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawCornerArcRightInverseSmall(pos, color)
        }
    },
    ArcCornerLeftInverseSmall(ShapeGroup.CORNER_ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawCornerArcLeftInverseSmall(pos, color)
        }
    },
    BoxTopLeft(ShapeGroup.CORNER) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawBoxTopLeft(pos, color)
        }
    },
    BoxBottomLeft(ShapeGroup.CORNER) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawBoxBottomLeft(pos, color)
        }
    },
    BoxTopRight(ShapeGroup.CORNER) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawBoxTopRight(pos, color)
        }
    },
    BoxBottomRight(ShapeGroup.CORNER) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawBoxBottomRight(pos, color)
        }
    },
    Diamond(ShapeGroup.BASIC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawDiamond(pos, color)
        }
    },
    ArcLeft(ShapeGroup.ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawArcLeft(pos, color)
        }
    },
    ArcRight(ShapeGroup.ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawArcRight(pos, color)
        }
    },
    ArcTop(ShapeGroup.ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawArcTop(pos, color)
        }
    },
    ArcBottom(ShapeGroup.ARC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawArcBottom(pos, color)
        }
    },
    VerticalLine(ShapeGroup.LINE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawVerticalLine(pos, color)
        }
    },
    HorizontalLine(ShapeGroup.LINE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawHorizontalLine(pos, color)
        }
    },
    TopLeftToBottomRightLine(ShapeGroup.LINE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTopLeftToBottomRightLine(pos, color)
        }
    },
    BottomLeftToTopRightLine(ShapeGroup.LINE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawBottomLeftToTopRightLine(pos, color)
        }
    },
    TopLeftElbow(ShapeGroup.CORNER) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTopLeftElbow(pos, color)
        }
    },
    TopRightElbow(ShapeGroup.CORNER) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTopRightElbow(pos, color)
        }
    },
    BottomLeftElbow(ShapeGroup.CORNER) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawBottomLeftElbow(pos, color)
        }
    },
    BottomRightElbow(ShapeGroup.CORNER) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawBottomRightElbow(pos, color)
        }
    },
    TriangleBottomLeftSmall(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleBottomLeftSmall(pos, color)
        }
    },
    TriangleBottomRightSmall(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleBottomRightSmall(pos, color)
        }
    },
    TriangleTopLeftSmall(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleTopLeftSmall(pos, color)
        }
    },
    TriangleTopRightSmall(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleTopRightSmall(pos, color)
        }
    },
    TriangleBottomLeftSmallest(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleBottomLeftSmallest(pos, color)
        }
    },
    TriangleBottomRightSmallest(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleBottomRightSmallest(pos, color)
        }
    },
    TriangleTopLeftSmallest(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleTopLeftSmallest(pos, color)
        }
    },
    TriangleTopRightSmallest(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawTriangleTopRightSmallest(pos, color)
        }
    },
    Lego(ShapeGroup.LEGO) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawLegoSquare(pos, color)
        }
    },
    LegoRound(ShapeGroup.LEGO) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawLegoRound(pos, color)
        }
    },
    RoundedBox(ShapeGroup.BASIC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawRoundedBox(pos, color)
        }
    },
    Octagon(ShapeGroup.BASIC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawOctagon(pos, color)
        }
    },
    Heart(ShapeGroup.BASIC) {
        override fun draw(
            scope: DrawScope,
            pos: Rect,
            color: ColorAndShape,
        ) {
            scope.drawHeart(pos, color)
        }
    },
}

const val LEGO_LIMIT = 2500
