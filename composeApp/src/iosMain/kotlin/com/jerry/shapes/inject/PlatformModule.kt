package com.jerry.shapes.inject

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.jerry.shapes.util.Analytics
import com.jerry.shapes.util.PlatformContext
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

class IosAnalytics : Analytics {
    override fun logEvent(name: String, params: Map<String, Any?>) {
        // No-op
    }

    override fun logError(throwable: Throwable) {
        // No-op
    }
}

class IosPlatformContext : PlatformContext()

actual val platformModule = module {
    single<Analytics> { IosAnalytics() }
    single {
        PreferenceDataStoreFactory.create {
            val directory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = true,
                error = null
            )
            (directory!!.path!! + "/settings.preferences_pb").toPath()
        }
    }
    single<PlatformContext> { IosPlatformContext() }
}
