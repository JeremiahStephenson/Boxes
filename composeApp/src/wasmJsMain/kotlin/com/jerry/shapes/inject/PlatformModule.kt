package com.jerry.shapes.inject

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.jerry.shapes.util.Analytics
import org.koin.dsl.module

class WebAnalytics : Analytics {
    override fun logEvent(name: String, params: Map<String, Any?>) {
        // No-op or console.log
    }

    override fun logError(throwable: Throwable) {
        // No-op or console.error
    }
}

actual val platformModule = module {
    single<Analytics> { WebAnalytics() }
    single {
        PreferenceDataStoreFactory.create {
            // Need a proper path or implementation for Wasm
            "settings.preferences_pb".toString()
        }
    }
}
