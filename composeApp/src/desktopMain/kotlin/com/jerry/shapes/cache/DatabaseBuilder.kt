package com.jerry.shapes.cache

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<BoxesDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), BoxesDatabase.DATABASE_NAME)
    return Room.databaseBuilder<BoxesDatabase>(
        name = dbFile.absolutePath
    )
}
