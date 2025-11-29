package com.example.consecutivepractices

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.consecutivepractices.ui.screens.BookDetailsScreen
import com.example.consecutivepractices.ui.screens.BookListScreen
import com.example.consecutivepractices.ui.screens.FavoritesScreen
import com.example.consecutivepractices.ui.screens.FilterScreen
import com.example.consecutivepractices.ui.screens.ProfileEditScreen
import com.example.consecutivepractices.ui.screens.ProfileScreen
import com.example.consecutivepractices.ui.theme.BookAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNotificationPermission()

        setContent {
            BookAppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    topBar = {
                        when (currentRoute) {
                            NavigationRoutes.BOOK_DETAILS -> BookDetailsTopAppBar(navController)
                            NavigationRoutes.FILTERS -> FilterTopAppBar(navController)
                            NavigationRoutes.FAVORITES -> FavoritesTopAppBar(navController)
                            NavigationRoutes.PROFILE -> ProfileTopAppBar(navController)
                            NavigationRoutes.PROFILE_EDIT -> ProfileEditTopAppBar(navController, navBackStackEntry)
                        }
                    },
                    bottomBar = {
                        if (currentRoute != NavigationRoutes.BOOK_DETAILS &&
                            currentRoute != NavigationRoutes.FILTERS &&
                            currentRoute != NavigationRoutes.PROFILE_EDIT) {
                            BottomNavigationBar(navController, currentRoute)
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController,
                        startDestination = NavigationRoutes.BOOK_LIST,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable(NavigationRoutes.BOOK_LIST) {
                            BookListScreen(navController = navController)
                        }
                        composable("${NavigationRoutes.BOOK_DETAILS}/{bookId}") { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()
                            BookDetailsScreen(
                                navController = navController,
                                bookId = bookId as String?
                            )
                        }
                        composable(NavigationRoutes.FILTERS) {
                            FilterScreen(navController = navController)
                        }
                        composable(NavigationRoutes.FAVORITES) {
                            FavoritesScreen(navController = navController)
                        }
                        composable(NavigationRoutes.PROFILE) {
                            ProfileScreen(navController = navController)
                        }
                        composable(NavigationRoutes.PROFILE_EDIT) {
                            ProfileEditScreen(navController = navController)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopAppBar(navController: androidx.navigation.NavController) {
    TopAppBar(
        title = { Text("Профиль") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(NavigationRoutes.PROFILE_EDIT) {
                        launchSingleTop = true
                    }
                }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Редактировать профиль")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditTopAppBar(
    navController: androidx.navigation.NavController,
    navBackStackEntry: androidx.navigation.NavBackStackEntry?
) {
    var isSaving by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            navController.popBackStack()
        }
    }

    TopAppBar(
        title = { Text("Редактирование профиля") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(
                onClick = {
                    isSaving = true

                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(1000)
                        isSaving = false
                        saveSuccess = true
                    }
                },
                enabled = !isSaving
            ) {
                if (isSaving) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = "Сохранить изменения")
                }
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == NavigationRoutes.HOME,
            onClick = { navController.navigate(NavigationRoutes.HOME) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == NavigationRoutes.BOOK_LIST,
            onClick = { navController.navigate(NavigationRoutes.BOOK_LIST) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Books") },
            label = { Text("Books") }
        )
        NavigationBarItem(
            selected = currentRoute == NavigationRoutes.FAVORITES,
            onClick = { navController.navigate(NavigationRoutes.FAVORITES) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") }
        )
        NavigationBarItem(
            selected = currentRoute == NavigationRoutes.VIDEO,
            onClick = { navController.navigate(NavigationRoutes.VIDEO) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Video") },
            label = { Text("Video") }
        )
        NavigationBarItem(
            selected = currentRoute == NavigationRoutes.PROFILE,
            onClick = { navController.navigate(NavigationRoutes.PROFILE) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}