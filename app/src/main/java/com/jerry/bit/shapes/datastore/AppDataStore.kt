package com.jerry.bit.shapes.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.first
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

    suspend fun canRequestInAppUpdate(nowMillis: Long = System.currentTimeMillis()): Boolean =
        (context.datastore.data.first()[UPDATE_PROMPT_DEFERRED_UNTIL] ?: 0L) <= nowMillis

    suspend fun deferInAppUpdatePrompt(nowMillis: Long = System.currentTimeMillis()) {
        context.datastore.edit { preferences ->
            preferences[UPDATE_PROMPT_DEFERRED_UNTIL] = nowMillis + UPDATE_PROMPT_COOLDOWN_MILLIS
        }
    }

    companion object {
        private val HAS_LAUNCHED = booleanPreferencesKey("FIRST_LAUNCH")
        private val FIRST_PROJECT_GUIDE_SHOWN = booleanPreferencesKey("FIRST_PROJECT_GUIDE_SHOWN")
        private val UPDATE_PROMPT_DEFERRED_UNTIL = longPreferencesKey("UPDATE_PROMPT_DEFERRED_UNTIL")
        private const val UPDATE_PROMPT_COOLDOWN_MILLIS = 2L * 24 * 60 * 60 * 1_000
    }
}
