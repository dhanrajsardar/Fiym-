package com.example.ui

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MessageEntity
import com.example.data.local.PreferencesManager
import com.example.data.repository.BroRepository
import com.example.service.BroForegroundService
import com.example.service.FloatingOrbService
import com.example.service.ScreenRecordingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BroViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val db = AppDatabase.getInstance(application)
    private val repository = BroRepository(db.messageDao(), preferencesManager)

    val isServiceEnabled: StateFlow<Boolean> = preferencesManager.isServiceEnabled
    val isLockScreenMessagesEnabled: StateFlow<Boolean> = preferencesManager.isLockScreenMessagesEnabled
    val messageFrequencyMinutes: StateFlow<Int> = preferencesManager.messageFrequencyMinutes
    val isScreenContextEnabled: StateFlow<Boolean> = preferencesManager.isScreenContextEnabled
    val lastMotivationalMessage: StateFlow<String> = preferencesManager.lastMotivationalMessage
    val apiKey: StateFlow<String> = preferencesManager.apiKey

    val allMessages: StateFlow<List<MessageEntity>> = repository.allMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _todayCheckIns = MutableStateFlow(0)
    val todayCheckIns: StateFlow<Int> = _todayCheckIns.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _isScreenRecording = MutableStateFlow(ScreenRecordingService.isRecording)
    val isScreenRecording: StateFlow<Boolean> = _isScreenRecording.asStateFlow()

    private val _activeContext = MutableStateFlow<String?>(ScreenRecordingService.currentContextInfo)
    val activeContext: StateFlow<String?> = _activeContext.asStateFlow()

    init {
        refreshTodayCheckIns()
    }

    fun refreshTodayCheckIns() {
        viewModelScope.launch {
            _todayCheckIns.value = repository.getTodayCheckInCount()
            _isScreenRecording.value = ScreenRecordingService.isRecording
            _activeContext.value = ScreenRecordingService.currentContextInfo
        }
    }

    fun toggleService(enabled: Boolean) {
        preferencesManager.setServiceEnabled(enabled)
        val context = getApplication<Application>().applicationContext
        try {
            val broIntent = Intent(context, BroForegroundService::class.java)
            val orbIntent = Intent(context, FloatingOrbService::class.java)

            if (enabled) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(broIntent)
                    } else {
                        context.startService(broIntent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (android.provider.Settings.canDrawOverlays(context)) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(orbIntent)
                        } else {
                            context.startService(orbIntent)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                try {
                    context.stopService(broIntent)
                    context.stopService(orbIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setLockScreenMessages(enabled: Boolean) {
        preferencesManager.setLockScreenMessagesEnabled(enabled)
    }

    fun setMessageFrequency(minutes: Int) {
        preferencesManager.setMessageFrequencyMinutes(minutes)
    }

    fun setScreenContextEnabled(enabled: Boolean) {
        preferencesManager.setScreenContextEnabled(enabled)
    }

    fun saveApiKey(key: String) {
        preferencesManager.setApiKey(key)
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            _isThinking.value = true
            val contextText = ScreenRecordingService.currentContextInfo
            repository.sendMessageToBro(userText, contextText)
            _isThinking.value = false
            refreshTodayCheckIns()
        }
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            repository.deleteMessage(id)
            refreshTodayCheckIns()
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            refreshTodayCheckIns()
        }
    }

    fun updateScreenRecordingState(recording: Boolean) {
        _isScreenRecording.value = recording
        _activeContext.value = ScreenRecordingService.currentContextInfo
    }
}
