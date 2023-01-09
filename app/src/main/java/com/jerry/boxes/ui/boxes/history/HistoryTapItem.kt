package com.jerry.boxes.ui.boxes.history

import android.graphics.Point
import android.os.Parcelable
import com.jerry.boxes.ui.boxes.SerializableColor
import kotlinx.parcelize.Parcelize

sealed class HistoryItem {
    @Parcelize
    data class HistoryClearItem(
        val data: Map<Point, Map<Long, SerializableColor?>?>
    ) : HistoryItem(), Parcelable

    @Parcelize
    data class HistoryDragItem(
        val layerId: Long,
        val points: Map<Point, SerializableColor?>
    ) : HistoryItem(), Parcelable

    @Parcelize
    data class HistoryTapItem(
        val color: SerializableColor?,
        val layerId: Long,
        val point: Point
    ) : HistoryItem(), Parcelable
}