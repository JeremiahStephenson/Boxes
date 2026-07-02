package com.jerry.shapes.util

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.jerry.shapes.cache.data.ColorAndShape
import com.jerry.shapes.cache.data.LayerAndPixel
import com.jerry.shapes.cache.data.Pixel
import kotlin.math.floor

fun generateSelections(pixels: List<Pixel>): SnapshotStateMap<Long, SnapshotStateMap<Point, SnapshotStateMap<Point, ColorAndShape>>> =
    SnapshotStateMap<Long, SnapshotStateMap<Point, SnapshotStateMap<Point, ColorAndShape>>>().apply {
        putAll(
            pixels
                .groupBy { it.layerId }
                .mapValues {
                    SnapshotStateMap<Point, SnapshotStateMap<Point, ColorAndShape>>().apply {
                        putAll(
                            it.value
                                .groupBy {
                                    Point(
                                        floor(it.x.toFloat() / QUADRANT_SIZE).toInt(),
                                        floor(it.y.toFloat() / QUADRANT_SIZE).toInt(),
                                    )
                                }.mapValues {
                                    it.value.associateTo(SnapshotStateMap()) {
                                        Point(it.x, it.y) to it.asColorAndShape
                                    }
                                },
                        )
                    }
                },
        )
    }

fun generateSelectionsMap(layers: List<LayerAndPixel>): Map<Long, Map<Point, ColorAndShape>> =
    layers
        .flatMap {
            it.pixels
        }.groupBy {
            it.layerId
        }.mapValues {
            it.value.associateTo(SnapshotStateMap()) { pixel ->
                Point(pixel.x, pixel.y) to pixel.asColorAndShape
            }
        }

const val QUADRANT_SIZE = 50F
const val GRID_ODD_ALPHA = 0.5F
