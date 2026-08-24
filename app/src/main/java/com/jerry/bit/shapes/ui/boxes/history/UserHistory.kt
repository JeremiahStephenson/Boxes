package com.jerry.bit.shapes.ui.boxes.history

import android.graphics.Point
import android.os.Parcelable
import com.jerry.bit.shapes.cache.data.ColorAndShape
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserHistory(
    val layerId: Long,
    val points: Map<Point, ColorAndShape?>,
) : Parcelable
