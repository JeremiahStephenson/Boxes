package com.jerry.shapes.cache.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = Layer.TABLE_NAME,
    indices = [(Index(value = ["id", "projectId"], unique = true)), Index("projectId")],
    foreignKeys = [
        (
            ForeignKey(
                entity = Project::class,
                parentColumns = ["id"],
                childColumns = ["projectId"],
                onDelete = ForeignKey.CASCADE,
            )
        ),
    ],
)
data class Layer(
    val projectId: Long,
    val index: Int,
    val name: String,
    val on: Boolean,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    companion object {
        const val TABLE_NAME = "layer"
    }
}
