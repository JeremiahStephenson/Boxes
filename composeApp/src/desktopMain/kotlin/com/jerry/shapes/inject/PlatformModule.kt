package com.jerry.shapes.inject

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.jerry.shapes.util.Analytics
import com.jerry.shapes.util.PlatformContext
import okio.Path.Companion.toPath
import org.koin.dsl.module

class DesktopAnalytics : Analytics {
    override fun logEvent(name: String, params: Map<String, Any?>) {
        println("Analytics Event: $name, $params")
    }

    override fun logError(throwable: Throwable) {
        println("Analytics Error: ${throwable.message}")
    }
}

class DesktopPlatformContext : PlatformContext()

actual val platformModule = module {
    single<Analytics> { DesktopAnalytics() }
    single {
        PreferenceDataStoreFactory.create {
            "settings.preferences_pb".toPath()
        }
    }
    single<PlatformContext> { DesktopPlatformContext() }
}
