package com.example.core.utils

import android.content.Context
import android.content.Intent

interface NotificationHandler {
    fun createNotificationIntent(context: Context): Intent
}