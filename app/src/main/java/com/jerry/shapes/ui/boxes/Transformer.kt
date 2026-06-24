package com.jerry.shapes.ui.boxes

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import com.jerry.shapes.ui.boxes.state.TransformerState

@Composable
fun Transformer(
    transformerState: TransformerState,
    content: @Composable (Float, Offset, TransformableState) -> Unit,
) {
    val state =
        rememberTransformableState { _, zoomChange, offsetChange, _ ->
            transformerState.setChanges(zoomChange, offsetChange)
        }
    content(transformerState.scale, transformerState.offset, state)
}
