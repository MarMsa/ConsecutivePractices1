package com.example.consecutivepractices.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.consecutivepractices.domain.models.Profile
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.profileDataStore: DataStore<Preferences> by preferencesDataStore(name = "profile")

@Singleton
class ProfilePreferences @Inject constructor(
    private val context: Context
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val profileAdapter = moshi.adapter(Profile::class.java)
    private val profileKey = stringPreferencesKey("profile_data")

    fun getProfile(): Flow<Profile> {
        return context.profileDataStore.data.map { preferences ->
            preferences[profileKey]?.let { json ->
                profileAdapter.fromJson(json) ?: Profile.EMPTY
            } ?: Profile.EMPTY
        }
    }

    suspend fun saveProfile(profile: Profile) {
        context.profileDataStore.edit { preferences ->
            val json = profileAdapter.toJson(profile)
            preferences[profileKey] = json
        }
    }

    suspend fun getProfileOnce(): Profile {
        return getProfile().first()
    }
}