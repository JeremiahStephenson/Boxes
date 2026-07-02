package com.jerry.shapes.inject

import com.jerry.shapes.cache.BoxesDatabase
import com.jerry.shapes.cache.getDatabaseBuilder
import org.koin.dsl.module

val cacheModule =
    module {
        single {
            getDatabaseBuilder().build()
        }
        single { get<BoxesDatabase>().boxesDao() }
    }
