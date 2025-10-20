package com.example.consecutivepractices.data.repository

import com.example.consecutivepractices.data.remote.GoogleBooksApi
import com.example.consecutivepractices.domain.models.Book
import com.example.consecutivepractices.domain.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val api: GoogleBooksApi
) : BookRepository {

    override suspend fun searchBooks(
        query: String,
        maxResults: Int,
        startIndex: Int
    ): Result<List<Book>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchBooks(
                query = query,
                maxResults = maxResults,
                startIndex = startIndex
            )
            val books = response.items?.map { it.toBook() } ?: emptyList()
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBooksByGenre(
        genre: String,
        maxResults: Int,
        startIndex: Int
    ): Result<List<Book>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getBooksByGenre(
                genre = "subject:$genre",
                maxResults = maxResults,
                startIndex = startIndex
            )
            val books = response.items?.map { it.toBook() } ?: emptyList()
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBooksByAuthor(
        author: String,
        maxResults: Int,
        startIndex: Int
    ): Result<List<Book>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getBooksByAuthor(
                author = "inauthor:$author",
                maxResults = maxResults,
                startIndex = startIndex
            )
            val books = response.items?.map { it.toBook() } ?: emptyList()
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPopularBooks(
        maxResults: Int,
        startIndex: Int
    ): Result<List<Book>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchBooks(
                query = "bestseller",
                maxResults = maxResults,
                startIndex = startIndex
            )
            val books = response.items?.map { it.toBook() } ?: emptyList()
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookDetails(bookId: String): Result<Book> = withContext(Dispatchers.IO) {
        try {
            var lastException: Exception? = null

            for (attempt in 1..3) {
                try {
                    val response = api.getBookDetails(bookId = bookId)
                    return@withContext Result.success(response.toBook())
                } catch (e: Exception) {
                    lastException = e
                    if (attempt < 3) {
                        kotlinx.coroutines.delay(1000L * attempt)
                    }
                }
            }

            Result.failure(lastException ?: Exception("Не удалось загрузить детали книги"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
