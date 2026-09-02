package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("bro_prefs", Context.MODE_PRIVATE)

    private val _isServiceEnabled = MutableStateFlow(getServiceEnabled())
    val isServiceEnabled: StateFlow<Boolean> = _isServiceEnabled.asStateFlow()

    private val _isLockScreenMessagesEnabled = MutableStateFlow(getLockScreenMessagesEnabled())
    val isLockScreenMessagesEnabled: StateFlow<Boolean> = _isLockScreenMessagesEnabled.asStateFlow()

    private val _messageFrequencyMinutes = MutableStateFlow(getMessageFrequencyMinutes())
    val messageFrequencyMinutes: StateFlow<Int> = _messageFrequencyMinutes.asStateFlow()

    private val _isScreenContextEnabled = MutableStateFlow(getScreenContextEnabled())
    val isScreenContextEnabled: StateFlow<Boolean> = _isScreenContextEnabled.asStateFlow()

    private val _lastMotivationalMessage = MutableStateFlow(getLastMotivationalMessage())
    val lastMotivationalMessage: StateFlow<String> = _lastMotivationalMessage.asStateFlow()

    private val _apiKey = MutableStateFlow(getApiKey())
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    fun getServiceEnabled(): Boolean = prefs.getBoolean(KEY_SERVICE_ENABLED, true)
    fun setServiceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SERVICE_ENABLED, enabled).apply()
        _isServiceEnabled.value = enabled
    }

    fun getLockScreenMessagesEnabled(): Boolean = prefs.getBoolean(KEY_LOCK_SCREEN_MESSAGES, true)
    fun setLockScreenMessagesEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOCK_SCREEN_MESSAGES, enabled).apply()
        _isLockScreenMessagesEnabled.value = enabled
    }

    fun getMessageFrequencyMinutes(): Int = prefs.getInt(KEY_MESSAGE_FREQUENCY, 30)
    fun setMessageFrequencyMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_MESSAGE_FREQUENCY, minutes).apply()
        _messageFrequencyMinutes.value = minutes
    }

    fun getScreenContextEnabled(): Boolean = prefs.getBoolean(KEY_SCREEN_CONTEXT, false)
    fun setScreenContextEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SCREEN_CONTEXT, enabled).apply()
        _isScreenContextEnabled.value = enabled
    }

    fun getLastMotivationalMessage(): String =
        prefs.getString(KEY_LAST_MESSAGE, "Good morning, champ! ☀️") ?: "Good morning, champ! ☀️"

    fun setLastMotivationalMessage(message: String) {
        prefs.edit().putString(KEY_LAST_MESSAGE, message).apply()
        _lastMotivationalMessage.value = message
    }

    fun getApiKey(): String = prefs.getString(KEY_API_KEY, "") ?: ""
    fun setApiKey(key: String) {
        prefs.edit().putString(KEY_API_KEY, key).apply()
        _apiKey.value = key
    }

    companion object {
        private const val KEY_SERVICE_ENABLED = "key_service_enabled"
        private const val KEY_LOCK_SCREEN_MESSAGES = "key_lock_screen_messages"
        private const val KEY_MESSAGE_FREQUENCY = "key_message_frequency"
        private const val KEY_SCREEN_CONTEXT = "key_screen_context"
        private const val KEY_LAST_MESSAGE = "key_last_message"
        private const val KEY_API_KEY = "key_api_key"
    }
}
