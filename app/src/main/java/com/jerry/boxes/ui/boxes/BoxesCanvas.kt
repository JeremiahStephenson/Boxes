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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.jerry.boxes.extensions.safeLet
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.state.ButtonsState
import com.jerry.boxes.ui.boxes.state.CanvasState
import com.jerry.boxes.ui.common.LocalAppBarHeight
import com.jerry.boxes.ui.common.pngBackground
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
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
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit
) {
    val contentOffset = LocalAppBarHeight.current
    val appBarExpanded by remember { derivedStateOf { contentOffset.value == 0F } }
    val scaleState by rememberUpdatedState(scale)
    val offsetState by rememberUpdatedState(offset)
    val sizeState by rememberUpdatedState(size)
    val pngBoxSize = with(LocalDensity.current) { 10.dp.toPx() }

    if (buttonsState.showPngBackgroundState) {
        Box(modifier = Modifier
            .fillMaxSize()
            .pngBackground(true, pngBoxSize)
        )
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(appBarExpanded) {
            detectTapGestures { point ->
                if (state.isTransformInProgress) return@detectTapGestures
                point
                    .findBox(
                        scaleState,
                        offsetState,
                        sizeState,
                        canvasState.boxes
                    )
                    ?.let {
                        onTap(it)
                    }
            }
        }
        .pointerInput(appBarExpanded) {
            detectDragGestures(
                onDragStart = { onDragStart() },
                onDragEnd = { onDragEnd() },
                onDragCancel = { onDragEnd() },
                onDrag = { position, change ->
                    if (state.isTransformInProgress) return@detectDragGestures
                    onDrag(
                        getDragPoints(canvasState, change, position).findBoxes(
                            scaleState,
                            offsetState,
                            sizeState,
                            canvasState.boxes
                        )
                    )
                }
            )
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
    layers: List<LayerUi>,
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

private fun getDragPoints(
    canvasState: CanvasState,
    change: Offset,
    position: PointerInputChange
): List<Offset> {
    return mutableListOf<Offset>().apply {
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
): Point? {
    return boxes[Point(0, 0)]?.let {
        val point = convert(scale, offset, size)
        with(it.width()) {
            Point(
                floor((point.x - it.left) / this).toInt(),
                floor((point.y - it.top) / this).toInt()
            )
        }
    }
}

private fun List<Offset>.findBoxes(
    scale: Float,
    offset: Offset,
    size: Constraints,
    boxes: Map<Point, RectF>
): List<Point> {
    return boxes[Point(0, 0)]?.let {
        with (it.width()) {
            mapNotNull { p ->
                val point = p.convert(scale, offset, size)
                Point(
                    floor((point.x - it.left) / this).toInt(),
                    floor((point.y - it.top) / this).toInt()
                )
            }
        }.distinct()
    } ?: emptyList()
}