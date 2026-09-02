package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager
import com.example.data.repository.BroRepository
import com.example.service.BroForegroundService
import com.example.service.ScreenRecordingService

class LockscreenMessageWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = PreferencesManager(applicationContext)
        if (!prefs.getLockScreenMessagesEnabled()) return Result.success()

        val db = AppDatabase.getInstance(applicationContext)
        val repository = BroRepository(db.messageDao(), prefs)

        val contextInfo = ScreenRecordingService.currentContextInfo
        val newMsg = repository.generateMotivationalMessage(contextInfo)

        showLockscreenNotification(applicationContext, newMsg)

        return Result.success()
    }

    private fun showLockscreenNotification(context: Context, messageText: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = BroForegroundService.LOCKSCREEN_CHANNEL_ID

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Fiym · Manifest Your Dreams",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Displays empowering manifestation and mindset insights"
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Fiym Wealth Insight")
            .setContentText(messageText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(BroForegroundService.NOTIFICATION_ID, notification)
    }
}
