package com.jerry.boxes.ui.boxes.history

import android.graphics.Point
import android.os.Parcelable
import com.jerry.boxes.ui.boxes.SerializableColor
import kotlinx.parcelize.Parcelize

sealed class UserHistory {
    @Parcelize
    data class HistoryClear(
        val data: Map<Point, Map<Long, SerializableColor?>?>
    ) : UserHistory(), Parcelable

    @Parcelize
    data class HistoryDrag(
        val layerId: Long,
        val points: Map<Point, SerializableColor?>
    ) : UserHistory(), Parcelable

    @Parcelize
    data class HistoryTap(
        val color: SerializableColor?,
        val layerId: Long,
        val point: Point
    ) : UserHistory(), Parcelable
}