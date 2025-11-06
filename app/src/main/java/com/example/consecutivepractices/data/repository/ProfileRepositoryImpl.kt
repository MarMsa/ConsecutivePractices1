package com.example.consecutivepractices.data.repository

import com.example.consecutivepractices.data.local.ProfilePreferences
import com.example.consecutivepractices.domain.models.Profile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profilePreferences: ProfilePreferences
) : ProfileRepository {

    override fun getProfile(): Flow<Profile> {
        return profilePreferences.getProfile()
    }

    override suspend fun saveProfile(profile: Profile) {
        profilePreferences.saveProfile(profile)
    }

    override suspend fun getProfileOnce(): Profile {
        return profilePreferences.getProfileOnce()
    }
}