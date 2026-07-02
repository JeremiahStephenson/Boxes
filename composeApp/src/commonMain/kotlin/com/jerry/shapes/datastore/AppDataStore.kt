package com.jerry.shapes.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppDataStore(
    private val scope: CoroutineScope,
    private val dataStore: DataStore<Preferences>
) {
    val showOnboarding: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SHOW_ONBOARDING] ?: true
    }

    suspend fun setShowOnboarding(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_ONBOARDING] = show
        }
    }

    companion object {
        private val SHOW_ONBOARDING = booleanPreferencesKey("show_onboarding")
    }
}
