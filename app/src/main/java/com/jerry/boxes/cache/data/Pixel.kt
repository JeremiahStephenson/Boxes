package com.jerry.boxes.cache.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = Pixel.TABLE_NAME,
    indices = [(Index(value = ["id", "projectId"], unique = true))],
    foreignKeys = [(ForeignKey(
        entity = Project::class,
        parentColumns = ["id"],
        childColumns = ["projectId"],
        onDelete = ForeignKey.CASCADE
    ))]
)
data class Pixel(
    val projectId: Long,
    val x: Int,
    val y: Int,
    val hue: Float,
    val saturation: Float,
    val value: Float,
    val alpha: Float
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    companion object {
        const val TABLE_NAME = "pixel"
    }
}