package com.jerry.shapes.ui.boxes

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import com.jerry.shapes.cache.data.Project
import com.jerry.shapes.extensions.findBox
import com.jerry.shapes.extensions.findBoxes
import com.jerry.shapes.extensions.safeLet
import com.jerry.shapes.ui.boxes.data.DragMode
import com.jerry.shapes.ui.boxes.state.ButtonsState
import com.jerry.shapes.ui.boxes.state.CanvasState
import com.jerry.shapes.ui.boxes.state.SelectionState
import com.jerry.shapes.ui.common.LocalAppBarHeight
import com.jerry.shapes.ui.common.pngBackground
import com.jerry.shapes.util.QUADRANT_SIZE
import com.jerry.shapes.util.drawShapes
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
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
    onDragEnd: () -> Unit,
) {
    val contentOffset = LocalAppBarHeight.current
    val appBarExpanded by remember { derivedStateOf { contentOffset.value == 0F } }
    val scaleState by rememberUpdatedState(scale)
    val offsetState by rememberUpdatedState(offset)
    val sizeState by rememberUpdatedState(size)
    val pngBoxSize = with(LocalDensity.current) { 20.dp.toPx() }
    val columnsState by rememberUpdatedState(project.columns)
    val rowsState by rememberUpdatedState(project.rows)

    if (project.showPngBg) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .pngBackground(true, pngBoxSize),
        )
    }

    val scope = rememberCoroutineScope()
    Box(
        modifier =
            Modifier
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
                                canvasState.boxes,
                            )?.let {
                                onTap(it)
                            }
                    }
                }.gesturePointer(
                    state = state,
                    onDragStart = { change ->
                        when (buttonsState.selectToolSelectedState) {
                            true ->
                                selectionState.startSelection(
                                    change.position,
                                    scaleState,
                                    offsetState,
                                    sizeState,
                                    columnsState,
                                    rowsState,
                                    canvasState.boxes,
                                )

                            else -> onDragStart()
                        }
                    },
                    onDrag = { change, dragAmount ->
                        when (buttonsState.selectToolSelectedState) {
                            true ->
                                selectionState.updateSelection(
                                    change.position,
                                    scaleState,
                                    offsetState,
                                    sizeState,
                                    columnsState,
                                    rowsState,
                                    canvasState.boxes,
                                )

                            else ->
                                onDrag(
                                    getDragPoints(
                                        canvasState,
                                        dragAmount,
                                        change,
                                    ).findBoxes(
                                        scaleState,
                                        offsetState,
                                        sizeState,
                                        columnsState,
                                        rowsState,
                                        canvasState.boxes,
                                    ),
                                )
                        }
                    },
                    onDragEnd = {
                        if (!buttonsState.selectToolSelectedState) {
                            onDragEnd()
                        }
                    },
                    onTransform = { panDelta, previousDistance, currentDistance ->
                        scope.launch {
                            state.transform {
                                transformBy(
                                    panChange = panDelta,
                                    zoomChange = if (previousDistance > 0F) currentDistance / previousDistance else 1F,
                                )
                            }
                        }
                    },
                ),
// TODO Delete the following once I'm confident that the above solution is fool-proof //
//                .pointerInput(appBarExpanded) {
//                    detectDragGestures(
//                        onDragStart = {
//                            if (state.isTransformInProgress) return@detectDragGestures
//                            when (buttonsState.selectToolSelectedState) {
//                                true ->
//                                    selectionState.startSelection(
//                                        it,
//                                        scaleState,
//                                        offsetState,
//                                        sizeState,
//                                        columnsState,
//                                        rowsState,
//                                        canvasState.boxes,
//                                    )
//
//                                else -> onDragStart()
//                            }
//                        },
//                        onDragEnd = {
//                            if (!buttonsState.selectToolSelectedState) {
//                                onDragEnd()
//                            }
//                        },
//                        onDragCancel = {
//                            if (!buttonsState.selectToolSelectedState) {
//                                onDragEnd()
//                            }
//                        },
//                        onDrag = { position, change ->
//                            if (state.isTransformInProgress) return@detectDragGestures
//                            when (buttonsState.selectToolSelectedState) {
//                                true -> {
//                                    selectionState.updateSelection(
//                                        position.position,
//                                        scaleState,
//                                        offsetState,
//                                        sizeState,
//                                        columnsState,
//                                        rowsState,
//                                        canvasState.boxes,
//                                    )
//                                }
//
//                                else ->
//                                    onDrag(
//                                        getDragPoints(canvasState, change, position).findBoxes(
//                                            scaleState,
//                                            offsetState,
//                                            sizeState,
//                                            columnsState,
//                                            rowsState,
//                                            canvasState.boxes,
//                                        ),
//                                    )
//                            }
//                        },
//                    )
//                }
//                .transformable(
//                    state = state,
//                    canPan = { false },
//                    lockRotationOnZoomPan = true,
//                ),
    ) {
        val quadrantXSize by remember {
            derivedStateOf {
                ceil(columnsState / QUADRANT_SIZE).toInt()
            }
        }
        val quadrantYSize by remember {
            derivedStateOf {
                ceil(rowsState / QUADRANT_SIZE).toInt()
            }
        }
        SelectionsBoxes(
            scale = scale,
            offset = offset,
            size = size,
            quadrantXSize = quadrantXSize,
            quadrantYSize = quadrantYSize,
            canvasState = canvasState,
        )

        if (project.showGrid) {
            val color =
                when (project.showPngBg) {
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
                canvasState = canvasState,
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
                selectionState = selectionState,
            )
        }
    }
}

