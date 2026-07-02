package com.jerry.shapes.inject

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.jerry.shapes.util.Analytics
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

class IosAnalytics : Analytics {
    override fun logEvent(name: String, params: Map<String, Any?>) {
        // No-op or native log
    }

    override fun logError(throwable: Throwable) {
        // No-op or native log
    }
}

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
}
