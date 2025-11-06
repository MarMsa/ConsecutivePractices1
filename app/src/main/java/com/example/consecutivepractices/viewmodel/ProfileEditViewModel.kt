package com.example.consecutivepractices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.data.repository.ProfileRepository
import com.example.consecutivepractices.domain.models.Profile
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

    fun saveProfile() {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                profileRepository.saveProfile(_profile.value)
                _saveSuccess.value = true
            } catch (e: Exception) {
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}