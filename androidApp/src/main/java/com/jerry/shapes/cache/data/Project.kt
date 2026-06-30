package com.jerry.shapes.cache.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.jerry.shapes.ui.shapes.Shape
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = Project.TABLE_NAME,
)
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val columns: Int,
    val rows: Int,
    val currentColor: Int,
    val currentShape: Shape,
    val showGrid: Boolean,
    val showPngBg: Boolean,
    val timestamp: Long,
) : Parcelable {
    companion object {
        const val TABLE_NAME = "project"
    }

    @Ignore
    @IgnoredOnParcel
    private var _colorAndShape: ColorAndShape? = null
    val colorAndShape: ColorAndShape
        get() =
            _colorAndShape ?: ColorAndShape(currentColor).also { _colorAndShape = it }
}
