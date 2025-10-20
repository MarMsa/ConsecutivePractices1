package com.example.consecutivepractices.viewmodel

import androidx.lifecycle.ViewModel
import com.example.consecutivepractices.data.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor() : ViewModel() {
    val books = listOf(
        Book(1, "Зелёная миля", 1996 , 8.6, "драма", "Стивен Кинг", "Роман-событие, ставший лауреатом премии Брэма..."),
        Book(2, "11/22/63", 2020, 9.5, "фантастика и фэнтези", "Стивен Кинг", ".Убийство президента Кеннеди стало..."),
        Book(3, "Стрелок", 2016, 7.6, "Детективы, триллеры, ужасы", "Стивен Кинг", "Юный Роланд — последний благородный рыцарь...")
    )
}
