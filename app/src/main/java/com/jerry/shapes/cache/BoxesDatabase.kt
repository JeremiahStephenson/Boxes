package com.jerry.shapes.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jerry.shapes.cache.data.*

@Database(
    entities = [Project::class, Pixel::class, Layer::class, History::class, HistoryItem::class],
    version = 1,
    exportSchema = false,
)
abstract class BoxesDatabase : RoomDatabase() {
    abstract fun boxesDao(): BoxesDao

    companion object {
        const val DATABASE_NAME = "boxesDb"
    }
}
