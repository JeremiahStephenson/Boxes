package com.jerry.boxes.cache

import androidx.paging.PagingSource
import androidx.room.*
import com.jerry.boxes.cache.data.*
import com.jerry.boxes.ui.shapes.Shape
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxesDao {

    @Query("SELECT * FROM project ORDER BY name COLLATE NOCASE ASC")
    fun findAllProjects(): PagingSource<Int, Project>

    @Transaction
    @Query("SELECT * FROM project WHERE id = :id")
    fun getFullProjectFlowById(id: Long): Flow<FullProject>

    @Transaction
    @Query("SELECT * FROM project WHERE id = :id")
    fun getProjectAndLayersFlowById(id: Long): Flow<ProjectAndLayers>

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

    @Query("DELETE FROM pixel WHERE pixel.layerId IN (SELECT layer.id FROM layer JOIN project ON project.id == layer.projectId AND project.id = :projectId) AND pixel.timeStamp < :timeStamp")
    suspend fun deletePixelsFromProject(projectId: Long, timeStamp: Long)

    @Query("DELETE FROM project WHERE id = :id")
    suspend fun deleteProject(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: History): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(historyItem: HistoryItem)

    @Query("SELECT MAX(`index`) FROM history WHERE layerId = :layerId")
    suspend fun findMaxIndexForHistory(layerId: Long): Int

    @Query("SELECT MIN(`index`) FROM history WHERE layerId = :layerId")
    suspend fun findMinIndexForHistory(layerId: Long): Int

    @Query("DELETE FROM history WHERE `index` <= :index AND layerId = :layerId")
    suspend fun cleanHistory(index: Int, layerId: Long)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistory(id: Long)

    @Query("UPDATE history SET `index` = `index` - 1 WHERE layerId = :layerId")
    suspend fun updateIndicies(layerId: Long)

    @Query("SELECT * FROM history WHERE layerId = :layerId AND `index` = :index")
    suspend fun findMaxHistory(layerId: Long, index: Int): History?

    @Query("SELECT * FROM historyItem WHERE historyId = :historyId")
    suspend fun findAllHistoryItems(historyId: Long): List<HistoryItem>

    @Query("SELECT Count(*) FROM historyItem JOIN history ON history.id == historyItem.historyId JOIN layer ON layer.id == history.layerId AND layer.id == :layerId")
    fun layerHistoryCount(layerId: Long): Flow<Int>
}