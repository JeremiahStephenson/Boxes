package com.jerry.boxes.cache.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jerry.boxes.ui.boxes.ColorAndShape
import com.jerry.boxes.ui.shapes.Shape
import java.io.Serializable

@Entity(
    tableName = Project.TABLE_NAME
)
data class Project(
    val name: String,
    val columns: Int,
    val rows: Int,
    val currentColor: Int,
    val currentShape: Shape,
    val showGrid: Boolean,
    val showPngBg: Boolean
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    companion object {
        const val TABLE_NAME = "project"
    }

    @Transient
    private var _color: ColorAndShape? = null
    val colorAndShape: ColorAndShape
        get() =
        _color ?: ColorAndShape(Color(currentColor)).also { _color = it }
}