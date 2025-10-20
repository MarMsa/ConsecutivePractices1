package com.example.consecutivepractices.data.remote

import com.example.consecutivepractices.data.models.BookDto
import com.example.consecutivepractices.data.models.BookResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApi {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("startIndex") startIndex: Int = 0,
        @Query("orderBy") orderBy: String = "relevance"
    ): BookResponse

    @GET("volumes")
    suspend fun getBooksByGenre(
        @Query("q") genre: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("startIndex") startIndex: Int = 0
    ): BookResponse

    @GET("volumes")
    suspend fun getBooksByAuthor(
        @Query("q") author: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("startIndex") startIndex: Int = 0
    ): BookResponse

    @GET("volumes/{bookId}")
    suspend fun getBookDetails(
        @Path("bookId") bookId: String
    ): BookDto
}
