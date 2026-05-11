package com.jerry.shapes.cache.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = History.TABLE_NAME,
    indices = [(Index(value = ["id", "layerId", "index"], unique = true)), Index("layerId")],
    foreignKeys = [
        (
            ForeignKey(
                entity = Layer::class,
                parentColumns = ["id"],
                childColumns = ["layerId"],
                onDelete = ForeignKey.CASCADE,
            )
        ),
    ],
)
data class History(
    val layerId: Long,
    val index: Int,
    val timestamp: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    companion object {
        const val TABLE_NAME = "history"
    }
}
