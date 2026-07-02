package com.jerry.shapes.extensions

import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.ui.boxes.data.LayerState
import com.jerry.shapes.util.ExportType
import com.jerry.shapes.util.PlatformContext
import com.jerry.shapes.util.Point

actual fun PlatformContext?.exportCanvas(
    name: String,
    exportType: ExportType,
    rows: Int,
    columns: Int,
    imageSize: Int,
    layers: Collection<LayerState>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
): String? {
    // TODO: Implement desktop canvas export
    return null
}
