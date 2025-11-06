package com.example.consecutivepractices.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.consecutivepractices.domain.models.FilterPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "filter_preferences")

@Singleton
class FilterPreferencesManager @Inject constructor(
    private val context: Context
) {
    private object PreferencesKeys {
        val GENRE = stringPreferencesKey("genre")
        val MIN_RATING = doublePreferencesKey("min_rating")
        val AUTHOR = stringPreferencesKey("author")
    }

    val filterPreferences: Flow<FilterPreferences> = context.dataStore.data
        .map { preferences ->
            FilterPreferences(
                genre = preferences[PreferencesKeys.GENRE] ?: "",
                minRating = preferences[PreferencesKeys.MIN_RATING] ?: 0.0,
                author = preferences[PreferencesKeys.AUTHOR] ?: ""
            )
        }

    suspend fun updateFilterPreferences(filterPreferences: FilterPreferences) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GENRE] = filterPreferences.genre
            preferences[PreferencesKeys.MIN_RATING] = filterPreferences.minRating
            preferences[PreferencesKeys.AUTHOR] = filterPreferences.author
        }
    }

    suspend fun clearFilterPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
