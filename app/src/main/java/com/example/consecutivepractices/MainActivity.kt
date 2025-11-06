package com.example.consecutivepractices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.consecutivepractices.ui.screens.BookDetailsScreen
import com.example.consecutivepractices.ui.screens.BookListScreen
import com.example.consecutivepractices.ui.screens.FavoritesScreen
import com.example.consecutivepractices.ui.screens.FilterScreen
import com.example.consecutivepractices.ui.theme.BookAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookAppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    topBar = {
                        when (currentRoute) {
                            NavRoutes.BOOK_DETAILS -> BookDetailsTopAppBar(navController)
                            NavRoutes.FILTERS -> FilterTopAppBar(navController)
                            NavRoutes.FAVORITES -> FavoritesTopAppBar(navController)
                        }
                    },
                    bottomBar = {
                        if (currentRoute != NavRoutes.BOOK_DETAILS &&
                            currentRoute != NavRoutes.FILTERS) {
                            BottomNavigationBar(navController, currentRoute)
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController,
                        startDestination = NavRoutes.BOOK_LIST,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable(NavRoutes.BOOK_LIST) {
                            BookListScreen(navController = navController)
                        }
                        composable("${NavRoutes.BOOK_DETAILS}/{bookId}") { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()
                            BookDetailsScreen(
                                navController = navController,
                                bookId = bookId as String?
                            )
                        }
                        composable(NavRoutes.FILTERS) {
                            FilterScreen(navController = navController)
                        }
                        composable(NavRoutes.FAVORITES) {
                            FavoritesScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsTopAppBar(navController: androidx.navigation.NavController) {
    TopAppBar(
        title = { Text("Подробности о книге") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterTopAppBar(navController: androidx.navigation.NavController) {
    TopAppBar(
        title = { Text("Фильтры") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesTopAppBar(navController: androidx.navigation.NavController) {
    TopAppBar(
        title = { Text("Избранное") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == NavRoutes.HOME,
            onClick = { navController.navigate(NavRoutes.HOME) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == NavRoutes.BOOK_LIST,
            onClick = { navController.navigate(NavRoutes.BOOK_LIST) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Books") },
            label = { Text("Books") }
        )
        NavigationBarItem(
            selected = currentRoute == NavRoutes.FAVORITES,
            onClick = { navController.navigate(NavRoutes.FAVORITES) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") }
        )
        NavigationBarItem(
            selected = currentRoute == NavRoutes.VIDEO,
            onClick = { navController.navigate(NavRoutes.VIDEO) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Video") },
            label = { Text("Video") }
        )
        NavigationBarItem(
            selected = currentRoute == NavRoutes.NOTIFICATIONS,
            onClick = { navController.navigate(NavRoutes.NOTIFICATIONS) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications") },
            label = { Text("Bell") }
        )
    }
}
