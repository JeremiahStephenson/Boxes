package com.jerry.boxes.cache

import androidx.paging.PagingSource
import androidx.room.*
import com.jerry.boxes.cache.data.Pixel
import com.jerry.boxes.cache.data.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxesDao {

    @Query("SELECT * FROM project")
    fun findAllProjects(): PagingSource<Int, Project>

    @Transaction
    @Query("SELECT * FROM pixel WHERE projectId = :projectId")
    fun getPixelsFlowByProjectId(projectId: Long): Flow<List<Pixel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPixels(pixelList: List<Pixel>)

    @Query("DELETE FROM pixel WHERE projectId = :id")
    suspend fun deletePixelsFromProject(id: Long)

}