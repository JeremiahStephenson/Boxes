package com.jerry.bit.shapes.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.mapLatest
import kotlin.properties.ReadOnlyProperty

class AppDataStore(
    private val context: Context,
    store: ReadOnlyProperty<Context, DataStore<Preferences>>,
) {
    private val Context.datastore by store

    val hasLaunchedBefore =
        context.datastore.data.mapLatest { preferences ->
            preferences[HAS_LAUNCHED]
        }

    val isFirstProjectGuideAvailable =
        context.datastore.data.mapLatest { preferences ->
            preferences[FIRST_PROJECT_GUIDE_SHOWN] != true
        }

    suspend fun setHasLaunched() {
        context.datastore.edit { preferences ->
            preferences[HAS_LAUNCHED] = true
        }
    }

    suspend fun markFirstProjectGuideShown() {
        context.datastore.edit { preferences ->
            preferences[FIRST_PROJECT_GUIDE_SHOWN] = true
        }
    }

    companion object {
        private val HAS_LAUNCHED = booleanPreferencesKey("FIRST_LAUNCH")
        private val FIRST_PROJECT_GUIDE_SHOWN = booleanPreferencesKey("FIRST_PROJECT_GUIDE_SHOWN")
    }
}
