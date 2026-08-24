package com.jerry.bit.shapes.inject

import androidx.room.Room
import com.jerry.bit.shapes.cache.BoxesDatabase
import org.koin.dsl.module

val cacheModule =
    module {
        single {
            Room
                .databaseBuilder(
                    get(),
                    BoxesDatabase::class.java,
                    BoxesDatabase.DATABASE_NAME,
                ).build()
        }
        single { get<BoxesDatabase>().boxesDao() }
    }
