package com.example.consecutivepractices.utils.cache

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BadgeCache @Inject constructor() {
    private val _showFilterBadge = MutableStateFlow(false)
    val showFilterBadge: StateFlow<Boolean> = _showFilterBadge.asStateFlow()

    fun setFilterBadgeVisible(visible: Boolean) {
        _showFilterBadge.value = visible
    }

    fun isFilterActive(): Boolean {
        return _showFilterBadge.value
    }
}
