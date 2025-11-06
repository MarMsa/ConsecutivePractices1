package com.example.consecutivepractices.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.consecutivepractices.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favoriteBooks by viewModel.favoriteBooks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранные книги") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (favoriteBooks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = "Нет избранных",
                            tint = Color.Gray,
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            text = "Здесь пока пусто",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "Добавляйте книги в избранное,\nнажимая на сердечко в деталях книги",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteBooks, key = { it.id }) { book ->
                        FavoriteBookItem(
                            book = book,
                            onItemClick = {
                                navController.navigate("book_details/${book.id}") {
                                    launchSingleTop = true
                                }
                            },
                            onRemoveFromFavorites = {
                                viewModel.removeFromFavorites(book.id)
                            }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteBookItem(
    book: com.example.consecutivepractices.domain.models.Book,
    onItemClick: () -> Unit,
    onRemoveFromFavorites: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = onItemClick
    ) {
        androidx.compose.material3.ListItem(
            headlineContent = {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
            },
            supportingContent = {
                Column {
                    Text(
                        text = "${book.author} • ${book.year}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Рейтинг: ${"%.1f".format(book.rating)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            trailingContent = {
                IconButton(onClick = onRemoveFromFavorites) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Удалить из избранного",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}
