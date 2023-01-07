package com.jerry.boxes.ui.boxes

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.jerry.boxes.cache.data.Layer
import com.jerry.boxes.extensions.safeLet
import com.jerry.boxes.ui.boxes.state.ButtonsState
import com.jerry.boxes.ui.boxes.state.CanvasState
import com.jerry.boxes.ui.common.LocalAppBarHeight
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun BoxCanvas(
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    columns: Int,
    rows: Int,
    scale: Float,
    size: Constraints,
    offset: Offset,
    strokeWidth: Float,
    state: TransformableState,
    onTap: (Point) -> Unit,
    onDrag: (List<Point>) -> Unit,
) {
    val contentOffset = LocalAppBarHeight.current
    val appBarExpanded by remember { derivedStateOf { contentOffset.value == 0F } }
    val scaleState by rememberUpdatedState(scale)
    val offsetState by rememberUpdatedState(offset)
    val sizeState by rememberUpdatedState(size)
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(appBarExpanded) {
            detectTapGestures { point ->
                point.findBox(
                    scaleState,
                    offsetState,
                    sizeState,
                    canvasState.boxes
                )?.let {
                    onTap(it)
                }
            }
        }
        .pointerInput(appBarExpanded) {
            detectDragGestures { position, change ->
                if (state.isTransformInProgress) return@detectDragGestures
                mutableListOf<Offset>().apply {
                    val boxSize = canvasState.boxes.values
                        .first()
                        .width()
                        .toInt()

                    if (abs(change.x) > boxSize || abs(change.y) > boxSize) {
                        val distance =
                            sqrt(
                                (position.position.x - position.previousPosition.x).pow(2) +
                                        (position.position.y - position.previousPosition.y).pow(
                                            2
                                        )
                            )
                        for (i in 1..(distance / boxSize).toInt()) {
                            val t = (boxSize * i) / distance
                            add(
                                Offset(
                                    ((1 - t) * (position.previousPosition.x) + (t * position.position.x)),
                                    ((1 - t) * (position.previousPosition.y) + (t * position.position.y))
                                )
                            )
                        }
                    }

                    add(position.position)

                    onDrag(
                        findBoxes(
                            scaleState,
                            offsetState,
                            sizeState,
                            canvasState.boxes
                        )
                    )
                }
            }
        }
        .transformable(
            state = state,
            lockRotationOnZoomPan = true
        )) {

        SelectionsBoxes(
            scale = scale,
            offset = offset,
            canvasState = canvasState
        )

        if (buttonsState.showGridState) {
            val color = when (buttonsState.showPngBackgroundState) {
                true -> MaterialTheme.colorScheme.background
                else -> Color.Gray
            }
            Grid(
                rows = rows,
                columns = columns,
                strokeWidth = strokeWidth,
                strokeColor = color,
                scale = scale,
                offset = offset,
                canvasState = canvasState
            )
        }
    }
}

@Composable
fun SelectionsBoxes(
    scale: Float,
    offset: Offset,
    layers: List<Layer>,
    boxes: Map<Point, RectF>,
    selections: Map<Point, Map<Long, SerializableColor?>?>
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
    ) {
        drawShapes(layers, selections, boxes)
    }
}

@Composable
fun SelectionsBoxes(
    scale: Float,
    offset: Offset,
    canvasState: CanvasState
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
    ) {
        drawShapes(canvasState.layers, canvasState.selections, canvasState.boxes)
    }
}

@Composable
fun PngBackground() {
    val size = with(LocalDensity.current) { 10.dp.toPx() }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        val columns = (this.size.width / size).roundToInt()
        val rows = (this.size.height / size).roundToInt()
        for (r in 0..rows) {
            for (c in 0..columns) {
                drawRect(
                    color = Color.Gray,
                    topLeft = Offset(c * size, r * size),
                    size = Size(size, size),
                    alpha = when (r % 2 == 0) {
                        true -> when (c % 2 == 0) {
                            true -> 1F
                            else -> GRID_ODD_ALPHA
                        }
                        else -> when (c % 2 == 0) {
                            true -> GRID_ODD_ALPHA
                            else -> 1F
                        }
                    }
                )
            }
        }
    }
}

private const val GRID_ODD_ALPHA = 0.5F

@Composable
private fun Grid(
    rows: Int,
    columns: Int,
    strokeWidth: Float,
    strokeColor: Color,
    scale: Float,
    offset: Offset,
    canvasState: CanvasState
) {
    Canvas(
        Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
    ) {
        for (i in 0 until rows) {
            safeLet(
                canvasState.boxes[Point(0, i)],
                canvasState.boxes[Point(columns - 1, i)]
            ) { start, end ->
                drawLine(
                    strokeWidth = strokeWidth / scale,
                    color = strokeColor,
                    start = Offset(start.left, start.top),
                    end = Offset(end.right, end.top)
                )
                if (i == rows - 1) {
                    drawLine(
                        strokeWidth = strokeWidth / scale,
                        color = strokeColor,
                        start = Offset(start.left, start.bottom),
                        end = Offset(end.right, end.bottom)
                    )
                }
            }
        }
        for (i in 0 until columns) {
            safeLet(
                canvasState.boxes[Point(i, 0)],
                canvasState.boxes[Point(i, rows - 1)]
            ) { start, end ->
                drawLine(
                    strokeWidth = strokeWidth / scale,
                    color = strokeColor,
                    start = Offset(start.left, start.top),
                    end = Offset(end.left, end.bottom)
                )
                if (i == columns - 1) {
                    drawLine(
                        strokeWidth = strokeWidth / scale,
                        color = strokeColor,
                        start = Offset(start.right, start.top),
                        end = Offset(end.right, end.bottom)
                    )
                }
            }
        }
    }
}

private fun Offset.convert(scale: Float, offset: Offset, size: Constraints): Offset {
    val centerX = size.maxWidth / 2F
    val centerY = size.maxHeight / 2F
    val point =
        Offset(((x - centerX) * (1F / scale)) + centerX, ((y - centerY) * (1F / scale)) + centerY)
    return point - (offset / scale)
}

private fun Offset.findBox(
    scale: Float,
    offset: Offset,
    size: Constraints,
    boxes: Map<Point, RectF>
) = findPoint(scale, offset, size, boxes)

private fun List<Offset>.findBoxes(
    scale: Float,
    offset: Offset,
    size: Constraints,
    boxes: Map<Point, RectF>
) = mapNotNull {
    it.findPoint(scale, offset, size, boxes)
}

private fun Offset.findPoint(
    scale: Float,
    offset: Offset,
    size: Constraints,
    boxes: Map<Point, RectF>
): Point? {
    val point = convert(scale, offset, size)
    return boxes.entries.find { box ->
        point.x >= box.value.left && point.x <= box.value.right &&
                point.y >= box.value.top && point.y <= box.value.bottom
    }?.key
}