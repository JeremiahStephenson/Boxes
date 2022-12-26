package com.jerry.boxes.inject

import androidx.room.Room
import com.jerry.boxes.cache.BoxesDatabase
import org.koin.dsl.module

val cacheModule = module {
    single {
        Room.databaseBuilder(
            get(),
            BoxesDatabase::class.java,
            BoxesDatabase.DATABASE_NAME
        ).build()
    }
    single { get<BoxesDatabase>().boxesDao() }
}