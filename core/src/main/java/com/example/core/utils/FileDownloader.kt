package com.example.core.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

object FileDownloader {

    private const val TAG = "FileDownloader"

    suspend fun downloadAndOpenFile(
        context: Context,
        url: String,
        fileName: String
    ): DownloadResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Processing URL: $url")

                if (url.isBlank() || !url.startsWith("http")) {
                    Log.w(TAG, "Invalid URL: $url")
                    return@withContext DownloadResult.InvalidUrl
                }

                if (isGoogleDriveUrl(url)) {
                    Log.d(TAG, "Google Drive URL detected, opening in browser")
                    val opened = openUrlInBrowser(context, url)
                    return@withContext if (opened) {
                        DownloadResult.OpenedInBrowser
                    } else {
                        val alternativeOpened = tryAlternativeBrowserMethods(context, url)
                        if (alternativeOpened) {
                            DownloadResult.OpenedInBrowser
                        } else {
                            DownloadResult.NoAppToOpenFile
                        }
                    }
                }

                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.w(TAG, "Network error: ${response.code}")
                        val opened = openUrlInBrowser(context, url)
                        return@withContext if (opened) {
                            DownloadResult.OpenedInBrowser
                        } else {
                            val alternativeOpened = tryAlternativeBrowserMethods(context, url)
                            if (alternativeOpened) {
                                DownloadResult.OpenedInBrowser
                            } else {
                                DownloadResult.NetworkError(response.code)
                            }
                        }
                    }

                    val contentType = response.header("Content-Type", "")
                    if (!isSupportedFileType(contentType, url)) {
                        Log.w(TAG, "Unsupported file type: $contentType")
                        val opened = openUrlInBrowser(context, url)
                        return@withContext if (opened) {
                            DownloadResult.OpenedInBrowser
                        } else {
                            val alternativeOpened = tryAlternativeBrowserMethods(context, url)
                            if (alternativeOpened) {
                                DownloadResult.OpenedInBrowser
                            } else {
                                DownloadResult.UnsupportedFileType
                            }
                        }
                    }

                    response.body?.bytes()?.let { bytes ->
                        if (bytes.isEmpty()) {
                            return@withContext DownloadResult.EmptyFile
                        }

                        val downloadsDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        } else {
                            File(context.getExternalFilesDir(null), "Downloads").apply {
                                if (!exists()) mkdirs()
                            }
                        }

                        val finalFileName = if (fileName.isBlank()) {
                            generateFileName(url, contentType)
                        } else {
                            fileName
                        }

                        val file = File(downloadsDir, finalFileName)

                        FileOutputStream(file).use { fos ->
                            fos.write(bytes)
                        }

                        if (file.exists() && file.length() > 0) {
                            val opened = openFile(context, file)
                            return@withContext if (opened) {
                                DownloadResult.Success
                            } else {
                                val browserOpened = openUrlInBrowser(context, url)
                                if (browserOpened) {
                                    DownloadResult.OpenedInBrowser
                                } else {
                                    val alternativeOpened = tryAlternativeBrowserMethods(context, url)
                                    if (alternativeOpened) {
                                        DownloadResult.OpenedInBrowser
                                    } else {
                                        DownloadResult.NoAppToOpenFile
                                    }
                                }
                            }
                        } else {
                            return@withContext DownloadResult.FileCreationFailed
                        }
                    }
                }
                DownloadResult.UnknownError
            } catch (e: IOException) {
                Log.e(TAG, "IO Exception: ${e.message}")
                e.printStackTrace()
                val opened = openUrlInBrowser(context, url)
                if (opened) {
                    DownloadResult.OpenedInBrowser
                } else {
                    val alternativeOpened = tryAlternativeBrowserMethods(context, url)
                    if (alternativeOpened) {
                        DownloadResult.OpenedInBrowser
                    } else {
                        DownloadResult.NetworkError(-1)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
                e.printStackTrace()
                val opened = openUrlInBrowser(context, url)
                if (opened) {
                    DownloadResult.OpenedInBrowser
                } else {
                    val alternativeOpened = tryAlternativeBrowserMethods(context, url)
                    if (alternativeOpened) {
                        DownloadResult.OpenedInBrowser
                    } else {
                        DownloadResult.UnknownError
                    }
                }
            }
        }
    }

    private fun isGoogleDriveUrl(url: String): Boolean {
        return url.contains("drive.google.com") ||
                url.contains("docs.google.com")
    }

    private fun isSupportedFileType(contentType: String?, url: String): Boolean {
        val lowerContentType = contentType?.lowercase() ?: ""
        val lowerUrl = url.lowercase()

        return lowerContentType.contains("pdf") ||
                lowerContentType.contains("msword") ||
                lowerContentType.contains("wordprocessingml") ||
                lowerContentType.contains("text/plain") ||
                lowerUrl.endsWith(".pdf") ||
                lowerUrl.endsWith(".doc") ||
                lowerUrl.endsWith(".docx") ||
                lowerUrl.endsWith(".txt")
    }

    private fun generateFileName(url: String, contentType: String?): String {
        val timeStamp = System.currentTimeMillis()
        val extension = when {
            contentType?.contains("pdf") == true -> ".pdf"
            contentType?.contains("msword") == true -> ".doc"
            contentType?.contains("wordprocessingml") == true -> ".docx"
            contentType?.contains("text/plain") == true -> ".txt"
            url.endsWith(".pdf", ignoreCase = true) -> ".pdf"
            url.endsWith(".doc", ignoreCase = true) -> ".doc"
            url.endsWith(".docx", ignoreCase = true) -> ".docx"
            url.endsWith(".txt", ignoreCase = true) -> ".txt"
            else -> ".download"
        }
        return "resume_$timeStamp$extension"
    }

    private fun openFile(context: Context, file: File): Boolean {
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, getMimeType(file.name))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val packageManager = context.packageManager
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening file: ${e.message}")
            false
        }
    }

    fun openUrlInBrowser(context: Context, url: String): Boolean {
        return try {
            Log.d(TAG, "Opening in browser: $url")

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addCategory(Intent.CATEGORY_BROWSABLE)
            }

            val packageManager = context.packageManager

            val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            Log.d(TAG, "Found ${activities.size} activities that can handle the URL")

            if (activities.isNotEmpty()) {
                context.startActivity(intent)
                Log.d(TAG, "Successfully opened URL in browser")
                true
            } else {
                Log.w(TAG, "No activities found to handle the URL")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening URL in browser: ${e.message}")
            false
        }
    }

    private fun tryAlternativeBrowserMethods(context: Context, url: String): Boolean {
        Log.d(TAG, "Trying alternative browser methods for: $url")

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            val packageManager = context.packageManager
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
                Log.d(TAG, "Successfully opened with alternative method 1")
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Alternative method 1 failed: ${e.message}")
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                type = "text/html"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val packageManager = context.packageManager
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
                Log.d(TAG, "Successfully opened with alternative method 2")
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Alternative method 2 failed: ${e.message}")
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooserIntent = Intent.createChooser(intent, "Открыть ссылку")
            context.startActivity(chooserIntent)
            Log.d(TAG, "Successfully opened with chooser")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Alternative method 3 (chooser) failed: ${e.message}")
        }

        Log.w(TAG, "All alternative methods failed for URL: $url")
        return false
    }

    private fun getMimeType(fileName: String): String {
        return when {
            fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
            fileName.endsWith(".doc", ignoreCase = true) -> "application/msword"
            fileName.endsWith(".docx", ignoreCase = true) -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            fileName.endsWith(".jpg", ignoreCase = true) -> "image/jpeg"
            fileName.endsWith(".png", ignoreCase = true) -> "image/png"
            fileName.endsWith(".txt", ignoreCase = true) -> "text/plain"
            else -> "*/*"
        }
    }

    sealed class DownloadResult {
        object Success : DownloadResult()
        object OpenedInBrowser : DownloadResult()
        object InvalidUrl : DownloadResult()
        object EmptyFile : DownloadResult()
        object FileCreationFailed : DownloadResult()
        object NoAppToOpenFile : DownloadResult()
        object UnknownError : DownloadResult()
        object UnsupportedFileType : DownloadResult()
        data class NetworkError(val code: Int) : DownloadResult()

        fun getErrorMessage(context: Context): String {
            return when (this) {
                is Success -> "Файл успешно открыт"
                is OpenedInBrowser -> "Ссылка открыта в браузере"
                is InvalidUrl -> "Некорректная ссылка"
                is EmptyFile -> "Файл пустой"
                is FileCreationFailed -> "Не удалось сохранить файл"
                is NoAppToOpenFile -> "Не удалось открыть ссылку. Проверьте наличие браузера"
                is UnknownError -> "Неизвестная ошибка"
                is UnsupportedFileType -> "Неподдерживаемый тип файла"
                is NetworkError -> "Ошибка сети: код $code"
            }
        }

        fun shouldOpenInBrowser(): Boolean {
            return when (this) {
                is NetworkError -> true
                is NoAppToOpenFile -> true
                is FileCreationFailed -> true
                is UnsupportedFileType -> true
                else -> false
            }
        }
    }
}