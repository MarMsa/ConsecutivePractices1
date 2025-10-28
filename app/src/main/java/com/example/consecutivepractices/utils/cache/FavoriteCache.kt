package com.example.consecutivepractices.utils.cache

import com.example.consecutivepractices.domain.models.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteCache @Inject constructor() {
    private val _favoriteBooks = MutableStateFlow<Set<String>>(emptySet())
    val favoriteBooks: StateFlow<Set<String>> = _favoriteBooks.asStateFlow()

    private val _favoriteBooksList = MutableStateFlow<List<Book>>(emptyList())
    val favoriteBooksList: StateFlow<List<Book>> = _favoriteBooksList.asStateFlow()

    fun addToFavorites(bookId: String, book: Book? = null) {
        _favoriteBooks.value = _favoriteBooks.value + bookId
        book?.let {
            _favoriteBooksList.value = _favoriteBooksList.value + it
        }
    }

    fun removeFromFavorites(bookId: String) {
        _favoriteBooks.value = _favoriteBooks.value - bookId
        _favoriteBooksList.value = _favoriteBooksList.value.filter { it.id != bookId }
    }

    fun isBookFavorite(bookId: String): Boolean {
        return _favoriteBooks.value.contains(bookId)
    }

    fun getFavoriteBooks(): List<Book> {
        return _favoriteBooksList.value
    }

    fun setFavorites(books: List<Book>) {
        _favoriteBooks.value = books.map { it.id }.toSet()
        _favoriteBooksList.value = books
    }
}
