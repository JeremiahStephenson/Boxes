package com.jerry.shapes.cache.data

import androidx.room.Embedded
import androidx.room.Relation

data class LayerAndPixel(
    @Embedded val layer: Layer,
    @Relation(
        parentColumn = "id",
        entityColumn = "layerId"
    )
    val pixels: List<Pixel>
)