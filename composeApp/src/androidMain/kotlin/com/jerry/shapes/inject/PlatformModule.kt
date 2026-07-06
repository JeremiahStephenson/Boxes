package com.jerry.shapes.inject

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.jerry.shapes.LaunchViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.jerry.shapes.util.Analytics
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class AndroidAnalytics(private val firebaseAnalytics: FirebaseAnalytics) : Analytics {
    override fun logEvent(name: String, params: Map<String, Any?>) {
        val bundle = android.os.Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putBoolean(key, value)
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }

    override fun logError(throwable: Throwable) {
        val bundle = android.os.Bundle()
        bundle.putString("Error", throwable.message.orEmpty())
        firebaseAnalytics.logEvent("SaveError", bundle)
    }
}

private val Context.dataStore by preferencesDataStore(name = "settings")

actual val platformModule = module {
    single { FirebaseAnalytics.getInstance(androidContext()) }
    single<Analytics> { AndroidAnalytics(get()) }
    single { androidContext().dataStore }
    viewModel { LaunchViewModel(get()) }
}
