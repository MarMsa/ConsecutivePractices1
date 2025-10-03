package com.example.consecutivepractices

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.consecutivepractices.data.Book
import com.example.consecutivepractices.ui.screens.BookListScreen
import com.example.consecutivepractices.ui.screens.BookDetailsScreen
import com.example.consecutivepractices.ui.theme.BookAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookAppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute != "book_details/{bookId}") {
                            BottomNavigationBar(navController, currentRoute)
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController,
                        startDestination = "book_list",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("book_list") { BookListScreen(navController) }
                        composable("book_details/{bookId}") { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()
                            BookDetailsScreen(bookId, navController)
                        }
                    }
                }
            }
        }
    }
}

fun shareBook(context: android.content.Context, book: Book) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Check out this book!")
        putExtra(Intent.EXTRA_TEXT, "Movie: ${book.title} (${book.year})\nRating: ${book.rating}\nGenre: ${book.genre}\nAuthor: ${book.author}\nSynopsis: ${book.synopsis}\nShare this awesome book!")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "book_list",
            onClick = { navController.navigate("book_list") { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Books") },
            label = { Text("Books") }
        )
        NavigationBarItem(
            selected = currentRoute == "video",
            onClick = { navController.navigate("video") { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Video") },
            label = { Text("Video") }
        )
        NavigationBarItem(
            selected = currentRoute == "notifications",
            onClick = { navController.navigate("notifications") { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications") },
            label = { Text("Bell") }
        )
    }
}