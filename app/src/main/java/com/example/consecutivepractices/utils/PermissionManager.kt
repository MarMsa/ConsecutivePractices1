package com.example.consecutivepractices.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

object PermissionManager {

    fun getGalleryPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun getCameraPermissions(): Array<String> {
        return arrayOf(Manifest.permission.CAMERA)
    }

    fun hasGalleryPermission(context: Context): Boolean {
        return getGalleryPermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasCameraPermission(context: Context): Boolean {
        return getCameraPermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}

@Composable
fun rememberGalleryPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit = {}
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    val allGranted = permissions.values.all { it }
    if (allGranted) {
        onGranted()
    } else {
        onDenied()
    }
}

@Composable
fun rememberCameraPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit = {}
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { granted ->
    if (granted) {
        onGranted()
    } else {
        onDenied()
    }
}