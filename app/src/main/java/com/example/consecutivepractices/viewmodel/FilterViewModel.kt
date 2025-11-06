package com.example.consecutivepractices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.domain.models.FilterPreferences
import com.example.consecutivepractices.domain.repository.BookRepository
import com.example.consecutivepractices.utils.cache.BadgeCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val repository: BookRepository,
    private val badgeCache: BadgeCache
) : ViewModel() {

    private val _filterState = MutableStateFlow(FilterPreferences())
    val filterState: StateFlow<FilterPreferences> = _filterState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSavedFilters()
    }

    private fun loadSavedFilters() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val savedFilters = repository.getFilterPreferences().first()
                _filterState.value = savedFilters
                updateBadgeVisibility(savedFilters)
            } catch (e: Exception) {
                // Ошибка загрузки фильтров - используем значения по умолчанию
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateGenre(genre: String) {
        _filterState.value = _filterState.value.copy(genre = genre)
    }

    fun updateMinRating(rating: Double) {
        _filterState.value = _filterState.value.copy(minRating = rating)
    }

    fun updateAuthor(author: String) {
        _filterState.value = _filterState.value.copy(author = author)
    }

    fun applyFilters() {
        viewModelScope.launch {
            repository.updateFilterPreferences(_filterState.value)
            updateBadgeVisibility(_filterState.value)
        }
    }

    fun clearFilters() {
        viewModelScope.launch {
            _filterState.value = FilterPreferences()
            repository.clearFilterPreferences()
            badgeCache.setFilterBadgeVisible(false)
        }
    }

    private fun updateBadgeVisibility(filterPreferences: FilterPreferences) {
        val hasActiveFilters = filterPreferences.genre.isNotBlank() ||
                filterPreferences.minRating > 0.0 ||
                filterPreferences.author.isNotBlank()
        badgeCache.setFilterBadgeVisible(hasActiveFilters)
    }

    fun getCurrentFilters(): FilterPreferences {
        return _filterState.value
    }
}
