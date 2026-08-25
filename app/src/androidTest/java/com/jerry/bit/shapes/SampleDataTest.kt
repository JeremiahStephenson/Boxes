package com.jerry.bit.shapes

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jerry.bit.shapes.cache.BoxesDao
import com.jerry.bit.shapes.cache.BoxesDatabase
import com.jerry.bit.shapes.util.ProjectSeeder
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

@RunWith(AndroidJUnit4::class)
class SampleDataTest {

    @Test
    fun createSampleProjects() = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Use the same DAO instance as the app if Koin is started, otherwise build a temp DB
        val dao = try {
            GlobalContext.get().get<BoxesDao>()
        } catch (e: Exception) {
            val db = Room.databaseBuilder(
                appContext,
                BoxesDatabase::class.java,
                BoxesDatabase.DATABASE_NAME
            ).build()
            db.boxesDao()
        }

        ProjectSeeder(dao).seedProjects()
    }
}
