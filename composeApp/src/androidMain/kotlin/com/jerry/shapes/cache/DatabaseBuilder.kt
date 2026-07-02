package com.jerry.shapes.cache

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidDatabaseBuilder : KoinComponent {
    val context: Context by inject()
    
    fun build(): RoomDatabase.Builder<BoxesDatabase> {
        val dbFile = context.getDatabasePath(BoxesDatabase.DATABASE_NAME)
        return Room.databaseBuilder<BoxesDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
    }
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<BoxesDatabase> {
    return AndroidDatabaseBuilder().build()
}
