package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.local.PreferencesManager

class FloatingOrbService : Service() {

    private var windowManager: WindowManager? = null
    private var orbView: View? = null
    private var orbLayoutParams: WindowManager.LayoutParams? = null

    private var bubbleView: View? = null
    private var bubbleLayoutParams: WindowManager.LayoutParams? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var dismissBubbleRunnable: Runnable? = null

    companion object {
        const val CHANNEL_ID = "bro_floating_orb_channel"
        const val NOTIFICATION_ID = 1001
        var isRunning = false
            private set
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        setupFloatingOrb()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun setupFloatingOrb() {
        if (!Settings.canDrawOverlays(this)) return

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val density = resources.displayMetrics.density
        val sizePx = (68 * density).toInt()

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val metrics = resources.displayMetrics
        val initialX = metrics.widthPixels - sizePx - (20 * density).toInt()
        val initialY = (150 * density).toInt()

        orbLayoutParams = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = initialX
            y = initialY
        }

        val broOrbView = BroOrbView(this)

        broOrbView.setOnTouchListener(object : View.OnTouchListener {
            private var lastX = 0
            private var lastY = 0
            private var startTouchX = 0f
            private var startTouchY = 0f
            private var isClick = false

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val params = orbLayoutParams ?: return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = params.x
                        lastY = params.y
                        startTouchX = event.rawX
                        startTouchY = event.rawY
                        isClick = true
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - startTouchX).toInt()
                        val dy = (event.rawY - startTouchY).toInt()
                        if (Math.abs(dx) > 8 || Math.abs(dy) > 8) {
                            isClick = false
                        }
                        params.x = lastX + dx
                        params.y = lastY + dy

                        // Dynamic 3D head and eye direction tracking
                        broOrbView.setDragOffset(dx.toFloat(), dy.toFloat())

                        windowManager?.updateViewLayout(orbView, params)
                        updateBubblePosition()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        broOrbView.releaseDrag()
                        if (isClick) {
                            broOrbView.triggerHappyBounce()
                            showBubbleMessage()
                        }
                        return true
                    }
                }
                return false
            }
        })

        orbView = broOrbView
        try {
            windowManager?.addView(orbView, orbLayoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showBubbleMessage() {
        val wm = windowManager ?: return
        val orbParams = orbLayoutParams ?: return
        val density = resources.displayMetrics.density

        val prefs = PreferencesManager(applicationContext)
        val latestQuote = prefs.getLastMotivationalMessage().ifBlank { "You got this! 💪" }
        val displayText = "🟠 Bro: $latestQuote"

        removeBubble()

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val orbHeight = orbParams.height

        bubbleLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (orbParams.x - (30 * density).toInt()).coerceAtLeast((10 * density).toInt())
            y = orbParams.y + orbHeight + (12 * density).toInt()
        }

        val bubbleContainer = FrameLayout(this).apply {
            val bgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 24 * density
                setColor(Color.WHITE)
                setStroke((1f * density).toInt(), Color.parseColor("#F0EAE6"))
            }
            background = bgDrawable
            val pHoriz = (18 * density).toInt()
            val pVert = (14 * density).toInt()
            setPadding(pHoriz, pVert, pHoriz, pVert)
            elevation = 12f
        }

        val textView = TextView(this).apply {
            text = displayText
            setTextColor(Color.parseColor("#2C1108"))
            textSize = 15f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setLineSpacing(0f, 1.25f)
        }

        bubbleContainer.addView(
            textView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        )

        bubbleContainer.setOnClickListener {
            removeBubble()
            openAppChat()
        }

        bubbleContainer.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                removeBubble()
                true
            } else {
                false
            }
        }

        bubbleView = bubbleContainer
        try {
            wm.addView(bubbleView, bubbleLayoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        dismissBubbleRunnable = Runnable {
            removeBubble()
        }
        mainHandler.postDelayed(dismissBubbleRunnable!!, 3000L)
    }

    private fun updateBubblePosition() {
        val bView = bubbleView ?: return
        val orbParams = orbLayoutParams ?: return
        val bParams = bubbleLayoutParams ?: return
        val density = resources.displayMetrics.density

        bParams.x = (orbParams.x - (30 * density).toInt()).coerceAtLeast((10 * density).toInt())
        bParams.y = orbParams.y + orbParams.height + (12 * density).toInt()

        try {
            windowManager?.updateViewLayout(bView, bParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeBubble() {
        dismissBubbleRunnable?.let { mainHandler.removeCallbacks(it) }
        dismissBubbleRunnable = null

        val bView = bubbleView
        val wm = windowManager
        if (bView != null && wm != null) {
            try {
                wm.removeView(bView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        bubbleView = null
        bubbleLayoutParams = null
    }

    private fun openAppChat() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("OPEN_CHAT", true)
        }
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Fiym Wealth Guide Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps Fiym wealth consciousness overlay active"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Wealth Guide is active")
            .setContentText("Wealth consciousness, always present")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        removeBubble()
        if (orbView != null && windowManager != null) {
            try {
                windowManager?.removeView(orbView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
