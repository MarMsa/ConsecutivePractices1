package com.example.consecutivepractices.data.repository

import com.example.consecutivepractices.domain.models.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<Profile>
    suspend fun saveProfile(profile: Profile)
    suspend fun getProfileOnce(): Profile
}