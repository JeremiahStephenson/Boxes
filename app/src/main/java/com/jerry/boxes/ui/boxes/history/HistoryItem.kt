package com.jerry.boxes.ui.boxes.history

import android.graphics.Point
import android.os.Parcelable
import com.jerry.boxes.ui.boxes.SerializableColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryItem(
    val color: SerializableColor?,
    val layerId: Long,
    val point: Point
) : History, Parcelable