@Composable
fun SelectionsBoxes(
    scale: Float,
    offset: Offset,
    size: Constraints,
    quadrantXSize: Int,
    quadrantYSize: Int,
    canvasState: CanvasState,
) {
    for (layerIndex in 0 until canvasState.layersOrder.count()) {
        for (x in 0 until quadrantXSize) {
            for (y in 0 until quadrantYSize) {
                Canvas(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                transformOrigin =
                                    TransformOrigin(
                                        ((size.maxWidth / 2F) - offset.x) / size.maxWidth,
                                        ((size.maxHeight / 2F) - offset.y) / size.maxHeight,
                                    )
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                            },
                ) {
                    val id = canvasState.layersOrder[layerIndex]
                    if (canvasState.layersVisibility[id]?.value == true) {
                        drawShapes(
                            id,
                            canvasState.selections[id]?.get(Point(x, y)),
                            canvasState.boxes,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionTool(
    scale: Float,
    offset: Offset,
    size: Constraints,
    boxes: Map<Point, RectF>,
    selectionState: SelectionState,
) {
    val highlightColor = MaterialTheme.colorScheme.primary
    val stroke = with(LocalDensity.current) { 5.dp.toPx() }
    Canvas(
        modifier =
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    transformOrigin =
                        TransformOrigin(
                            ((size.maxWidth / 2F) - offset.x) / size.maxWidth,
                            ((size.maxHeight / 2F) - offset.y) / size.maxHeight,
                        )
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                },
    ) {
        if (selectionState.topLeftState == null || selectionState.bottomRightState == null) return@Canvas
        safeLet(
            boxes[selectionState.topLeftState],
            boxes[selectionState.bottomRightState],
        ) { tl, br ->
            val adjustmentTopLeft =
                Offset(
                    when (br.left < tl.left) {
                        true -> tl.right
                        else -> tl.left
                    },
                    when (br.top < tl.top) {
                        true -> tl.bottom
                        else -> tl.top
                    },
                )
            val adjustmentBottomRight =
                Offset(
                    when (br.left < tl.left) {
                        true -> br.left
                        else -> br.right
                    },
                    when (br.top < tl.top) {
                        true -> br.top
                        else -> br.bottom
                    },
                )
            drawRect(
                style = Stroke(width = stroke / scale),
                color = highlightColor,
                topLeft = adjustmentTopLeft,
                size =
                    Size(
                        adjustmentBottomRight.x - adjustmentTopLeft.x,
                        adjustmentBottomRight.y - adjustmentTopLeft.y,
                    ),
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
    canvasState: CanvasState,
) {
    Canvas(
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                transformOrigin =
                    TransformOrigin(
                        ((size.maxWidth / 2F) - offset.x) / size.maxWidth,
                        ((size.maxHeight / 2F) - offset.y) / size.maxHeight,
                    )
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            },
    ) {
        val boxSize = (canvasState.boxes[Point(0, 0)]?.width() ?: 0F) * scale
        val stroke = strokeWidth / scale
        val strokeSpace = stroke * 2
        if (boxSize > strokeSpace) {
            for (i in 0 until rows) {
                safeLet(
                    canvasState.boxes[Point(0, i)],
                    canvasState.boxes[Point(columns - 1, i)],
                ) { start, end ->
                    drawLine(
                        strokeWidth = stroke,
                        color = strokeColor,
                        start = Offset(start.left, start.top),
                        end = Offset(end.right, end.top),
                    )
                    if (i == rows - 1) {
                        drawLine(
                            strokeWidth = stroke,
                            color = strokeColor,
                            start = Offset(start.left, start.bottom),
                            end = Offset(end.right, end.bottom),
                        )
                    }
                }
            }
            for (i in 0 until columns) {
                safeLet(
                    canvasState.boxes[Point(i, 0)],
                    canvasState.boxes[Point(i, rows - 1)],
                ) { start, end ->
                    drawLine(
                        strokeWidth = stroke,
                        color = strokeColor,
                        start = Offset(start.left, start.top),
                        end = Offset(end.left, end.bottom),
                    )
                    if (i == columns - 1) {
                        drawLine(
                            strokeWidth = stroke,
                            color = strokeColor,
                            start = Offset(start.right, start.top),
                            end = Offset(end.right, end.bottom),
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.gesturePointer(
    state: TransformableState,
    onDragStart: (PointerInputChange) -> Unit,
    onDrag: (PointerInputChange, Offset) -> Unit,
    onDragEnd: () -> Unit,
    onTransform: (Offset, Float, Float) -> Unit,
): Modifier =
    pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown()

            // Keep track of previous positions to calculate custom deltas
            var previousCentroid = Offset.Unspecified
            var dragMode: DragMode = DragMode.NONE

            do {
                val event = awaitPointerEvent()
                // Check the live number of active fingers
                val activePointers = event.changes.filter { it.pressed }
                val pointerCount = activePointers.size

                when {
                    // Drawing with single finger
                    pointerCount == 1 && !dragMode.isTransforming && !state.isTransformInProgress -> {
                        val change = activePointers.first()
                        val dragAmount = change.position - change.previousPosition

                        change.consume() // Consume the event so parent views don't steal it

                        if (!dragMode.isDrawing) {
                            onDragStart(change)
                            dragMode = DragMode.DRAW
                        }

                        onDrag(change, dragAmount)

                        // Reset multi-finger tracking state
                        previousCentroid = Offset.Unspecified
                    }

                    // Pinch to zoom gesture
                    pointerCount >= 2 -> {
                        dragMode = DragMode.TRANSFORM
                        // Calculate the centroid (midpoint) of all fingers
                        val currentCentroid =
                            activePointers
                                .asSequence()
                                .map { it.position }
                                .reduce { acc, offset -> acc + offset } / pointerCount.toFloat()

                        if (previousCentroid != Offset.Unspecified) {
                            // 1. Calculate Multi-finger Pan
                            val panDelta = currentCentroid - previousCentroid

                            // 2. Calculate Pinch-to-Zoom (Using distance changes between fingers)
                            val previousDistance =
                                calculateAverageDistance(
                                    activePointers,
                                    isCurrent = false,
                                )
                            val currentDistance =
                                calculateAverageDistance(
                                    activePointers,
                                    isCurrent = true,
                                )

                            onTransform(panDelta, previousDistance, currentDistance)
                        }

                        previousCentroid = currentCentroid
                        // Consume all active changes to mark the gesture as handled
                        event.changes.forEach { it.consume() }
                    }
                }
            } while (event.changes.any { it.pressed })

            if (dragMode.isDrawing) {
                onDragEnd()
            }
        }
    }

// Helper to find the average distance from the center of the touch group
private fun calculateAverageDistance(
    changes: List<PointerInputChange>,
    isCurrent: Boolean,
): Float {
    if (changes.size < 2) return 0F
    val centroid =
        changes
            .asSequence()
            .map { if (isCurrent) it.position else it.previousPosition }
            .reduce { acc, offset -> acc + offset } / changes.size.toFloat()

    return changes
        .sumOf { change ->
            val pos = if (isCurrent) change.position else change.previousPosition
            (pos - centroid).getDistance().toDouble()
        }.toFloat() / changes.size
}

private fun getDragPoints(
    canvasState: CanvasState,
    change: Offset,
    position: PointerInputChange,
): HashSet<Offset> =
    HashSet<Offset>().apply {
        val boxSize =
            canvasState.boxes.values
                .first()
                .width()
                .toInt()

        if (abs(change.x) > boxSize || abs(change.y) > boxSize) {
            val distance =
                sqrt(
                    (position.position.x - position.previousPosition.x).pow(2) +
                        (position.position.y - position.previousPosition.y).pow(
                            2,
                        ),
                )
            for (i in 1..(distance / boxSize).toInt()) {
                val t = (boxSize * i) / distance
                add(
                    Offset(
                        ((1 - t) * (position.previousPosition.x) + (t * position.position.x)),
                        ((1 - t) * (position.previousPosition.y) + (t * position.position.y)),
                    ),
                )
            }
        }

        add(position.position)
    }
