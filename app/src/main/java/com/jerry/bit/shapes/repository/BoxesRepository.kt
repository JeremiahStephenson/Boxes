package com.jerry.bit.shapes.repository

import android.content.Context
import android.graphics.Point
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import androidx.room.withTransaction
import com.google.firebase.analytics.FirebaseAnalytics
import com.jerry.bit.shapes.cache.BoxesDao
import com.jerry.bit.shapes.cache.BoxesDatabase
import com.jerry.bit.shapes.cache.data.ColorAndShape
import com.jerry.bit.shapes.cache.data.History
import com.jerry.bit.shapes.cache.data.HistoryItem
import com.jerry.bit.shapes.cache.data.Layer
import com.jerry.bit.shapes.cache.data.Pixel
import com.jerry.bit.shapes.cache.data.Project
import com.jerry.bit.shapes.extensions.exportCanvas
import com.jerry.bit.shapes.extensions.logError
import com.jerry.bit.shapes.ui.boxes.data.LayerState
import com.jerry.bit.shapes.ui.boxes.state.CanvasState
import com.jerry.bit.shapes.ui.shapes.Shape
import com.jerry.bit.shapes.util.CoroutineContextProvider
import com.jerry.bit.shapes.util.ExportType
import com.jerry.bit.shapes.util.Resource
import com.jerry.bit.shapes.util.generateSelections
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.time.Clock

