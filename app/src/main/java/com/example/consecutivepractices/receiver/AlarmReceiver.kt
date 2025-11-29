package com.example.consecutivepractices.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.consecutivepractices.service.NotificationService

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val userName = intent.getStringExtra(USER_NAME_EXTRA) ?: "Студент"
        val message = "пора на пару по мобильной разработке!"

        val notificationService = NotificationService(context)
        notificationService.showNotification(userName, message)
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