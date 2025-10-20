package com.example.consecutivepractices.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.domain.models.Book
import com.example.consecutivepractices.domain.usecase.GetBookDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getBookDetailsUseCase: GetBookDetailsUseCase
) : ViewModel() {

    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadBookDetails()
    }

    private fun loadBookDetails() {
        val bookId = getBookIdFromSavedState()

        bookId?.let { id ->
            viewModelScope.launch {
                _isLoading.value = true
                _error.value = null

                try {
                    val result = getBookDetailsUseCase(bookId = id)

                    _isLoading.value = false

                    result.onSuccess { bookDetails ->
                        _book.value = bookDetails
                    }.onFailure { exception ->
                        _error.value = "Ошибка загрузки деталей книги: ${exception.message}"
                    }
                } catch (e: Exception) {
                    _isLoading.value = false
                    _error.value = "Ошибка: ${e.message}"
                }
            }
        } ?: run {
            _error.value = "ID книги не найден"
        }
    }

    private fun getBookIdFromSavedState(): String? {
        return try {
            savedStateHandle.get<String>("bookId")
        } catch (e: Exception) {
            null
        }
    }

    fun shareBook(context: Context) {
        _book.value?.let { book ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Прочтите эту книгу!")
                putExtra(Intent.EXTRA_TEXT,
                    "Книга: ${book.title} (${book.year})\n" +
                            "Рейтинг: ${book.rating}\n" +
                            "Жанр: ${book.genre}\n" +
                            "Автор: ${book.author}\n" +
                            "Описание: ${book.synopsis.take(200)}...\n\n" +
                            "Расскажите об этой книге друзьям!"
                )
            }
            context.startActivity(Intent.createChooser(shareIntent, "Поделиться книгой"))
        }
    }

    fun retry() {
        loadBookDetails()
    }
}
