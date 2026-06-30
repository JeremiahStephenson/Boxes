package com.jerry.shapes.ui.boxes.state

import android.graphics.Point
import android.graphics.RectF
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.jerry.shapes.extensions.adjust
import com.jerry.shapes.extensions.findBox
import com.jerry.shapes.extensions.safeLet
import com.jerry.shapes.ui.boxes.state.enums.Direction
import com.jerry.shapes.ui.boxes.state.enums.SelectionType
import kotlin.math.max
import kotlin.math.min

@Stable
class SelectionState private constructor(
    private val topLeft: Point? = null,
    private val bottomRight: Point? = null,
    private val mode: SelectionType? = null,
) {
    constructor(
        topLeft: Point? = null,
        bottomRight: Point? = null,
    ) : this(topLeft = topLeft, bottomRight = bottomRight, mode = null)

    var topLeftState by mutableStateOf(topLeft)
        private set

    var bottomRightState by mutableStateOf(bottomRight)
        private set

    private var modeState: SelectionType? = mode

    fun startSelection(
        area: Offset,
        scale: Float,
        offset: Offset,
        size: Size,
        columns: Int,
        rows: Int,
        topLeft: RectF,
    ) {
        val position =
            area.findBox(
                scale,
                offset,
                size,
                columns,
                rows,
                topLeft,
            )
        when (topLeftState == null || bottomRightState == null) {
            true -> {
                modeState = SelectionType.FREE
                setTopLeft(position)
            }
            else -> {
                position?.let {
                    val tl =
                        Point(
                            min(topLeftState!!.x, bottomRightState!!.x),
                            min(topLeftState!!.y, bottomRightState!!.y),
                        )
                    val br =
                        Point(
                            max(topLeftState!!.x, bottomRightState!!.x),
                            max(topLeftState!!.y, bottomRightState!!.y),
                        )
                    topLeftState = tl
                    bottomRightState = br

                    val nearLeft = it.x >= tl.x - 1 && it.x <= tl.x + 1
                    val nearRight = it.x >= br.x - 1 && it.x <= br.x + 1
                    val nearTop = it.y >= tl.y - 1 && it.y <= tl.y + 1
                    val nearBottom = it.y <= br.y + 1 && it.y >= br.y - 1

                    modeState =
                        when {
                            nearLeft && nearTop -> SelectionType.TOP_LEFT
                            nearLeft && nearBottom -> SelectionType.BOTTOM_LEFT
                            nearRight && nearTop -> SelectionType.TOP_RIGHT
                            nearRight && nearBottom -> SelectionType.BOTTOM_RIGHT
                            nearLeft -> SelectionType.LEFT
                            nearRight -> SelectionType.RIGHT
                            nearTop -> SelectionType.TOP
                            nearBottom -> SelectionType.BOTTOM
                            else -> null
                        }
                }
            }
        }
    }

    fun updateSelection(
        area: Offset,
        scale: Float,
        offset: Offset,
        size: Size,
        columns: Int,
        rows: Int,
        topLeft: RectF,
    ) {
        val position =
            area.findBox(
                scale,
                offset,
                size,
                columns,
                rows,
                topLeft,
            )
        when (modeState) {
            SelectionType.FREE -> setBottomRight(position)
            SelectionType.TOP ->
                safeLet(topLeftState, position) { tl, point ->
                    setTopLeft(Point(tl.x, point.y))
                }
            SelectionType.LEFT ->
                safeLet(topLeftState, position) { tl, point ->
                    setTopLeft(Point(point.x, tl.y))
                }
            SelectionType.BOTTOM ->
                safeLet(bottomRightState, position) { br, point ->
                    setBottomRight(Point(br.x, point.y))
                }
            SelectionType.RIGHT ->
                safeLet(bottomRightState, position) { br, point ->
                    setBottomRight(Point(point.x, br.y))
                }
            SelectionType.TOP_RIGHT ->
                safeLet(
                    topLeftState,
                    bottomRightState,
                    position,
                ) { tl, br, point ->
                    setTopLeft(Point(tl.x, point.y))
                    setBottomRight(Point(point.x, br.y))
                }
            SelectionType.TOP_LEFT ->
                safeLet(topLeftState, position) { _, point ->
                    setTopLeft(Point(point.x, point.y))
                }
            SelectionType.BOTTOM_RIGHT ->
                safeLet(bottomRightState, position) { _, point ->
                    setBottomRight(Point(point.x, point.y))
                }
            SelectionType.BOTTOM_LEFT ->
                safeLet(
                    topLeftState,
                    bottomRightState,
                    position,
                ) { tl, br, point ->
                    setTopLeft(Point(point.x, tl.y))
                    setBottomRight(Point(br.x, point.y))
                }
            else -> {}
        }
    }

    fun checkRowsAndColumns(
        columns: Int,
        rows: Int,
    ) {
        if (topLeftState == null || bottomRightState == null) return
        if (
            max(topLeftState!!.x, bottomRightState!!.x) > (columns - 1) ||
            max(topLeftState!!.y, bottomRightState!!.y) > (rows - 1)
        ) {
            clear()
        }
    }

    private fun setTopLeft(point: Point?) {
        topLeftState = point
    }

    private fun setBottomRight(point: Point?) {
        bottomRightState = point
    }

    fun clear() {
        topLeftState = null
        bottomRightState = null
    }

    fun move(direction: Direction) {
        topLeftState = topLeftState?.adjust(direction)
        bottomRightState = bottomRightState?.adjust(direction)
    }

    companion object {
        val SAVER =
            listSaver<SelectionState, Any?>(
                save = { item ->
                    listOf(item.topLeftState, item.bottomRightState, item.modeState)
                },
                restore = { state ->
                    SelectionState(
                        topLeft = state[0] as? Point,
                        bottomRight = state[1] as? Point,
                        mode = state[2] as? SelectionType,
                    )
                },
            )
    }
}
