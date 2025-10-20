package com.example.consecutivepractices.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.consecutivepractices.data.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val books = listOf(
        Book(1, "Зелёная миля", 1996 , 8.6, "драма", "Стивен Кинг", "Роман-событие, ставший лауреатом премии Брэма..."),
        Book(2, "11/22/63", 2020, 9.5, "фантастика и фэнтези", "Стивен Кинг", ".Убийство президента Кеннеди стало..."),
        Book(3, "Стрелок", 2016, 7.6, "Детективы, триллеры, ужасы", "Стивен Кинг", "Юный Роланд — последний благородный рыцарь...")
    )

    val book: Book?
        get() {
            val bookIdString = savedStateHandle.get<String>("bookId")
            return bookIdString?.toIntOrNull()?.let { bookId ->
                books.find { it.id == bookId }
            }
        }

    fun shareBook(context: Context) {
        book?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Прочтите эту книгу!")
                putExtra(Intent.EXTRA_TEXT, "Книга: ${it.title} (${it.year})\nРейтинг: ${it.rating}\nЖанр: ${it.genre}\nАвтор: ${it.author}\nОписание: ${it.synopsis}\nРасскажите об этой книге друзьям")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Поделиться"))
        }
    }
}
