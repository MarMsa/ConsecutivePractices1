package com.example.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.core.service.NotificationService
import com.example.core.utils.NotificationHandler

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val userName = intent.getStringExtra(USER_NAME_EXTRA) ?: "Студент"
        val message = "пора на пару по мобильной разработке!"

        val notificationHandler = getNotificationHandler(context)
        val notificationService = NotificationService(context)
        notificationService.showNotification(userName, message, notificationHandler)
    }

    private fun getNotificationHandler(context: Context): NotificationHandler {
        return try {
            context.applicationContext as NotificationHandler
        } catch (e: Exception) {
            object : NotificationHandler {
                override fun createNotificationIntent(context: Context): Intent {
                    return Intent().apply {
                        action = Intent.ACTION_MAIN
                        addCategory(Intent.CATEGORY_LAUNCHER)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }
            }
        }
    }

    companion object {
        const val USER_NAME_EXTRA = "user_name"

        fun createIntent(context: Context, userName: String): Intent {
            return Intent(context, AlarmReceiver::class.java).apply {
                putExtra(USER_NAME_EXTRA, userName)
            }
        }
    }
}