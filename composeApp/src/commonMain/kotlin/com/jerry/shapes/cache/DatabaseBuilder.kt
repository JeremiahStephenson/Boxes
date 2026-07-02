package com.jerry.shapes.cache

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<BoxesDatabase>
