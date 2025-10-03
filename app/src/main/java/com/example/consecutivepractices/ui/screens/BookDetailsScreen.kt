package com.example.consecutivepractices.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.consecutivepractices.viewmodel.BookViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.consecutivepractices.shareBook

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(bookId: Int?, navController: androidx.navigation.NavController) {
    val viewModel: BookViewModel = viewModel()
    val book = bookId?.let { viewModel.getBookById(it) }
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val bookId = navBackStackEntry?.arguments?.getString("bookId")?.toIntOrNull()

    Column {
        TopAppBar(
            title = { Text("Book Details") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    book?.let { shareBook(context, it) }
                }) {
                    Icon(Icons.Filled.Share, contentDescription = "Share")
                }
            }
        )

        Column(modifier = Modifier.padding(16.dp)) {
            book?.let {
                val details = listOf(
                    "Title" to it.title,
                    "Year" to it.year.toString(),
                    "Rating" to it.rating.toString(),
                    "Genre" to it.genre,
                    "Author" to it.author,
                    "Synopsis" to it.synopsis
                )
                details.forEach { (label, value) ->
                    Text(
                        text = "$label: $value",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } ?: Text(text = "Book not found")
        }
    }

}