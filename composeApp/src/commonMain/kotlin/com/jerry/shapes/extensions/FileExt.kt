package com.jerry.shapes.extensions

import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.cache.data.Project
import com.jerry.shapes.ui.boxes.data.LayerState
import com.jerry.shapes.util.ExportType
import com.jerry.shapes.platform.AppContext
import com.jerry.shapes.platform.PlatformBitmap
import com.jerry.shapes.util.Point

expect fun AppContext.exportCanvas(
    name: String,
    exportType: ExportType,
    rows: Int,
    columns: Int,
    imageSize: Int,
    layers: Collection<LayerState>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
): String?

expect fun Project.thumbnailUrl(context: coil3.PlatformContext): String

expect fun generateBitmap(
    rows: Int,
    columns: Int,
    imageSize: Int,
    layers: Collection<LayerState>,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
): PlatformBitmap

expect fun generateBitmap(
    rows: Int,
    columns: Int,
    imageSize: Int,
    layerId: Long,
    selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
): PlatformBitmap