package com.jerry.shapes.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.Density

class ArrangementLastItem : Arrangement.Vertical {
    override fun Density.arrange(
        totalSize: Int,
        sizes: IntArray,
        outPositions: IntArray
    ) {
        var currentOffset = 0
        sizes.forEachIndexed { index, size ->
            if (index == sizes.lastIndex) {
                outPositions[index] = totalSize - size
            } else {
                outPositions[index] = currentOffset
                currentOffset += size
            }
        }
    }
}
