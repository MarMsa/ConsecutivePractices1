package com.example.consecutivepractices.data.repository

import com.example.consecutivepractices.data.local.datastore.FilterPreferencesManager
import com.example.consecutivepractices.data.remote.GoogleBooksApi
import com.example.consecutivepractices.domain.models.Book
import com.example.consecutivepractices.domain.models.FilterPreferences
import com.example.consecutivepractices.domain.repository.BookRepository
import com.example.consecutivepractices.utils.cache.FavoriteCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val api: GoogleBooksApi,
    private val filterPreferencesManager: FilterPreferencesManager,
    private val favoriteCache: FavoriteCache
) : BookRepository {

    override suspend fun searchBooks(
        query: String,
        maxResults: Int,
        startIndex: Int
    ): Result<List<Book>> {
        try {
            val response = api.searchBooks(
                query = query,
                maxResults = maxResults,
                startIndex = startIndex
            )
            val books = response.items?.map { it.toBook() } ?: emptyList()
            return Result.success(books)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getBooksByGenre(
        genre: String,
        maxResults: Int,
        startIndex: Int
    ): Result<List<Book>> {
        try {
            val response = api.getBooksByGenre(
                genre = "subject:$genre",
                maxResults = maxResults,
                startIndex = startIndex
            )
            val books = response.items?.map { it.toBook() } ?: emptyList()
            return Result.success(books)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getBooksByAuthor(
        author: String,
        maxResults: Int,
        startIndex: Int
    ): Result<List<Book>> {
        try {
            val response = api.getBooksByAuthor(
                author = "inauthor:$author",
                maxResults = maxResults,
                startIndex = startIndex
            )
            val books = response.items?.map { it.toBook() } ?: emptyList()
            return Result.success(books)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getPopularBooks(
        maxResults: Int,
        startIndex: Int
    ): Result<List<Book>> {
        try {
            val response = api.searchBooks(
                query = "bestseller",
                maxResults = maxResults,
                startIndex = startIndex
            )
            val books = response.items?.map { it.toBook() } ?: emptyList()
            return Result.success(books)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getBookDetails(bookId: String): Result<Book> {
        try {
            var lastException: Exception? = null

            for (attempt in 1..3) {
                try {
                    val response = api.getBookDetails(bookId = bookId)
                    return Result.success(response.toBook())
                } catch (e: Exception) {
                    lastException = e
                    if (attempt < 3) {
                        kotlinx.coroutines.delay(1000L * attempt)
                    }
                }
            }

            return Result.failure(lastException ?: Exception("Не удалось загрузить детали книги"))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun getFavoriteBooks(): Flow<List<Book>> {
        return favoriteCache.favoriteBooksList
    }

    override suspend fun addToFavorites(book: Book) {
        favoriteCache.addToFavorites(book.id, book)
    }


    override suspend fun removeFromFavorites(bookId: String) {
        favoriteCache.removeFromFavorites(bookId)
    }

    override suspend fun isBookFavorite(bookId: String): Boolean {
        return favoriteCache.isBookFavorite(bookId)
    }

    // Методы для фильтров
    override fun getFilterPreferences(): Flow<FilterPreferences> {
        return filterPreferencesManager.filterPreferences
    }

    override suspend fun updateFilterPreferences(filterPreferences: FilterPreferences) {
        filterPreferencesManager.updateFilterPreferences(filterPreferences)
    }

    override suspend fun clearFilterPreferences() {
        filterPreferencesManager.clearFilterPreferences()
    }
}
