package com.example.consecutivepractices.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.consecutivepractices.viewmodel.BookDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: Int?
) {
    val viewModel: BookDetailsViewModel = hiltViewModel()
    val context = LocalContext.current

    Column {
        TopAppBar(
            title = { Text("Детали книги") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.shareBook(context) }) {
                    Icon(Icons.Default.Share, contentDescription = "Поделиться")
                }
            }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            viewModel.book?.let { book ->

                val details = listOf(
                    "Название" to book.title,
                    "Год" to book.year.toString(),
                    "Рейтинг" to book.rating.toString(),
                    "Жанр" to book.genre,
                    "Автор" to book.author,
                    "Описание" to book.synopsis
                )

                details.forEach { (label, value) ->
                    Text(
                        text = "$label: $value",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } ?: Text(text = "Книга не найден")
        }
    }
}
