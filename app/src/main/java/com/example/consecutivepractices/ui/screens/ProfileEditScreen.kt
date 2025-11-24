package com.example.consecutivepractices.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.consecutivepractices.utils.ImagePicker
import com.example.consecutivepractices.utils.PermissionManager
import com.example.consecutivepractices.utils.rememberCameraPermissionLauncher
import com.example.consecutivepractices.utils.rememberGalleryPermissionLauncher
import com.example.consecutivepractices.viewmodel.ProfileEditViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    navController: NavController,
    viewModel: ProfileEditViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val timeError by viewModel.timeError.collectAsState()
    val context = LocalContext.current

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf(profile.fullName) }
    var resumeUrl by remember { mutableStateOf(profile.resumeUrl) }
    var position by remember { mutableStateOf(profile.position) }
    var favoritePairTime by remember { mutableStateOf(profile.favoritePairTime) }

    val timePickerState = rememberTimePickerState(
        initialHour = getSafeHour(favoritePairTime),
        initialMinute = getSafeMinute(favoritePairTime)
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.updateAvatarUri(it.toString())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
        }
    }

    val galleryPermissionLauncher = rememberGalleryPermissionLauncher(
        onGranted = {
            galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onDenied = {
        }
    )

    val cameraPermissionLauncher = rememberCameraPermissionLauncher(
        onGranted = {
            val uri = ImagePicker.createImageUri(context)
            viewModel.updateAvatarUri(uri.toString())
            cameraLauncher.launch(uri)
        },
        onDenied = {
        }
    )

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            navController.popBackStack()
            viewModel.resetSaveSuccess()
        }
    }

    LaunchedEffect(profile) {
        fullName = profile.fullName
        resumeUrl = profile.resumeUrl
        position = profile.position
        favoritePairTime = profile.favoritePairTime
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { showImageSourceDialog = true },
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

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Изменить фото",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Нажмите для изменения фото",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("ФИО *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = fullName.isBlank()
        )

        OutlinedTextField(
            value = position,
            onValueChange = { position = it },
            label = { Text("Должность") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = resumeUrl,
            onValueChange = { resumeUrl = it },
            label = { Text("Ссылка на резюме (URL)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            placeholder = { Text("https://example.com/resume.pdf") }
        )

        OutlinedTextField(
            value = favoritePairTime,
            onValueChange = {
                favoritePairTime = it
                viewModel.updateFavoritePairTime(it)
            },
            label = { Text("Время любимой пары") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("HH:mm") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = timeError != null,
            trailingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Выбрать время",
                    modifier = Modifier.clickable { showTimePicker = true }
                )
            }
        )

        if (timeError != null) {
            Text(
                text = timeError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                viewModel.updateFullName(fullName)
                viewModel.updateResumeUrl(resumeUrl)
                viewModel.updatePosition(position)
                viewModel.updateFavoritePairTime(favoritePairTime)
                viewModel.saveProfile(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isSaving && fullName.isNotBlank() && timeError == null,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text("Сохранение...")
            } else {
                Icon(Icons.Default.Check, contentDescription = "Сохранить")
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text("Готово")
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Выберите источник") },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageSourceDialog = false
                                if (PermissionManager.hasGalleryPermission(context)) {
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else {
                                    galleryPermissionLauncher.launch(PermissionManager.getGalleryPermissions())
                                }
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Face, contentDescription = "Галерея")
                        Text("Галерея")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageSourceDialog = false
                                if (PermissionManager.hasCameraPermission(context)) {
                                    val uri = ImagePicker.createImageUri(context)
                                    viewModel.updateAvatarUri(uri.toString())
                                    cameraLauncher.launch(uri)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Камера")
                        Text("Камера")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showImageSourceDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Выберите время") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                Button(onClick = {
                    val hour = String.format("%02d", timePickerState.hour)
                    val minute = String.format("%02d", timePickerState.minute)
                    val time = "$hour:$minute"
                    favoritePairTime = time
                    viewModel.updateFavoritePairTime(time)
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showTimePicker = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

private fun getSafeHour(time: String): Int {
    return try {
        if (time.isNotBlank() && time.contains(":")) {
            val parts = time.split(":")
            if (parts.size >= 2) {
                parts[0].toInt().coerceIn(0, 23)
            } else {
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            }
        } else {
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        }
    } catch (e: Exception) {
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }
}

private fun getSafeMinute(time: String): Int {
    return try {
        if (time.isNotBlank() && time.contains(":")) {
            val parts = time.split(":")
            if (parts.size >= 2) {
                parts[1].toInt().coerceIn(0, 59)
            } else {
                Calendar.getInstance().get(Calendar.MINUTE)
            }
        } else {
            Calendar.getInstance().get(Calendar.MINUTE)
        }
    } catch (e: Exception) {
        Calendar.getInstance().get(Calendar.MINUTE)
    }
}