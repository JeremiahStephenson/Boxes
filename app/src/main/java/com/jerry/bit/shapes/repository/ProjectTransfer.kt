package com.jerry.bit.shapes.repository

import com.jerry.bit.shapes.cache.data.Layer
import com.jerry.bit.shapes.cache.data.Pixel
import com.jerry.bit.shapes.cache.data.Project
import com.jerry.bit.shapes.ui.shapes.Shape
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProjectTransfer(
    val format: String = FORMAT,
    val version: Int = CURRENT_VERSION,
    val project: TransferProject,
) {
    companion object {
        const val FORMAT = "com.jerry.bit.shapes.project"
        const val CURRENT_VERSION = 1
        const val FILE_EXTENSION = "bitshape.json"
        const val MIME_TYPE = "application/json"
    }
}

@JsonClass(generateAdapter = true)
data class TransferProject(
    val name: String,
    val columns: Int,
    val rows: Int,
    val currentColor: Int,
    val currentShape: String,
    val showGrid: Boolean,
    val showPngBackground: Boolean,
    val layers: List<TransferLayer>,
) {
    fun toProject(
        timestamp: Long,
        id: Long = 0L,
    ) = Project(
        id = id,
        name = name.trim(),
        columns = columns,
        rows = rows,
        currentColor = currentColor,
        currentShape = Shape.valueOf(currentShape),
        showGrid = showGrid,
        showPngBg = showPngBackground,
        timestamp = timestamp,
    )
}

@JsonClass(generateAdapter = true)
data class TransferLayer(
    val index: Int,
    val name: String,
    val visible: Boolean,
    val pixels: List<TransferPixel>,
) {
    fun toLayer(projectId: Long) =
        Layer(
            projectId = projectId,
            index = index,
            name = name.trim(),
            on = visible,
        )
}

@JsonClass(generateAdapter = true)
data class TransferPixel(
    val x: Int,
    val y: Int,
    val color: Int,
    val shape: String,
) {
    fun toPixel(
        layerId: Long,
        timestamp: Long,
    ) = Pixel(
        layerId = layerId,
        x = x,
        y = y,
        color = color,
        shape = Shape.valueOf(shape),
        timestamp = timestamp,
    )
}
