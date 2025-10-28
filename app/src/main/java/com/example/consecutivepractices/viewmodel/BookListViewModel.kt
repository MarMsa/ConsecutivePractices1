package com.example.consecutivepractices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.domain.models.Book
import com.example.consecutivepractices.domain.usecase.GetPopularBooksUseCase
import com.example.consecutivepractices.domain.usecase.SearchBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val getPopularBooksUseCase: GetPopularBooksUseCase,
    private val searchBooksUseCase: SearchBooksUseCase
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadPopularBooks()
    }

    fun loadPopularBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = getPopularBooksUseCase()

            _isLoading.value = false

            result.onSuccess { books ->
                _books.value = books
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

            val result = searchBooksUseCase(query = query)

            _isLoading.value = false

            result.onSuccess { books ->
                _books.value = books
            }.onFailure { exception ->
                _error.value = "Ошибка поиска: ${exception.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
