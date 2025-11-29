package com.example.consecutivepractices

import android.app.Application
import android.content.Context
import android.content.Intent
import com.example.core.utils.NotificationHandler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application(), NotificationHandler {

    override fun createNotificationIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
}