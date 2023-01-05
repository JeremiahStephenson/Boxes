package com.jerry.boxes.cache.data

import androidx.room.Embedded
import androidx.room.Relation

data class ProjectAndLayer(
    @Embedded val project: Project,
    @Relation(
        entity = Layer::class,
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val layers: List<LayerAndPixel>
)