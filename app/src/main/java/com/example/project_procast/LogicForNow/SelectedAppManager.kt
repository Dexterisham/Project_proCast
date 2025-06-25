package com.example.project_procast.LogicForNow

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ✅ Must be top-level!
val Context.dataStore by preferencesDataStore(name = "selected_apps")

object SelectedAppsManager {
    private val SELECTED_APPS_KEY = stringSetPreferencesKey("selected_apps")

    suspend fun saveSelectedApps(context: Context, packages: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_APPS_KEY] = packages
        }
    }

    fun getSelectedApps(context: Context): Flow<Set<String>> {
        return context.dataStore.data
            .map { prefs ->
                prefs[SELECTED_APPS_KEY] ?: emptySet()
            }
    }
}
