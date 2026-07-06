package com.jerry.shapes.util

import android.graphics.Canvas

actual abstract class CanvasExport actual constructor()
class AndroidPlatformCanvasExport(val canvas: Canvas) : CanvasExport()

fun CanvasExport.expectPlatformCanvas(block: Canvas.() -> Unit) =
    (this as? AndroidPlatformCanvasExport)?.canvas?.block() ?: Unit