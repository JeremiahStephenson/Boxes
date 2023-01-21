package com.jerry.boxes.ui.shapes

import android.graphics.Canvas
import android.graphics.RectF
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jerry.boxes.ui.boxes.data.ColorAndShape

interface ShapersInterface {
    fun draw(
        scope: DrawScope,
        pos: RectF,
        color: ColorAndShape
    )

    fun draw(
        scope: Canvas,
        pos: RectF,
        color: ColorAndShape
    )
}

enum class Shape(val group: ShapeGroup) : ShapersInterface {
    Box(ShapeGroup.BASIC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawBox(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawBox(pos, color)
        }
    },
    Circle(ShapeGroup.BASIC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawCircle(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawCircle(pos, color)
        }
    },
    Star(ShapeGroup.BASIC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawStar(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawStar(pos, color)
        }
    },
    TriangleBottomLeft(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomLeft(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomLeft(pos, color)
        }
    },
    TriangleBottomRight(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomRight(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomRight(pos, color)
        }
    },
    TriangleTopLeft(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopLeft(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopLeft(pos, color)
        }
    },
    TriangleTopRight(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopRight(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopRight(pos, color)
        }
    },
    RectangleLeft(ShapeGroup.RECTANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawRectangleLeft(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawRectangleLeft(pos, color)
        }
    },
    RectangleRight(ShapeGroup.RECTANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawRectangleRight(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawRectangleRight(pos, color)
        }
    },
    RectangleTop(ShapeGroup.RECTANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawRectangleTop(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawRectangleTop(pos, color)
        }
    },
    RectangleBottom(ShapeGroup.RECTANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawRectangleBottom(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawRectangleBottom(pos, color)
        }
    },
    ArcCornerRight(ShapeGroup.CORNER_ARC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawCornerArcRight(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawCornerArcRight(pos, color)
        }
    },
    ArcCornerLeft(ShapeGroup.CORNER_ARC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawCornerArcLeft(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawCornerArcLeft(pos, color)
        }
    },
    ArcCornerRightInverse(ShapeGroup.CORNER_ARC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawCornerArcRightInverse(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawCornerArcRightInverse(pos, color)
        }
    },
    ArcCornerLeftInverse(ShapeGroup.CORNER_ARC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawCornerArcLeftInverse(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawCornerArcLeftInverse(pos, color)
        }
    },
    BoxTopLeft(ShapeGroup.CORNER) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawBoxTopLeft(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawBoxTopLeft(pos, color)
        }
    },
    BoxBottomLeft(ShapeGroup.CORNER) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawBoxBottomLeft(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawBoxBottomLeft(pos, color)
        }
    },
    BoxTopRight(ShapeGroup.CORNER) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawBoxTopRight(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawBoxTopRight(pos, color)
        }
    },
    BoxBottomRight(ShapeGroup.CORNER) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawBoxBottomRight(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawBoxBottomRight(pos, color)
        }
    },
    Diamond(ShapeGroup.BASIC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawDiamond(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawDiamond(pos, color)
        }
    },
    ArcLeft(ShapeGroup.ARC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawArcLeft(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawArcLeft(pos, color)
        }
    },
    ArcRight(ShapeGroup.ARC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawArcRight(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawArcRight(pos, color)
        }
    },
    ArcTop(ShapeGroup.ARC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawArcTop(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawArcTop(pos, color)
        }
    },
    ArcBottom(ShapeGroup.ARC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawArcBottom(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawArcBottom(pos, color)
        }
    },
    VerticalLine(ShapeGroup.LINE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawVerticalLine(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawVerticalLine(pos, color)
        }
    },
    HorizontalLine(ShapeGroup.LINE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawHorizontalLine(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawHorizontalLine(pos, color)
        }
    },
    TopLeftToBottomRightLine(ShapeGroup.LINE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTopLeftToBottomRightLine(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTopLeftToBottomRightLine(pos, color)
        }
    },
    BottomLeftToTopRightLine(ShapeGroup.LINE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawBottomLeftToTopRightLine(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawBottomLeftToTopRightLine(pos, color)
        }
    },
    TopLeftElbow(ShapeGroup.CORNER) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTopLeftElbow(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTopLeftElbow(pos, color)
        }
    },
    TopRightElbow(ShapeGroup.CORNER) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTopRightElbow(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTopRightElbow(pos, color)
        }
    },
    BottomLeftElbow(ShapeGroup.CORNER) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawBottomLeftElbow(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawBottomLeftElbow(pos, color)
        }
    },
    BottomRightElbow(ShapeGroup.CORNER) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawBottomRightElbow(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawBottomRightElbow(pos, color)
        }
    },
    TriangleBottomLeftSmall(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomLeftSmall(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomLeftSmall(pos, color)
        }
    },
    TriangleBottomRightSmall(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomRightSmall(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomRightSmall(pos, color)
        }
    },
    TriangleTopLeftSmall(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopLeftSmall(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopLeftSmall(pos, color)
        }
    },
    TriangleTopRightSmall(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopRightSmall(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopRightSmall(pos, color)
        }
    },
    TriangleBottomLeftSmallest(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomLeftSmallest(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomLeftSmallest(pos, color)
        }
    },
    TriangleBottomRightSmallest(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomRightSmallest(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleBottomRightSmallest(pos, color)
        }
    },
    TriangleTopLeftSmallest(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopLeftSmallest(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopLeftSmallest(pos, color)
        }
    },
    TriangleTopRightSmallest(ShapeGroup.CORNER_TRIANGLE) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopRightSmallest(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawTriangleTopRightSmallest(pos, color)
        }
    },
    Lego(ShapeGroup.BASIC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawLegoSquare(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawLegoSquare(pos, color)
        }
    },
    LegoRound(ShapeGroup.BASIC) {
        override fun draw(scope: DrawScope, pos: RectF, color: ColorAndShape) {
            scope.drawLegoRound(pos, color)
        }

        override fun draw(scope: Canvas, pos: RectF, color: ColorAndShape) {
            scope.drawLegoRound(pos, color)
        }
    }
}
