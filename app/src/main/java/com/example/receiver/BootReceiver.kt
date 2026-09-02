package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.local.PreferencesManager
import com.example.service.BroForegroundService
import com.example.service.FloatingOrbService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            val prefs = PreferencesManager(context)
            if (prefs.getServiceEnabled()) {
                val broIntent = Intent(context, BroForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(broIntent)
                } else {
                    context.startService(broIntent)
                }

                if (android.provider.Settings.canDrawOverlays(context)) {
                    val orbIntent = Intent(context, FloatingOrbService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(orbIntent)
                    } else {
                        context.startService(orbIntent)
                    }
                }
            }
        }
    }
}
