package com.example.consecutivepractices.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.service.AlarmService
import com.example.consecutivepractices.domain.models.Profile
import com.example.consecutivepractices.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableStateFlow(Profile.EMPTY)
    val profile: StateFlow<Profile> = _profile.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _timeError = MutableStateFlow<String?>(null)
    val timeError: StateFlow<String?> = _timeError.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val currentProfile = profileRepository.getProfileOnce()
            _profile.value = currentProfile
        }
    }

    fun updateFullName(fullName: String) {
        _profile.value = _profile.value.copy(fullName = fullName)
    }

    fun updateAvatarUri(avatarUri: String) {
        _profile.value = _profile.value.copy(avatarUri = avatarUri)
    }

    fun updateResumeUrl(resumeUrl: String) {
        _profile.value = _profile.value.copy(resumeUrl = resumeUrl)
    }

    fun updatePosition(position: String) {
        _profile.value = _profile.value.copy(position = position)
    }

    fun updateFavoritePairTime(time: String) {
        _profile.value = _profile.value.copy(favoritePairTime = time)
        validateTime(time)
    }

    private fun validateTime(time: String) {
        _timeError.value = when {
            time.isBlank() -> null
            !isValidTimeFormat(time) -> "Некорректный формат времени. Используйте HH:mm"
            else -> null
        }
    }

    private fun isValidTimeFormat(time: String): Boolean {
        return try {
            val parts = time.split(":")
            if (parts.size != 2) return false

            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            hour in 0..23 && minute in 0..59
        } catch (e: Exception) {
            false
        }
    }

    fun saveProfile(context: Context) {
        val time = _profile.value.favoritePairTime
        if (time.isNotBlank() && !isValidTimeFormat(time)) {
            _timeError.value = "Некорректный формат времени. Используйте HH:mm"
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            try {
                val profileToSave = _profile.value
                profileRepository.saveProfile(profileToSave)

                // Установить уведомление если время указано и корректно
                if (profileToSave.favoritePairTime.isNotBlank() &&
                    isValidTimeFormat(profileToSave.favoritePairTime)) {
                    val alarmService = AlarmService(context)
                    alarmService.scheduleNotification(
                        profileToSave.favoritePairTime,
                        profileToSave.fullName.ifBlank { "Студент" }
                    )
                }

                _saveSuccess.value = true
            } catch (e: Exception) {
                // Обработка ошибки
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }

    fun clearTimeError() {
        _timeError.value = null
    }
}