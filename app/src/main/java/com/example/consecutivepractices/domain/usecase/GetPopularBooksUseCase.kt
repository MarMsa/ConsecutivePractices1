package com.example.consecutivepractices.domain.usecase

import com.example.consecutivepractices.domain.models.Book
import com.example.consecutivepractices.domain.repository.BookRepository
import javax.inject.Inject

class GetPopularBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(
        maxResults: Int = 20,
        startIndex: Int = 0
    ): Result<List<Book>> {
        return repository.getPopularBooks(maxResults, startIndex)
    }
}
