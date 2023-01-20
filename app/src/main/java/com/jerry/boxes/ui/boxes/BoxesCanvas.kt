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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.extensions.findBox
import com.jerry.boxes.extensions.findBoxes
import com.jerry.boxes.extensions.safeLet
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.state.ButtonsState
import com.jerry.boxes.ui.boxes.state.CanvasState
import com.jerry.boxes.ui.boxes.state.SelectionState
import com.jerry.boxes.ui.common.LocalAppBarHeight
import com.jerry.boxes.ui.common.pngBackground
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun BoxCanvas(
    canvasState: CanvasState,
    buttonsState: ButtonsState,
    selectionState: SelectionState,
    project: Project,
    scale: Float,
    size: Constraints,
    offset: Offset,
    strokeWidth: Float,
    state: TransformableState,
    onTap: (Point) -> Unit,
    onDrag: (HashSet<Point>) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit
) {
    val contentOffset = LocalAppBarHeight.current
    val appBarExpanded by remember { derivedStateOf { contentOffset.value == 0F } }
    val scaleState by rememberUpdatedState(scale)
    val offsetState by rememberUpdatedState(offset)
    val sizeState by rememberUpdatedState(size)
    val pngBoxSize = with(LocalDensity.current) { 10.dp.toPx() }
    val columnsState by rememberUpdatedState(project.columns)
    val rowsState by rememberUpdatedState(project.rows)

    if (project.showPngBg) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pngBackground(true, pngBoxSize)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(appBarExpanded) {
                detectTapGestures { point ->
                    if (state.isTransformInProgress) return@detectTapGestures
                    point
                        .findBox(
                            scaleState,
                            offsetState,
                            sizeState,
                            columnsState,
                            rowsState,
                            canvasState.boxes
                        )
                        ?.let {
                            onTap(it)
                        }
                }
            }
            .pointerInput(appBarExpanded) {
                detectDragGestures(
                    onDragStart = {
                        if (state.isTransformInProgress) return@detectDragGestures
                        when (buttonsState.selectToolSelectedState) {
                            true -> selectionState.startSelection(
                                it,
                                scaleState,
                                offsetState,
                                sizeState,
                                columnsState,
                                rowsState,
                                canvasState.boxes
                            )
                            else -> onDragStart()
                        }
                    },
                    onDragEnd = {
                        if (!buttonsState.selectToolSelectedState) {
                            onDragEnd()
                        }
                    },
                    onDragCancel = {
                        if (!buttonsState.selectToolSelectedState) {
                            onDragEnd()
                        }
                    },
                    onDrag = { position, change ->
                        if (state.isTransformInProgress) return@detectDragGestures
                        when (buttonsState.selectToolSelectedState) {
                            true -> {
                                selectionState.updateSelection(
                                    position.position,
                                    scaleState,
                                    offsetState,
                                    sizeState,
                                    columnsState,
                                    rowsState,
                                    canvasState.boxes
                                )
                            }
                            else -> onDrag(
                                getDragPoints(canvasState, change, position).findBoxes(
                                    scaleState,
                                    offsetState,
                                    sizeState,
                                    columnsState,
                                    rowsState,
                                    canvasState.boxes
                                )
                            )
                        }
                    }
                )
            }
            .transformable(
                state = state,
                lockRotationOnZoomPan = true
            )
    ) {
        SelectionsBoxes(
            scale = scale,
            offset = offset,
            size = size,
            canvasState = canvasState
        )

        if (project.showGrid) {
            val color = when (project.showPngBg) {
                true -> MaterialTheme.colorScheme.background
                else -> Color.Gray
            }
            Grid(
                rows = rowsState,
                columns = columnsState,
                strokeWidth = strokeWidth,
                strokeColor = color,
                scale = scale,
                offset = offset,
                size = sizeState,
                canvasState = canvasState
            )
        }

        LaunchedEffect(buttonsState.selectToolSelectedState) {
            if (!buttonsState.selectToolSelectedState) {
                selectionState.clear()
            }
        }
        LaunchedEffect(rowsState, columnsState) {
            selectionState.checkRowsAndColumns(columnsState, rowsState)
        }
        if (buttonsState.selectToolSelectedState) {
            SelectionTool(
                scale = scale,
                offset = offset,
                size = size,
                boxes = canvasState.boxes,
                selectionState = selectionState
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
    selections: Map<Long, Map<Point, ColorAndShape>>
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
        drawShapes(
            layers,
            selections,
            boxes
        )
    }
}

@Composable
fun SelectionsBoxes(
    scale: Float,
    offset: Offset,
    size: Constraints,
    canvasState: CanvasState
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                transformOrigin = TransformOrigin(
                    ((size.maxWidth / 2F) - offset.x) / size.maxWidth,
                    ((size.maxHeight / 2F) - offset.y) / size.maxHeight
                )
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            }
    ) {
        drawShapes(
            canvasState.layers,
            canvasState.selections,
            canvasState.boxes
        )
    }
}

@Composable
fun SelectionTool(
    scale: Float,
    offset: Offset,
    size: Constraints,
    boxes: Map<Point, RectF>,
    selectionState: SelectionState
) {
    val highlightColor = MaterialTheme.colorScheme.primary
    val stroke = with(LocalDensity.current) { 5.dp.toPx() }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                transformOrigin = TransformOrigin(
                    ((size.maxWidth / 2F) - offset.x) / size.maxWidth,
                    ((size.maxHeight / 2F) - offset.y) / size.maxHeight
                )
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            }
    ) {
        if (selectionState.topLeftState == null || selectionState.bottomRightState == null) return@Canvas
        safeLet(
            boxes[selectionState.topLeftState],
            boxes[selectionState.bottomRightState]
        ) { tl, br ->
            val adjustmentTopLeft = Offset(
                when (br.left < tl.left) {
                    true -> tl.right
                    else -> tl.left
                },
                when (br.top < tl.top) {
                    true -> tl.bottom
                    else -> tl.top
                }
            )
            val adjustmentBottomRight = Offset(
                when (br.left < tl.left) {
                    true -> br.left
                    else -> br.right
                },
                when (br.top < tl.top) {
                    true -> br.top
                    else -> br.bottom
                }
            )
            drawRect(
                style = Stroke(width = stroke / scale),
                color = highlightColor,
                topLeft = adjustmentTopLeft,
                size = Size(
                    adjustmentBottomRight.x - adjustmentTopLeft.x,
                    adjustmentBottomRight.y - adjustmentTopLeft.y
                )
            )
        }
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
    size: Constraints,
    canvasState: CanvasState
) {
    Canvas(
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                transformOrigin = TransformOrigin(
                    ((size.maxWidth / 2F) - offset.x) / size.maxWidth,
                    ((size.maxHeight / 2F) - offset.y) / size.maxHeight
                )
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            }
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
): HashSet<Offset> {
    return HashSet<Offset>().apply {
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
