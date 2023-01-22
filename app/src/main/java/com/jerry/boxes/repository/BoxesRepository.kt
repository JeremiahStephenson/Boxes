package com.jerry.boxes.repository

import android.content.Context
import android.graphics.Point
import androidx.compose.ui.graphics.toArgb
import androidx.room.withTransaction
import com.jerry.boxes.cache.BoxesDao
import com.jerry.boxes.cache.BoxesDatabase
import com.jerry.boxes.cache.data.*
import com.jerry.boxes.ui.boxes.data.ColorAndShape
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.ui.boxes.generateSelections
import com.jerry.boxes.ui.shapes.Shape
import com.jerry.boxes.util.CoroutineContextProvider
import com.jerry.boxes.util.DataResource
import com.jerry.boxes.util.exportCanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant

class BoxesRepository(
    private val boxesDatabase: BoxesDatabase,
    private val boxesDao: BoxesDao,
    private val applicationScope: CoroutineScope,
    private val cc: CoroutineContextProvider,
    private val application: Context
) {
    fun getPixelsFlow(projectId: Long) = boxesDao.getProjectPixelsFlow(projectId)
        .map {
            DataResource.done(generateSelections(it))
        }
        .flowOn(cc.io)

    fun getLayersFlow(projectId: Long) = boxesDao
        .getProjectLayersByProjectId(projectId)
        .map { it.sortedByDescending { layer -> layer.index } }

    fun getLayerHistoryCount(layerId: Long): Flow<Int> {
        return boxesDao.layerHistoryCount(layerId)
    }

    fun getProjectFlowById(projectId: Long) =
        boxesDao.getProjectFlowById(projectId)
            .filterNotNull()

    suspend fun updateProjectShape(projectId: Long, shape: Shape) {
        boxesDao.updateProjectShape(projectId, shape)
    }

    suspend fun updateProjectColor(projectId: Long, color: ColorAndShape) {
        boxesDao.updateProjectColor(projectId, color.color.toArgb())
    }

    suspend fun updateProjectShowGrid(projectId: Long, showGrid: Boolean) {
        boxesDao.updateProjectShowGrid(projectId, showGrid)
    }

    suspend fun updateProjectShowPngBg(projectId: Long, showPngBg: Boolean) {
        boxesDao.updateProjectShowPngBg(projectId, showPngBg)
    }

    private var saveJob: Job? = null
    fun save(
        project: Project,
        boxes: List<Point>? = null,
        selections: Map<Long, Map<Point, ColorAndShape>>,
        layers: Collection<LayerUi>
    ) {
        if (saveJob?.isActive == true) return
        saveJob = applicationScope.launch(cc.io) {
            try {
                export(
                    project = project,
                    fileName = project.id.toString(),
                    imageSize = 200,
                    layers = layers,
                    selections = selections,
                    export = false
                )
            } catch (t: Throwable) {
                // todo log this out
            }
            boxesDatabase.withTransaction {
                saveProject(project.id, boxes, selections)
                layers.forEach {
                    boxesDao.turnOnOrOffLayer(it.on, it.id)
                }
            }
        }
    }

    fun export(
        project: Project,
        fileName: String,
        selections: Map<Long, Map<Point, ColorAndShape>>,
        layers: Collection<LayerUi>,
        imageSize: Int,
        export: Boolean
    ): String? {
        return application.exportCanvas(
            imageSize = imageSize,
            name = fileName,
            rows = project.rows,
            columns = project.columns,
            layers = layers,
            selections = selections,
            export = export
        )
    }

    suspend fun addLayer(
        projectId: Long,
        name: String,
        index: Int,
        selections: Map<Long, Map<Point, ColorAndShape?>?>
    ): Long {
        return boxesDatabase.withTransaction {
            saveProject(projectId, selections = selections)
            boxesDao.insertLayer(Layer(projectId, index, name, true))
        }
    }

    suspend fun updateHistory(layerId: Long, points: Map<Point, ColorAndShape?>) {
        boxesDatabase.withTransaction {
            val index = boxesDao.findMaxIndexForHistory(layerId)
            val historyId = boxesDao.insertHistory(History(layerId, index + 1))
            points.forEach { (point, color) ->
                boxesDao.insertHistoryItem(
                    HistoryItem(
                        historyId,
                        point.x,
                        point.y,
                        color?.color?.toArgb(),
                        color?.shape
                    )
                )
            }
            if (index >= MAX_HISTORY_PER_LAYER) {
                val min = boxesDao.findMinIndexForHistory(layerId)
                boxesDao.cleanHistory(min, layerId)
                boxesDao.updateIndicies(layerId)
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
            ).also {
            history?.let { boxesDao.deleteHistory(it.id) }
        }
    }

    private suspend fun saveProject(
        projectId: Long,
        boxes: List<Point>? = null,
        selections: Map<Long, Map<Point, ColorAndShape?>?>
    ) {
        val now = Instant.now().toEpochMilli()

        val list = selections.flatMap { layer ->
            layer.value?.filterKeys { boxes?.contains(it) ?: true }?.map {
                Pixel(
                    layer.key,
                    it.key.x,
                    it.key.y,
                    it.value!!.color.toArgb(),
                    it.value!!.shape,
                    now
                )
            } ?: emptyList()
        }
        boxesDao.updateProjectTimestamp(projectId)
        boxesDao.insertAllPixels(list)
        boxesDao.deletePixelsFromProject(projectId, now)
    }

    companion object {
        private const val MAX_HISTORY_PER_LAYER = 20
    }
}
