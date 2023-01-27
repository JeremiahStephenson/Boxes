package com.jerry.shapes.cache

import androidx.room.*
import com.jerry.shapes.cache.data.*
import com.jerry.shapes.ui.shapes.Shape
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface BoxesDao {

    @Query("SELECT * FROM project ORDER BY name COLLATE NOCASE ASC")
    fun findAllProjects(): Flow<List<Project>>

    @Transaction
    @Query("SELECT * FROM project WHERE id = :id")
    fun getFullProjectFlowById(id: Long): Flow<FullProject>

    @Transaction
    @Query("SELECT * FROM project WHERE id = :id")
    fun getProjectAndLayersFlowById(id: Long): Flow<ProjectAndLayers>

    @Query("SELECT * FROM layer WHERE projectId = :projectId")
    fun getProjectLayersByProjectId(projectId: Long): Flow<List<Layer>>

    @Query("SELECT * FROM pixel JOIN layer ON layer.id == pixel.layerId WHERE layer.projectId = :projectId")
    fun getProjectPixelsFlow(projectId: Long): Flow<List<Pixel>>

    @Query("SELECT * FROM project WHERE id = :id")
    fun getProjectFlowById(id: Long): Flow<Project>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLayer(layer: Layer): Long

    @Query("UPDATE layer SET `index` = :index WHERE id = :layerId")
    suspend fun setLayerIndex(layerId: Long, index: Int)

    @Query("UPDATE layer SET `on` = :on WHERE id = :layerId")
    suspend fun turnOnOrOffLayer(on: Boolean, layerId: Long)

    @Query("DELETE FROM layer WHERE id = :layerId")
    suspend fun deleteLayer(layerId: Long)

    @Query("UPDATE layer SET name = :name WHERE id = :layerId")
    suspend fun setLayerName(layerId: Long, name: String)

    @Query("UPDATE project SET name = :name, rows = :rows, columns = :columns WHERE id = :id")
    suspend fun updateProject(name: String, columns: Int, rows: Int, id: Long)

    @Query("UPDATE project SET timestamp = :timeStamp WHERE id = :id")
    suspend fun updateProjectTimestamp(id: Long, timeStamp: Long = Instant.now().toEpochMilli())

    @Query("UPDATE project SET currentColor = :color WHERE id = :id")
    suspend fun updateProjectColor(id: Long, color: Int)

    @Query("UPDATE project SET currentShape = :shape WHERE id = :id")
    suspend fun updateProjectShape(id: Long, shape: Shape)

    @Query("UPDATE project SET showGrid = :showGrid WHERE id = :id")
    suspend fun updateProjectShowGrid(id: Long, showGrid: Boolean)

    @Query("UPDATE project SET showPngBg = :showPngBg WHERE id = :id")
    suspend fun updateProjectShowPngBg(id: Long, showPngBg: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPixels(pixelList: List<Pixel>)

    @Query("DELETE FROM pixel WHERE pixel.layerId IN (SELECT layer.id FROM layer JOIN project ON project.id == layer.projectId AND project.id = :projectId) AND pixel.timestamp < :timestamp")
    suspend fun deletePixelsFromProject(projectId: Long, timestamp: Long)

    @Query("DELETE FROM project WHERE id = :id")
    suspend fun deleteProject(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: History): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(historyItem: HistoryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItems(historyItems: List<HistoryItem>)

    @Query("SELECT MAX(`index`) FROM history WHERE layerId = :layerId")
    suspend fun findMaxIndexForHistory(layerId: Long): Int

    @Query("SELECT MIN(`index`) FROM history WHERE layerId = :layerId")
    suspend fun findMinIndexForHistory(layerId: Long): Int

    @Query("DELETE FROM history WHERE `index` <= :index AND layerId = :layerId")
    suspend fun cleanHistory(index: Int, layerId: Long)

    @Query("DELETE FROM history WHERE history.layerId IN (SELECT layer.id FROM layer JOIN project ON project.id == layer.projectId AND history.timestamp > project.timestamp)")
    suspend fun cleanInvalidHistory()

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistory(id: Long)

    @Query("UPDATE history SET `index` = `index` - :amount WHERE layerId = :layerId")
    suspend fun updateIndicies(layerId: Long, amount: Int)

    @Query("SELECT * FROM history WHERE layerId = :layerId AND `index` = :index")
    suspend fun findMaxHistory(layerId: Long, index: Int): History?

    @Query("SELECT * FROM historyItem WHERE historyId = :historyId")
    suspend fun findAllHistoryItems(historyId: Long): List<HistoryItem>

    @Query("SELECT Count(*) FROM history JOIN layer ON layer.id == history.layerId AND layer.id == :layerId")
    fun layerHistoryCount(layerId: Long): Flow<Int>
}
