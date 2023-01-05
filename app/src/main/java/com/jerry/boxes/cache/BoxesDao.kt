package com.jerry.boxes.cache

import androidx.paging.PagingSource
import androidx.room.*
import com.jerry.boxes.cache.data.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxesDao {

    @Query("SELECT * FROM project")
    fun findAllProjects(): PagingSource<Int, Project>

    @Transaction
    @Query("SELECT * FROM project WHERE id = :id")
    fun getFullProjectFlowById(id: Long): Flow<ProjectAndLayer>

    @Query("SELECT * FROM project WHERE id = :id")
    fun getProjectFlowById(id: Long): Flow<Project>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLayer(layer: Layer)

    @Query("UPDATE layer SET `on` = :on WHERE id = :layerId")
    suspend fun turnOnOrOffLayer(on: Boolean, layerId: Long)

    @Query("UPDATE project SET name = :name, rows = :rows, columns = :columns WHERE id = :id")
    suspend fun updateProject(name: String, columns: Int, rows: Int, id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPixels(pixelList: List<Pixel>)

    @Query("DELETE FROM pixel WHERE pixel.layerId IN (SELECT layer.id FROM layer JOIN project ON project.id == layer.projectId AND project.id = :projectId) AND pixel.timeStamp < :timeStamp")
    suspend fun deletePixelsFromProject(projectId: Long, timeStamp: Long)

    @Query("DELETE FROM project WHERE id = :id")
    suspend fun deleteProject(id: Long)
}