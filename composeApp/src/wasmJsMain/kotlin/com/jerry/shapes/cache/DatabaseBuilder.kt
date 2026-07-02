package com.jerry.shapes.cache

import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(): RoomDatabase.Builder<BoxesDatabase> {
    // Room support on Wasm is experimental and might need a different approach
    return Room.databaseBuilder<BoxesDatabase>(
        name = BoxesDatabase.DATABASE_NAME,
        factory = { BoxesDatabase::class.instantiateImpl() }
    )
}
