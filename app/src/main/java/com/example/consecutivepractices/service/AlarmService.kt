package com.example.consecutivepractices.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.consecutivepractices.receiver.AlarmReceiver
import java.util.Calendar

class AlarmService(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNotification(time: String, userName: String) {
        val (hour, minute) = parseTimeSafe(time)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = AlarmReceiver.createIntent(context, userName)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(time),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelNotification(time: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(time),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun parseTimeSafe(time: String): Pair<Int, Int> {
        return try {
            val parts = time.split(":")
            if (parts.size >= 2) {
                Pair(parts[0].toInt().coerceIn(0, 23), parts[1].toInt().coerceIn(0, 59))
            } else {
                Pair(12, 0)
            }
        } catch (e: Exception) {
            Pair(12, 0)
        }
    }

    private fun getRequestCode(time: String): Int {
        return try {
            time.replace(":", "").toInt()
        } catch (e: Exception) {
            1234
        }
    }
}