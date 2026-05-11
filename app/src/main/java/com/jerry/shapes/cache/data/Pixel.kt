package com.jerry.shapes.cache.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jerry.shapes.ui.shapes.Shape
import java.io.Serializable

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
    val layerId: Long,
    val x: Int,
    val y: Int,
    val color: Int,
    val shape: Shape,
    val timestamp: Long,
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    companion object {
        const val TABLE_NAME = "pixel"
    }

    val asColorAndShape get() = ColorAndShape(Color(color), shape)
}
