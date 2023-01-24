package com.jerry.shapes.ui.boxes.state

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

@Stable
class TransformerState {
    var scale by mutableStateOf(1f)
        private set
    var offset by mutableStateOf(Offset.Zero)
        private set

    private val zoomAnimator = Animatable(0F)
    private val panAnimatorX = Animatable(0F)
    private val panAnimatorY = Animatable(0F)

    fun setChanges(zoomChange: Float, offsetChange: Offset) {
        scale = max(1F, scale * zoomChange)
        offset += (offsetChange / scale)
    }

    fun reset(scope: CoroutineScope) {
        scope.launch {
            zoomAnimator.snapTo(scale)
            zoomAnimator.animateTo(1F) {
                scale = this.value
            }
        }
        scope.launch {
            panAnimatorX.snapTo(offset.x)
            panAnimatorX.animateTo(0F) {
                offset = offset.copy(x = this.value)
            }
        }
        scope.launch {
            panAnimatorY.snapTo(offset.y)
            panAnimatorY.animateTo(0F) {
                offset = offset.copy(y = this.value)
            }
        }
    }
}