class BoxesRepository(
    private val boxesDatabase: BoxesDatabase,
    private val boxesDao: BoxesDao,
    private val applicationScope: CoroutineScope,
    private val cc: CoroutineContextProvider,
    private val application: Context,
    private val analytics: FirebaseAnalytics,
) {
    private val projectTransferAdapter =
        Moshi
            .Builder()
            .build()
            .adapter(ProjectTransfer::class.java)
            .indent("  ")

    fun getPixelsFlow(projectId: Long) =
        boxesDao
            .getProjectPixelsFlow(projectId)
            .map {
                Resource.done(generateSelections(it))
            }.flowOn(cc.io)

    fun getLayersFlow(projectId: Long) =
        boxesDao
            .getProjectLayersByProjectId(projectId)
            .map { it.sortedByDescending { layer -> layer.index } }

    fun getLayerHistoryCount(layerId: Long): Flow<Int> = boxesDao.layerHistoryCount(layerId)

    fun getProjectFlowById(projectId: Long) =
        boxesDao
            .getProjectFlowById(projectId)
            .filterNotNull()

    suspend fun exportProject(
        project: Project,
        layers: Collection<LayerState>,
        selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
        destinationDocument: Uri,
    ): Uri {
        val transfer =
            ProjectTransfer(
                project =
                    TransferProject(
                        name = project.name,
                        columns = project.columns,
                        rows = project.rows,
                        currentColor = project.currentColor,
                        currentShape = project.currentShape.name,
                        showGrid = project.showGrid,
                        showPngBackground = project.showPngBg,
                        layers =
                            layers.sortedBy { it.index }.map { layer ->
                                TransferLayer(
                                    index = layer.index,
                                    name = layer.name,
                                    visible = layer.on,
                                    pixels =
                                        selections[layer.id]
                                            .orEmpty()
                                            .values
                                            .flatMap { it.entries }
                                            .sortedWith(compareBy({ it.key.y }, { it.key.x }))
                                            .map { (point, value) ->
                                                TransferPixel(
                                                    point.x,
                                                    point.y,
                                                    value.color.toArgb(),
                                                    value.shape.name
                                                )
                                            },
                                )
                            },
                    ),
            )
        val resolver = application.contentResolver
        resolver.openOutputStream(destinationDocument, "w")?.bufferedWriter()?.use {
            it.write(projectTransferAdapter.toJson(transfer))
        } ?: error("The project file could not be opened for writing")
        return destinationDocument
    }

    suspend fun importProject(source: Uri): Long {
        val resolver = application.contentResolver
        val length = resolver.openAssetFileDescriptor(source, "r")?.use { it.length } ?: -1L
        require(length == -1L || length <= MAX_PROJECT_FILE_BYTES) { "That project file is too large" }
        val json =
            resolver.openInputStream(source)?.bufferedReader()?.use { reader ->
                reader.readText().also {
                    require(it.length <= MAX_PROJECT_FILE_CHARS) { "That project file is too large" }
                }
            } ?: error("The project file could not be opened")
        val transfer = projectTransferAdapter.fromJson(json) ?: error("The project file is empty")
        validate(transfer)
        val imported = transfer.project
        val now = Clock.System.now().toEpochMilliseconds()
        val importedLayers = mutableListOf<LayerState>()
        val importedPixels = mutableListOf<Pixel>()
        val projectId =
            boxesDatabase.withTransaction {
                val projectId =
                    boxesDao.insertProject(
                        Project(
                            name = imported.name.trim(),
                            columns = imported.columns,
                            rows = imported.rows,
                            currentColor = imported.currentColor,
                            currentShape = Shape.valueOf(imported.currentShape),
                            showGrid = imported.showGrid,
                            showPngBg = imported.showPngBackground,
                            timestamp = now,
                        ),
                    )
                imported.layers.forEach { layer ->
                    val layerId =
                        boxesDao.insertLayer(
                            Layer(
                                projectId = projectId,
                                index = layer.index,
                                name = layer.name.trim(),
                                on = layer.visible,
                            ),
                        )
                    importedLayers +=
                        LayerState(
                            id = layerId,
                            projectId = projectId,
                            index = layer.index,
                            name = layer.name.trim(),
                            on = layer.visible,
                            selected = false,
                            visibilityEnabled = true,
                            showControls = imported.layers.size > 1,
                        )
                    val pixels =
                        layer.pixels.map { pixel ->
                            Pixel(
                                layerId = layerId,
                                x = pixel.x,
                                y = pixel.y,
                                color = pixel.color,
                                shape = Shape.valueOf(pixel.shape),
                                timestamp = now,
                            )
                        }
                    boxesDao.insertAllPixels(pixels)
                    importedPixels += pixels
                }
                projectId
            }
        runCatching {
            export(
                project =
                    Project(
                        id = projectId,
                        name = imported.name.trim(),
                        columns = imported.columns,
                        rows = imported.rows,
                        currentColor = imported.currentColor,
                        currentShape = Shape.valueOf(imported.currentShape),
                        showGrid = imported.showGrid,
                        showPngBg = imported.showPngBackground,
                        timestamp = now,
                    ),
                fileName = projectId.toString(),
                selections = generateSelections(importedPixels),
                layers = importedLayers,
                imageSize = THUMBNAIL_SIZE,
                exportType = ExportType.THUMBNAIL,
            )
        }.onFailure(analytics::logError)
        return projectId
    }

    private fun validate(transfer: ProjectTransfer) {
        require(transfer.format == ProjectTransfer.FORMAT) { "This is not a BitShape project file" }
        require(transfer.version == ProjectTransfer.CURRENT_VERSION) { "This project file version is not supported" }
        val project = transfer.project
        require(project.name.isNotBlank() && project.name.length <= 100) { "The project name is invalid" }
        require(project.columns in 1..MAX_SIDE_SIZE && project.rows in 1..MAX_SIDE_SIZE) { "The canvas size is invalid" }
        require(project.layers.size in 1..MAX_LAYERS) { "The project must contain between 1 and $MAX_LAYERS layers" }
        require(project.layers.any { it.visible }) { "At least one layer must be visible" }
        require(
            project.layers
                .map { it.index }
                .distinct()
                .size == project.layers.size,
        ) { "Layer order values must be unique" }
        Shape.valueOf(project.currentShape)
        project.layers.forEach { layer ->
            require(layer.name.isNotBlank() && layer.name.length <= 100) { "A layer name is invalid" }
            require(layer.pixels.size <= project.columns * project.rows) { "A layer contains too many pixels" }
            require(
                layer.pixels
                    .map { it.x to it.y }
                    .distinct()
                    .size == layer.pixels.size,
            ) { "A layer contains duplicate pixels" }
            layer.pixels.forEach { pixel ->
                require(pixel.x in 0 until project.columns && pixel.y in 0 until project.rows) { "A pixel is outside the canvas" }
                Shape.valueOf(pixel.shape)
            }
        }
    }

    suspend fun updateProjectShape(
        projectId: Long,
        shape: Shape,
    ) {
        boxesDao.updateProjectShape(projectId, shape)
    }

    suspend fun updateProjectColor(
        projectId: Long,
        color: ColorAndShape,
    ) {
        boxesDao.updateProjectColor(projectId, color.color.toArgb())
    }

    suspend fun updateProjectShowGrid(
        projectId: Long,
        showGrid: Boolean,
    ) {
        boxesDao.updateProjectShowGrid(projectId, showGrid)
    }

    suspend fun updateProjectShowPngBg(
        projectId: Long,
        showPngBg: Boolean,
    ) {
        boxesDao.updateProjectShowPngBg(projectId, showPngBg)
    }

    private var saveJob: Job? = null

    fun save(
        project: Project,
        canvasState: CanvasState,
        autoSave: Boolean,
    ) {
        if (saveJob?.isActive == true) return
        saveJob =
            applicationScope.launch(cc.io) {
                runCatching {
                    export(
                        project = project,
                        fileName = project.id.toString(),
                        imageSize = 200,
                        layers = canvasState.layers,
                        selections = canvasState.selections,
                        exportType = ExportType.THUMBNAIL,
                    )
                }.onFailure { error ->
                    analytics.logError(error)
                }
                boxesDatabase.withTransaction {
                    saveProject(
                        projectId = project.id,
                        canvasState = canvasState,
                        autoSave = autoSave
                    )
                    canvasState.layers.forEach {
                        boxesDao.turnOnOrOffLayer(it.on, it.id)
                    }
                }
            }
    }

    fun export(
        project: Project,
        fileName: String,
        selections: Map<Long, Map<Point, Map<Point, ColorAndShape>>>,
        layers: Collection<LayerState>,
        imageSize: Int,
        exportType: ExportType,
    ): String? =
        application.exportCanvas(
            imageSize = imageSize,
            name = fileName,
            rows = project.rows,
            columns = project.columns,
            layers = layers,
            selections = selections,
            exportType = exportType,
        )

    suspend fun addLayer(
        projectId: Long,
        name: String,
        index: Int,
        canvasState: CanvasState,
    ): Long =
        boxesDatabase.withTransaction {
            saveProject(projectId = projectId, canvasState = canvasState, autoSave = false)
            boxesDao.insertLayer(
                Layer(
                    projectId = projectId,
                    index = index,
                    name = name,
                    on = true,
                ),
            )
        }

    suspend fun updateHistory(
        layerId: Long,
        points: Map<Point, ColorAndShape?>,
    ) {
        boxesDatabase.withTransaction {
            val index = boxesDao.findMaxIndexForHistory(layerId)
            val historyId =
                boxesDao.insertHistory(
                    History(layerId, index + 1, Clock.System.now().toEpochMilliseconds()),
                )
            boxesDao.insertHistoryItems(
                points.map { (point, color) ->
                    HistoryItem(
                        historyId,
                        point.x,
                        point.y,
                        color?.color?.toArgb(),
                        color?.shape,
                    )
                },
            )
            if (index >= MAX_HISTORY_PER_LAYER) {
                val diff = max(index - MAX_HISTORY_PER_LAYER, 1)
                boxesDao.cleanHistory(diff, layerId)
                boxesDao.updateIndicies(layerId, diff)
            }
        }
    }

    suspend fun getLastHistoryItem(layerId: Long): List<HistoryItem> {
        val max = boxesDao.findMaxIndexForHistory(layerId)
        val history = boxesDao.findMaxHistory(layerId, max)
        return (
                history?.let {
                    boxesDao.findAllHistoryItems(history.id)
                } ?: emptyList()
                ).also { history?.let { boxesDao.deleteHistory(it.id) } }
    }

    suspend fun deleteInvalidHistoryItems() {
        boxesDao.cleanInvalidHistory()
    }

    private suspend fun saveProject(
        projectId: Long,
        canvasState: CanvasState,
        autoSave: Boolean,
    ) {
        val now = Clock.System.now().toEpochMilliseconds()
        val list =
            canvasState.selections.flatMap { (layer, quad) ->
                quad.flatMap { q ->
                    q.value.filterKeys { if (autoSave) true else canvasState.containsPosition(it) }
                        .map {
                            Pixel(
                                layerId = layer,
                                x = it.key.x,
                                y = it.key.y,
                                color = it.value.color.toArgb(),
                                shape = it.value.shape,
                                timestamp = now,
                            )
                        }
                }
            }
        boxesDao.updateProjectTimestamp(projectId)
        boxesDao.insertAllPixels(list)
        boxesDao.deletePixelsFromProject(projectId, now)
    }

    companion object {
        private const val MAX_HISTORY_PER_LAYER = 20
        private const val MAX_LAYERS = 10
        private const val MAX_PROJECT_FILE_BYTES = 20L * 1024L * 1024L
        private const val MAX_PROJECT_FILE_CHARS = 20 * 1024 * 1024
        private const val THUMBNAIL_SIZE = 200

        const val MAX_SIDE_SIZE = 200
    }
}
