package com.jerry.boxes.cache.data

import androidx.room.Embedded
import androidx.room.Relation

data class ProjectAndPixel(
    @Embedded val project: Project,
    @Relation(
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val pixels: List<Pixel>
)