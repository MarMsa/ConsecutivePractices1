package com.example.consecutivepractices.domain.repository

import com.example.consecutivepractices.domain.models.Book

interface BookRepository {
    suspend fun searchBooks(
        query: String,
        maxResults: Int = 20,
        startIndex: Int = 0
    ): Result<List<Book>>

    suspend fun getBooksByGenre(
        genre: String,
        maxResults: Int = 20,
        startIndex: Int = 0
    ): Result<List<Book>>

    suspend fun getBooksByAuthor(
        author: String,
        maxResults: Int = 20,
        startIndex: Int = 0
    ): Result<List<Book>>

    suspend fun getPopularBooks(
        maxResults: Int = 20,
        startIndex: Int = 0
    ): Result<List<Book>>
    suspend fun getBookDetails(bookId: String): Result<Book>
}
