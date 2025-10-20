package com.example.consecutivepractices.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.consecutivepractices.viewmodel.BookListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    navController: NavController,
    viewModel: BookListViewModel = hiltViewModel()
) {
    Column {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            LazyColumn {
                items(viewModel.books) { book ->
                    Row(modifier = Modifier
                        .padding(6.dp)
                        .clickable {
                            navController.navigate("book_details/${book.id}") {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Text(
                            text = "${book.title} (${book.year}) - ${book.rating}",
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
