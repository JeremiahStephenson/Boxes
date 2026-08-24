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

    suspend fun setHasLaunched() {
        context.datastore.edit { preferences ->
            preferences[HAS_LAUNCHED] = true
        }
    }

    companion object {
        private val HAS_LAUNCHED = booleanPreferencesKey("FIRST_LAUNCH")
    }
}
