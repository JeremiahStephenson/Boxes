package com.jerry.shapes.cache.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jerry.shapes.ui.shapes.Shape

@Entity(
    tableName = Pixel.TABLE_NAME,
    indices = [(Index(value = ["layerId", "x", "y"], unique = true))],
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
data class Pixel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val layerId: Long,
    val x: Int,
    val y: Int,
    val color: Int,
    val shape: Shape,
    val timestamp: Long,
) {
    companion object {
        const val TABLE_NAME = "pixel"
    }

    val asColorAndShape get() = ColorAndShape(color, shape)
}
