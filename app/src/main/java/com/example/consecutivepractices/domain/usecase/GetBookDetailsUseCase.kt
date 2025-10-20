package com.example.consecutivepractices.domain.usecase

import com.example.consecutivepractices.domain.models.Book
import com.example.consecutivepractices.domain.repository.BookRepository
import javax.inject.Inject

class GetBookDetailsUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: String): Result<Book> {
        return repository.getBookDetails(bookId)
    }
}
