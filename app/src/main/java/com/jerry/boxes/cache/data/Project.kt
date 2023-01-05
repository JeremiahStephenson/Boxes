package com.jerry.boxes.cache.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = Project.TABLE_NAME
)
data class Project(
    val name: String,
    val columns: Int,
    val rows: Int
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    companion object {
        const val TABLE_NAME = "project"
    }
}