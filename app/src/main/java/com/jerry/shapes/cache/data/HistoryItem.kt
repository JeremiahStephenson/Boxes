package com.jerry.shapes.cache.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jerry.shapes.ui.shapes.Shape

@Entity(
    tableName = HistoryItem.TABLE_NAME,
    indices = [(Index(value = ["id", "historyId", "x", "y"], unique = true)), Index("historyId")],
    foreignKeys = [
        (
            ForeignKey(
                entity = History::class,
                parentColumns = ["id"],
                childColumns = ["historyId"],
                onDelete = ForeignKey.CASCADE
            )
            )
    ]
)
data class HistoryItem(
    val historyId: Long,
    val x: Int,
    val y: Int,
    val color: Int?,
    val shape: Shape?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    companion object {
        const val TABLE_NAME = "historyItem"
    }
}
