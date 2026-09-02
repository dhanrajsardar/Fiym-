package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.BroQuotes
import com.example.data.local.MessageDao
import com.example.data.local.MessageEntity
import com.example.data.local.PreferencesManager
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Calendar

class BroRepository(
    private val messageDao: MessageDao,
    private val preferencesManager: PreferencesManager
) {
    val allMessages: Flow<List<MessageEntity>> = messageDao.getAllMessages()

    suspend fun getTodayCheckInCount(): Int = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        messageDao.getTodayCheckInCount(calendar.timeInMillis)
    }

    suspend fun sendMessageToBro(userText: String, screenContext: String? = null): String = withContext(Dispatchers.IO) {
        val userMsg = MessageEntity(
            text = userText,
            isFromBro = false,
            timestamp = System.currentTimeMillis(),
            contextInfo = screenContext
        )
        messageDao.insertMessage(userMsg)

        val apiKey = getEffectiveApiKey()
        var broResponseText: String

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = "You are Bro, a wise and empowering wealth consciousness guide. Your purpose is to help users build abundance, wealth consciousness, and financial freedom.\n\n" +
                        "Your personality:\n" +
                        "- Calm, wise, and supportive\n" +
                        "- Speaks about: Abundance, financial freedom, mindset shifts, value creation, conscious spending, and wealth building\n" +
                        "- Language style: Gentle, inspiring, empowering, yet direct\n" +
                        "- You help users align their mindset with wealth and abundance\n\n" +
                        "You do not use emojis or casual slang. You speak with clarity, wisdom, and purpose."

                val userPrompt = if (!screenContext.isNullOrBlank()) {
                    "Context of what the user is doing on their device right now: [$screenContext]. " +
                            "User message: \"$userText\". Respond as the Wealth Consciousness Guide with empowering, wise insight on mindset or abundance!"
                } else {
                    userText
                }

                val request = GeminiRequest(
                    systemInstruction = GeminiContent(
                        parts = listOf(GeminiPart(text = systemPrompt))
                    ),
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = userPrompt)),
                            role = "user"
                        )
                    )
                )

                val response = GeminiApiClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                broResponseText = if (!text.isNullOrBlank()) {
                    text.trim()
                } else {
                    getFallbackBroResponse(userText, screenContext)
                }
            } catch (e: HttpException) {
                broResponseText = when (e.code()) {
                    400, 401, 403 -> "Please add your Gemini API key in Settings"
                    429 -> "Bro is tired. Please wait a moment."
                    else -> "Bro is thinking... taking longer than usual."
                }
            } catch (e: UnknownHostException) {
                broResponseText = "Bro is offline. Check your connection."
            } catch (e: SocketTimeoutException) {
                broResponseText = "Bro is thinking... taking longer than usual."
            } catch (e: IOException) {
                broResponseText = "Bro is offline. Check your connection."
            } catch (e: Exception) {
                e.printStackTrace()
                broResponseText = "Bro is thinking... taking longer than usual."
            }
        } else {
            broResponseText = getFallbackBroResponse(userText, screenContext)
        }

        val broMsg = MessageEntity(
            text = broResponseText,
            isFromBro = true,
            timestamp = System.currentTimeMillis(),
            contextInfo = screenContext
        )
        messageDao.insertMessage(broMsg)
        preferencesManager.setLastMotivationalMessage(broResponseText)

        broResponseText
    }

    suspend fun generateMotivationalMessage(screenContext: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        var quote: String

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = "You are Bro, a wise wealth consciousness guide. " +
                        "Generate ONE brief, empowering wealth mindset quote or insight (under 12 words). " +
                        "Do not use emojis."

                val prompt = if (!screenContext.isNullOrBlank()) {
                    "User activity context: $screenContext. Provide one concise, powerful wealth mindset insight."
                } else {
                    "Provide a powerful wealth consciousness insight for today."
                }

                val request = GeminiRequest(
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)), role = "user"))
                )

                val response = GeminiApiClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                quote = if (!text.isNullOrBlank()) text.trim() else BroQuotes.getRandomQuote()
            } catch (e: Exception) {
                quote = BroQuotes.getRandomQuote()
            }
        } else {
            quote = BroQuotes.getRandomQuote()
        }

        preferencesManager.setLastMotivationalMessage(quote)
        quote
    }

    suspend fun deleteMessage(id: Long) = withContext(Dispatchers.IO) {
        messageDao.deleteMessageById(id)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        messageDao.clearAllMessages()
    }

    private fun getEffectiveApiKey(): String {
        val customKey = preferencesManager.getApiKey()
        return if (customKey.isNotBlank()) customKey else BuildConfig.GEMINI_API_KEY
    }

    private fun getFallbackBroResponse(userText: String, screenContext: String?): String {
        val lower = userText.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
                "Wealth begins with the right mindset. What are you thinking about money right now?"
            lower.contains("fear") || lower.contains("worry") || lower.contains("stress") ->
                "Your thoughts create your financial reality. Let us work on shifting your money mindset toward abundance."
            lower.contains("goal") || lower.contains("dream") || lower.contains("future") ->
                "Money is a tool for your purpose. What purpose are you building wealth for?"
            lower.contains("action") || lower.contains("start") || lower.contains("habit") ->
                "Small actions lead to big wealth. What small aligned action can you take today?"
            lower.contains("spend") || lower.contains("buy") || lower.contains("expense") ->
                "Every expense is a choice. Choose wisely and direct resources to what creates lasting value."
            screenContext != null ->
                "Value creation is the path to wealth. How are you creating value right now?"
            else ->
                "Abundance is your natural state. Focus on growth, value creation, and mindful discipline."
        }
    }
}
