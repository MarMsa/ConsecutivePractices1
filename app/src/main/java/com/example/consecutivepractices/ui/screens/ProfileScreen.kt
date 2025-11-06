package com.example.consecutivepractices.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.consecutivepractices.utils.FileDownloader
import com.example.consecutivepractices.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var isDownloading by remember { mutableStateOf(false) }
    var downloadResult by remember { mutableStateOf<FileDownloader.DownloadResult?>(null) }
    var showBrowserDialog by remember { mutableStateOf(false) }

    LaunchedEffect(profile) {
        downloadResult = null
    }

    LaunchedEffect(downloadResult) {
        downloadResult?.let { result ->
            if (result.shouldOpenInBrowser()) {
                showBrowserDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("profile_edit") {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profile.avatarUri.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(profile.avatarUri)
                                        .build()
                                ),
                                contentDescription = "Аватар",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Аватар",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            ProfileField(
                                label = "ФИО",
                                value = profile.fullName.ifBlank { "Не указано" }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            ProfileField(
                                label = "Должность",
                                value = profile.position.ifBlank { "Не указана" }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            ProfileField(
                                label = "Email",
                                value = profile.email.ifBlank { "Не указан" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    downloadResult?.let { result ->
                        val isError = result !is FileDownloader.DownloadResult.Success &&
                                result !is FileDownloader.DownloadResult.OpenedInBrowser
                        val icon = when {
                            result is FileDownloader.DownloadResult.Success -> Icons.Default.Check
                            result is FileDownloader.DownloadResult.OpenedInBrowser -> Icons.Default.Send
                            else -> Icons.Default.Warning
                        }
                        val color = when {
                            result is FileDownloader.DownloadResult.Success -> MaterialTheme.colorScheme.primaryContainer
                            result is FileDownloader.DownloadResult.OpenedInBrowser -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.errorContainer
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = color)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = "Результат",
                                    tint = if (isError) MaterialTheme.colorScheme.onErrorContainer
                                    else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = result.getErrorMessage(context),
                                    color = if (isError) MaterialTheme.colorScheme.onErrorContainer
                                    else MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (profile.resumeUrl.isNotBlank()) {
                        Button(
                            onClick = {
                                isDownloading = true
                                downloadResult = null

                                kotlinx.coroutines.GlobalScope.launch {
                                    try {
                                        val result = FileDownloader.downloadAndOpenFile(
                                            context = context,
                                            url = profile.resumeUrl,
                                            fileName = "resume_${profile.fullName}.pdf"
                                        )
                                        downloadResult = result
                                    } catch (e: Exception) {
                                        downloadResult = FileDownloader.DownloadResult.UnknownError
                                    } finally {
                                        isDownloading = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isDownloading
                        ) {
                            if (isDownloading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                Text("Загрузка...")
                            } else {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Скачать",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                Text("Скачать резюме")
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Резюме не добавлено",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showBrowserDialog) {
            AlertDialog(
                onDismissRequest = {
                    showBrowserDialog = false
                    downloadResult = null
                },
                title = { Text("Не удалось скачать файл") },
                text = {
                    Text("Хотите открыть ссылку в браузере?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showBrowserDialog = false
                            val opened = FileDownloader.openUrlInBrowser(context, profile.resumeUrl)
                            downloadResult = if (opened) {
                                FileDownloader.DownloadResult.OpenedInBrowser
                            } else {
                                FileDownloader.DownloadResult.NoAppToOpenFile
                            }
                        }
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Открыть в браузере")
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Text("Открыть в браузере")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showBrowserDialog = false
                            downloadResult = null
                        }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}