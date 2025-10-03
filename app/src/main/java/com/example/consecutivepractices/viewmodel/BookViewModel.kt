package com.example.consecutivepractices.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.consecutivepractices.data.Book

class BookViewModel : ViewModel() {
    private val _books = mutableStateListOf(
        Book(1, "Зелёная миля", 1996 , 8.6, "драма", "Стивен Кинг", "Роман-событие, ставший лауреатом премии Брэма..."),
        Book(2, "11/22/63", 2020, 9.5, "фантастика и фэнтези", "Стивен Кинг", ".Убийство президента Кеннеди стало..."),
        Book(3, "Стрелок", 2016, 7.6, "Детективы, триллеры, ужасы", "Стивен Кинг", "Юный Роланд — последний благородный рыцарь...")
    )

    val books: List<Book> get() = _books

    fun getBookById(id: Int): Book? = _books.find { it.id == id }
}