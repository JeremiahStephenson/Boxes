package com.jerry.bit.shapes.repository

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
)

@JsonClass(generateAdapter = true)
data class TransferLayer(
    val index: Int,
    val name: String,
    val visible: Boolean,
    val pixels: List<TransferPixel>,
)

@JsonClass(generateAdapter = true)
data class TransferPixel(
    val x: Int,
    val y: Int,
    val color: Int,
    val shape: String,
)
