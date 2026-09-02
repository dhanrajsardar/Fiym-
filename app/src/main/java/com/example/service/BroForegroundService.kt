package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.MainActivity
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager
import com.example.data.repository.BroRepository
import com.example.worker.LockscreenMessageWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class BroForegroundService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var repository: BroRepository

    companion object {
        const val LOCKSCREEN_CHANNEL_ID = "bro_lockscreen_messages_channel"
        const val NOTIFICATION_ID = 1002
        var isRunning = false
            private set
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        preferencesManager = PreferencesManager(applicationContext)
        val db = AppDatabase.getInstance(applicationContext)
        repository = BroRepository(db.messageDao(), preferencesManager)

        createNotificationChannel()
        val initialMsg = preferencesManager.getLastMotivationalMessage().ifBlank { "Wealth Guide is active" }
        startForeground(NOTIFICATION_ID, buildLockscreenNotification(initialMsg))

        scheduleWorkManagerRotation()
        startPeriodicNotificationUpdates()
    }

    private fun scheduleWorkManagerRotation() {
        try {
            val freqMins = preferencesManager.getMessageFrequencyMinutes().coerceAtLeast(15)
            val workRequest = PeriodicWorkRequestBuilder<LockscreenMessageWorker>(
                freqMins.toLong(), TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "LockscreenMessageWork",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startPeriodicNotificationUpdates() {
        serviceScope.launch {
            while (isRunning) {
                if (preferencesManager.getLockScreenMessagesEnabled()) {
                    val contextText = ScreenRecordingService.currentContextInfo
                    val newMsg = repository.generateMotivationalMessage(contextText)
                    updateNotification(newMsg)
                }
                val intervalMins = preferencesManager.getMessageFrequencyMinutes().coerceAtLeast(15)
                delay(intervalMins * 60 * 1000L)
            }
        }
    }

    private fun updateNotification(message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildLockscreenNotification(message))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                LOCKSCREEN_CHANNEL_ID,
                "Fiym · Manifest Your Dreams",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Displays empowering manifestation and mindset insights"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildLockscreenNotification(messageText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, LOCKSCREEN_CHANNEL_ID)
            .setContentTitle("Fiym Wealth Insight")
            .setContentText(messageText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceJob.cancel()
    }
}
