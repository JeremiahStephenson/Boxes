package com.jerry.boxes.ui.boxes.history

import android.graphics.Point
import android.os.Parcelable
import com.jerry.boxes.ui.boxes.SerializableColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryClearItem(
    val data: Map<Point, Map<Long, SerializableColor?>?>
) : History, Parcelable