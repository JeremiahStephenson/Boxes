package com.jerry.boxes.cache

import androidx.paging.PagingSource
import androidx.room.*
import com.jerry.boxes.cache.data.Pixel
import com.jerry.boxes.cache.data.Project
import com.jerry.boxes.cache.data.ProjectAndPixel
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxesDao {

    @Query("SELECT * FROM project")
    fun findAllProjects(): PagingSource<Int, Project>

    @Transaction
    @Query("SELECT * FROM project WHERE id = :id")
    fun getFullProjectFlowById(id: Long): Flow<ProjectAndPixel>

    @Query("SELECT * FROM project WHERE id = :id")
    fun getProjectFlowById(id: Long): Flow<Project>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Query("UPDATE project SET name = :name, rows = :rows, columns = :columns WHERE id = :id")
    suspend fun updateProject(name: String, columns: Int, rows: Int, id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPixels(pixelList: List<Pixel>)

    @Query("DELETE FROM pixel WHERE projectId = :id AND timeStamp < :timeStamp")
    suspend fun deletePixelsFromProject(id: Long, timeStamp: Long)

    @Query("DELETE FROM project WHERE id = :id")
    suspend fun deleteProject(id: Long)
}