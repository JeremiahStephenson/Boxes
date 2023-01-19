package com.jerry.boxes.ui.boxes.history

import android.graphics.Point
import android.os.Parcelable
import com.jerry.boxes.ui.boxes.ColorAndShape
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserHistory(
    val layerId: Long,
    val points: Map<Point, ColorAndShape?>
) : Parcelable