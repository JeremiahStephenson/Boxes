package com.jerry.bit.shapes.cache.data

import androidx.room.Embedded
import androidx.room.Relation

data class FullProject(
    @Embedded val project: Project,
    @Relation(
        entity = Layer::class,
        parentColumn = "id",
        entityColumn = "projectId",
    )
    val layers: List<LayerAndPixel>,
)
