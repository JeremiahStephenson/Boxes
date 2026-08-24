package com.jerry.bit.shapes.ui.boxes.state

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

@Stable
class TransformerState private constructor(
    scale: Float,
    offset: Offset,
) {
    constructor() : this(scale = 1F, offset = Offset.Zero)

    var scaleState by mutableFloatStateOf(scale)
        private set
    var offsetState by mutableStateOf(offset)
        private set

    private val zoomAnimator = Animatable(0F)
    private val panAnimatorX = Animatable(0F)
    private val panAnimatorY = Animatable(0F)

    fun setChanges(
        zoomChange: Float,
        offsetChange: Offset,
    ) {
        scaleState = max(1F, scaleState * zoomChange)
        offsetState += (offsetChange / scaleState)
    }

    fun reset(scope: CoroutineScope) {
        scope.launch {
            zoomAnimator.snapTo(scaleState)
            zoomAnimator.animateTo(1F) {
                scaleState = this.value
            }
        }
        scope.launch {
            panAnimatorX.snapTo(offsetState.x)
            panAnimatorX.animateTo(0F) {
                offsetState = offsetState.copy(x = this.value)
            }
        }
        scope.launch {
            panAnimatorY.snapTo(offsetState.y)
            panAnimatorY.animateTo(0F) {
                offsetState = offsetState.copy(y = this.value)
            }
        }
    }

    companion object {
        val SAVER =
            listSaver<TransformerState, Any>(
                save = { item ->
                    listOf(item.scaleState, item.offsetState.packedValue)
                },
                restore = { state ->
                    TransformerState(
                        scale = state[0] as Float,
                        offset = Offset(state[1] as Long),
                    )
                },
            )
    }
}
