package com.jerry.bit.shapes.ui.boxes.state

import com.jerry.bit.shapes.ui.boxes.state.enums.ActiveTool
import com.jerry.bit.shapes.ui.boxes.state.enums.TapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ButtonsStateTest {
    @Test
    fun selectingEachToolReplacesThePreviousTool() {
        val state = ButtonsState()

        state.setTapType(TapType.FILL)
        assertEquals(ActiveTool.FILL, state.activeToolState)

        state.toggleEraserSelected()
        assertEquals(ActiveTool.ERASER, state.activeToolState)
        assertEquals(TapType.TAP, state.tapTypeState)

        state.toggleSelectTool()
        assertEquals(ActiveTool.SELECT, state.activeToolState)
        assertFalse(state.eraserSelectedState)

        state.setTapType(TapType.PICKER)
        assertEquals(ActiveTool.EYEDROPPER, state.activeToolState)
        assertFalse(state.selectToolSelectedState)
    }

    @Test
    fun turningOffContextualToolReturnsToDraw() {
        val state = ButtonsState(eraserSelected = true)

        assertTrue(state.eraserSelectedState)
        state.eraserSelectedState = false

        assertEquals(ActiveTool.DRAW, state.activeToolState)
        assertEquals(TapType.TAP, state.tapTypeState)
    }
}
