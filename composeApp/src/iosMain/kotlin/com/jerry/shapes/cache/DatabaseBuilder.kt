package com.jerry.shapes.cache

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun getDatabaseBuilder(): RoomDatabase.Builder<BoxesDatabase> {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    val path = directory!!.path!! + "/" + BoxesDatabase.DATABASE_NAME
    return Room.databaseBuilder<BoxesDatabase>(
        name = path,
        factory = { BoxesDatabase::class.instantiateImpl() }
    )
}
