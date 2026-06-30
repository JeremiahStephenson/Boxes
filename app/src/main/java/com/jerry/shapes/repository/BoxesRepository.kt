package com.jerry.shapes.repository

import android.content.Context
import android.graphics.Point
import androidx.compose.ui.graphics.toArgb
import androidx.room.withTransaction
import com.google.firebase.analytics.FirebaseAnalytics
import com.jerry.shapes.cache.BoxesDao
import com.jerry.shapes.cache.BoxesDatabase
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.cache.data.History
import com.jerry.shapes.cache.data.HistoryItem
import com.jerry.shapes.cache.data.Layer
import com.jerry.shapes.cache.data.Pixel
import com.jerry.shapes.cache.data.Project
import com.jerry.shapes.extensions.exportCanvas
import com.jerry.shapes.extensions.logError
import com.jerry.shapes.ui.boxes.data.LayerState
import com.jerry.shapes.ui.boxes.state.CanvasState
import com.jerry.shapes.ui.shapes.Shape
import com.jerry.shapes.util.CoroutineContextProvider
import com.jerry.shapes.util.ExportType
import com.jerry.shapes.util.Resource
import com.jerry.shapes.util.generateSelections
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
                    saveProject(projectId = project.id, canvasState = canvasState, autoSave = autoSave)
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
                    q.value.filterKeys { if (autoSave) true else canvasState.containsPosition(it) }.map {
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

        const val MAX_SIDE_SIZE = 200
    }
}
