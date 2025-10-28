package com.example.consecutivepractices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.domain.models.Book
import com.example.consecutivepractices.domain.models.FilterPreferences
import com.example.consecutivepractices.domain.repository.BookRepository
import com.example.consecutivepractices.utils.cache.BadgeCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val repository: BookRepository,
    private val badgeCache: BadgeCache
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentFilters = MutableStateFlow(FilterPreferences())
    val currentFilters: StateFlow<FilterPreferences> = _currentFilters.asStateFlow()

    val showFilterBadge: StateFlow<Boolean> = badgeCache.showFilterBadge

    init {
        loadPopularBooks()
        observeFilters()
    }

    private fun observeFilters() {
        viewModelScope.launch {
            repository.getFilterPreferences().collectLatest { filters ->
                _currentFilters.value = filters
                applyCurrentFilters()
            }
        }
    }

    fun loadPopularBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = repository.getPopularBooks()

            _isLoading.value = false

            result.onSuccess { books ->
                _books.value = applyFilters(books)
            }.onFailure { exception ->
                _error.value = "Ошибка загрузки книг: ${exception.message}"
            }
        }
    }

    fun searchBooks(query: String) {
        if (query.isBlank()) {
            loadPopularBooks()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = repository.searchBooks(query = query)

            _isLoading.value = false

            result.onSuccess { books ->
                _books.value = applyFilters(books)
            }.onFailure { exception ->
                _error.value = "Ошибка поиска: ${exception.message}"
            }
        }
    }

    fun applyCurrentFilters() {
        _books.value = applyFilters(_books.value)
    }

    private fun applyFilters(books: List<Book>): List<Book> {
        val filters = _currentFilters.value
        return books.filter { book ->
            (filters.genre.isBlank() || book.genre.contains(filters.genre, ignoreCase = true)) &&
                    (book.rating >= filters.minRating) &&
                    (filters.author.isBlank() || book.author.contains(filters.author, ignoreCase = true))
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun getFilterPreferences(): FilterPreferences {
        return _currentFilters.value
    }
}
