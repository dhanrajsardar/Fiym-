package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BrokenOathRecord(
    val oathText: String,
    val reason: String,
    val date: String
)

data class IAmStatement(
    val id: String,
    val statement: String,
    val category: String,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)

data class FavoriteItem(
    val id: String,
    val title: String,
    val content: String,
    val category: String, // "Affirmation", "Manifestation", "Future Self", "Oath", "I AM"
    val timestamp: Long = System.currentTimeMillis()
)

data class ManifestationItem(
    val id: String,
    val title: String,
    val date: String,
    val imageUri: String? = null,
    val drawableResId: Int? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val posX: Float = 0f,
    val posY: Float = 0f,
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val zIndex: Float = 0f
)

data class HypnagogicInsight(
    val id: String,
    val tags: List<String>,
    val note: String,
    val relaxationScore: Int,
    val vividnessScore: Int,
    val insightSummary: String,
    val actionCreated: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val dateStr: String = ""
)

data class WealthActionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val duration: String,
    val isCompleted: Boolean = false
)

data class GratitudeEntry(
    val id: String = "gratitude_${System.currentTimeMillis()}",
    val item1: String,
    val item2: String = "",
    val item3: String = "",
    val category: String = "Daily Abundance",
    val moodEmoji: String = "🙏",
    val moodLabel: String = "Grateful",
    val reflection: String = "",
    val dateStr: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class ManifestationVisionData(
    val id: String = "vision_primary",
    val intention: String = "Build a successful digital business",
    val category: String = "Achievement",
    val whatText: String = "Build a profitable online business",
    val whyText: String = "Freedom and independence",
    val whenText: String = "Within 2 years",
    val howKnowText: String = "First 100 paying customers",
    val whereAreYou: String = "Sunlit modern home studio overlooking greenery",
    val whatDoing: String = "Designing impactful software solutions calmly",
    val whoWith: String = "Supportive dream team and loved ones",
    val whatChanged: String = "Complete financial sovereignty and zero anxiety",
    val emotion: String = "Free",
    val emotionEmoji: String = "🕊️",
    val emotionWhy: String = "I want freedom because I want control over how I spend my time and create value.",
    val thinkTrait: String = "They think in abundant possibilities and long-term compounding.",
    val believeTrait: String = "They believe they are fully worthy of extraordinary wealth.",
    val doTrait: String = "They consistently take high-leverage action and finish what they start.",
    val dontTrait: String = "They do not procrastinate or seek outside validation.",
    val todayAction: String = "Finish my high-converting landing page and ship to early users.",
    val timestamp: Long = System.currentTimeMillis(),
    val dateStr: String = ""
)

class WealthIdentityStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("fiym_wealth_identity_store", Context.MODE_PRIVATE)

    // --- FUTURE SELF STATE ---
    private val _futureSelfText = MutableStateFlow(getFutureSelfText())
    val futureSelfText: StateFlow<String> = _futureSelfText.asStateFlow()

    fun getFutureSelfText(): String {
        return prefs.getString(KEY_FUTURE_SELF_TEXT, DEFAULT_FUTURE_SELF) ?: DEFAULT_FUTURE_SELF
    }

    fun saveFutureSelfText(text: String) {
        prefs.edit().putString(KEY_FUTURE_SELF_TEXT, text).apply()
        _futureSelfText.value = text
    }

    // --- THE OATH STATE ---
    private val _isOathSealed = MutableStateFlow(getIsOathSealed())
    val isOathSealed: StateFlow<Boolean> = _isOathSealed.asStateFlow()

    private val _userName = MutableStateFlow(getUserName())
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _personalOaths = MutableStateFlow(getPersonalOaths())
    val personalOaths: StateFlow<List<String>> = _personalOaths.asStateFlow()

    private val _sealedDate = MutableStateFlow(getSealedDate())
    val sealedDate: StateFlow<String> = _sealedDate.asStateFlow()

    private val _signatureSvg = MutableStateFlow(getSignatureSvg())
    val signatureSvg: StateFlow<String> = _signatureSvg.asStateFlow()

    private val _brokenRecords = MutableStateFlow(getBrokenRecords())
    val brokenRecords: StateFlow<List<BrokenOathRecord>> = _brokenRecords.asStateFlow()

    fun getIsOathSealed(): Boolean = prefs.getBoolean(KEY_OATH_SEALED, false)

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "Dhanraj") ?: "Dhanraj"

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
        _userName.value = name
    }

    fun getPersonalOaths(): List<String> {
        val raw = prefs.getString(KEY_PERSONAL_OATHS, null) ?: return listOf(
            "I will not abandon a goal just because progress is slow.",
            "I will not let fear make my decisions.",
            "I will protect at least 2 hours every day for building my future."
        )
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { arr.getString(it) }
        } catch (e: Exception) {
            listOf("I will not let fear make my decisions.")
        }
    }

    fun savePersonalOaths(oaths: List<String>) {
        val arr = JSONArray(oaths)
        prefs.edit().putString(KEY_PERSONAL_OATHS, arr.toString()).apply()
        _personalOaths.value = oaths
    }

    fun addPersonalOath(oath: String) {
        val current = _personalOaths.value.toMutableList()
        current.add(oath)
        savePersonalOaths(current)
    }

    fun removePersonalOath(index: Int) {
        val current = _personalOaths.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            savePersonalOaths(current)
        }
    }

    fun getSealedDate(): String {
        return prefs.getString(KEY_SEALED_DATE, SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date()))
            ?: "August 15, 2026"
    }

    fun getSignatureSvg(): String = prefs.getString(KEY_SIGNATURE_SVG, "") ?: ""

    fun sealTheOath(signatureData: String, name: String) {
        val today = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())
        prefs.edit()
            .putBoolean(KEY_OATH_SEALED, true)
            .putString(KEY_SEALED_DATE, today)
            .putString(KEY_SIGNATURE_SVG, signatureData)
            .putString(KEY_USER_NAME, name)
            .apply()
        _isOathSealed.value = true
        _sealedDate.value = today
        _signatureSvg.value = signatureData
        _userName.value = name
    }

    fun unsealForRewrite() {
        prefs.edit().putBoolean(KEY_OATH_SEALED, false).apply()
        _isOathSealed.value = false
    }

    fun recordBrokenOath(oath: String, reason: String) {
        val today = SimpleDateFormat("MMM d, yyyy - h:mm a", Locale.getDefault()).format(Date())
        val record = BrokenOathRecord(oath, reason, today)
        val current = _brokenRecords.value.toMutableList()
        current.add(0, record)
        val arr = JSONArray()
        current.forEach {
            val obj = JSONObject()
            obj.put("oath", it.oathText)
            obj.put("reason", it.reason)
            obj.put("date", it.date)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_BROKEN_RECORDS, arr.toString()).apply()
        _brokenRecords.value = current
    }

    private fun getBrokenRecords(): List<BrokenOathRecord> {
        val raw = prefs.getString(KEY_BROKEN_RECORDS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) {
                val obj = arr.getJSONObject(it)
                BrokenOathRecord(obj.getString("oath"), obj.getString("reason"), obj.getString("date"))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- FAVORITES STORE ---
    private val _favorites = MutableStateFlow(loadFavorites())
    val favorites: StateFlow<List<FavoriteItem>> = _favorites.asStateFlow()

    private fun loadFavorites(): List<FavoriteItem> {
        val raw = prefs.getString(KEY_FAVORITES, null) ?: return getDefaultFavorites()
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) {
                val obj = arr.getJSONObject(it)
                FavoriteItem(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    content = obj.getString("content"),
                    category = obj.getString("category"),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            getDefaultFavorites()
        }
    }

    fun toggleFavorite(title: String, content: String, category: String) {
        val current = _favorites.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.content == content || it.title == title }
        if (existingIndex != -1) {
            current.removeAt(existingIndex)
        } else {
            current.add(
                0,
                FavoriteItem(
                    id = "fav_${System.currentTimeMillis()}",
                    title = title,
                    content = content,
                    category = category
                )
            )
        }
        saveFavorites(current)
    }

    fun isFavorite(content: String): Boolean {
        return _favorites.value.any { it.content == content }
    }

    private fun saveFavorites(list: List<FavoriteItem>) {
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("title", it.title)
            obj.put("content", it.content)
            obj.put("category", it.category)
            obj.put("timestamp", it.timestamp)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_FAVORITES, arr.toString()).apply()
        _favorites.value = list
    }

    private fun getDefaultFavorites(): List<FavoriteItem> {
        return listOf(
            FavoriteItem(
                id = "fav_1",
                title = "Magnetic Abundance",
                content = "I am a powerful magnet for continuous wealth, clarity, and sovereign freedom.",
                category = "I AM"
            ),
            FavoriteItem(
                id = "fav_2",
                title = "Iron Discipline",
                content = "I will not break my discipline for temporary comfort.",
                category = "The Oath"
            ),
            FavoriteItem(
                id = "fav_3",
                title = "Infinite Value Creation",
                content = "My net worth expands in direct proportion to the sovereign value I create for the world.",
                category = "Manifestation"
            )
        )
    }

    // --- MANIFESTATIONS STORE ---
    private val _manifestations = MutableStateFlow(loadManifestations())
    val manifestations: StateFlow<List<ManifestationItem>> = _manifestations.asStateFlow()

    private fun loadManifestations(): List<ManifestationItem> {
        val raw = prefs.getString(KEY_MANIFESTATIONS, null) ?: return getDefaultManifestations()
        return try {
            val arr = JSONArray(raw)
            if (arr.length() == 0) return getDefaultManifestations()
            List(arr.length()) {
                val obj = arr.getJSONObject(it)
                ManifestationItem(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    date = obj.getString("date"),
                    imageUri = if (obj.has("imageUri") && !obj.isNull("imageUri")) obj.getString("imageUri") else null,
                    drawableResId = if (obj.has("drawableResId")) obj.optInt("drawableResId", 0).takeIf { res -> res != 0 } else null,
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    posX = obj.optDouble("posX", 0.0).toFloat(),
                    posY = obj.optDouble("posY", 0.0).toFloat(),
                    rotation = obj.optDouble("rotation", 0.0).toFloat(),
                    scale = obj.optDouble("scale", 1.0).toFloat(),
                    zIndex = obj.optDouble("zIndex", 0.0).toFloat()
                )
            }
        } catch (e: Exception) {
            getDefaultManifestations()
        }
    }

    fun addManifestation(
        title: String,
        imageUri: String? = null,
        drawableResId: Int? = null,
        posX: Float = 40f,
        posY: Float = 100f,
        rotation: Float = 0f
    ) {
        val today = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
        val maxZ = (_manifestations.value.maxOfOrNull { it.zIndex } ?: 0f) + 1f
        val newItem = ManifestationItem(
            id = "manifest_${System.currentTimeMillis()}",
            title = title,
            date = today,
            imageUri = imageUri,
            drawableResId = drawableResId,
            timestamp = System.currentTimeMillis(),
            posX = posX,
            posY = posY,
            rotation = rotation,
            scale = 1f,
            zIndex = maxZ
        )
        val current = _manifestations.value.toMutableList()
        current.add(0, newItem)
        saveManifestations(current)
    }

    fun deleteManifestation(id: String) {
        val current = _manifestations.value.toMutableList()
        current.removeAll { it.id == id }
        saveManifestations(current)
    }

    fun updateManifestationPosition(
        id: String,
        posX: Float,
        posY: Float,
        rotation: Float? = null,
        scale: Float? = null,
        bringToFront: Boolean = false
    ) {
        val current = _manifestations.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = current[index]
            val newZ = if (bringToFront) {
                (current.maxOfOrNull { it.zIndex } ?: 0f) + 1f
            } else {
                item.zIndex
            }
            current[index] = item.copy(
                posX = posX,
                posY = posY,
                rotation = rotation ?: item.rotation,
                scale = scale ?: item.scale,
                zIndex = newZ
            )
            saveManifestations(current)
        }
    }

    fun resetPositions() {
        val current = _manifestations.value.mapIndexed { index, item ->
            val col = index % 2
            val row = index / 2
            val defaultX = if (col == 0) 24f else 190f
            val defaultY = 40f + row * 260f
            val rot = if (index % 2 == 0) -2f else 2.5f
            item.copy(
                posX = defaultX,
                posY = defaultY,
                rotation = rot,
                scale = 1f,
                zIndex = index.toFloat()
            )
        }
        saveManifestations(current)
    }

    private fun saveManifestations(list: List<ManifestationItem>) {
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("title", it.title)
            obj.put("date", it.date)
            obj.put("imageUri", it.imageUri)
            if (it.drawableResId != null) {
                obj.put("drawableResId", it.drawableResId)
            }
            obj.put("timestamp", it.timestamp)
            obj.put("posX", it.posX.toDouble())
            obj.put("posY", it.posY.toDouble())
            obj.put("rotation", it.rotation.toDouble())
            obj.put("scale", it.scale.toDouble())
            obj.put("zIndex", it.zIndex.toDouble())
            arr.put(obj)
        }
        prefs.edit().putString(KEY_MANIFESTATIONS, arr.toString()).apply()
        _manifestations.value = list
    }

    fun updateManifestation(
        id: String,
        newTitle: String,
        newImageUri: String? = null,
        newDrawableResId: Int? = null
    ) {
        val current = _manifestations.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val existing = current[index]
            current[index] = existing.copy(
                title = newTitle,
                imageUri = newImageUri,
                drawableResId = newDrawableResId
            )
            saveManifestations(current)
        }
    }

    private fun getDefaultManifestations(): List<ManifestationItem> {
        return listOf(
            ManifestationItem(
                id = "manifest_default_car",
                title = "Dream car",
                date = "Aug 15, 2026",
                imageUri = null,
                drawableResId = com.example.R.drawable.manifest_dream_car_1786842283241,
                posX = 185f,
                posY = 40f,
                rotation = 2.5f,
                scale = 1f,
                zIndex = 1f
            ),
            ManifestationItem(
                id = "manifest_default_hello",
                title = "Live with unwavering sovereign abundance & clarity ✦",
                date = "Aug 16, 2026",
                imageUri = null,
                drawableResId = null,
                posX = 20f,
                posY = 48f,
                rotation = -2f,
                scale = 1f,
                zIndex = 2f
            ),
            ManifestationItem(
                id = "manifest_default_villa",
                title = "Dream villa",
                date = "Aug 16, 2026",
                imageUri = null,
                drawableResId = com.example.R.drawable.manifest_villa_1786842298224,
                posX = 24f,
                posY = 280f,
                rotation = -1.5f,
                scale = 1f,
                zIndex = 3f
            ),
            ManifestationItem(
                id = "manifest_default_quote",
                title = "Every desire called in with conviction is already mine.",
                date = "Aug 16, 2026",
                imageUri = null,
                drawableResId = null,
                posX = 188f,
                posY = 360f,
                rotation = 3f,
                scale = 1f,
                zIndex = 4f
            )
        )
    }

    // --- HYPNAGOGIC KNOWLEDGE & SESSIONS STORE ---
    private val _hypnagogicProgress = MutableStateFlow(prefs.getFloat(KEY_HYPNAGOGIC_PROGRESS, 0.60f))
    val hypnagogicProgress: StateFlow<Float> = _hypnagogicProgress.asStateFlow()

    fun updateHypnagogicProgress(progress: Float) {
        val capped = progress.coerceIn(0f, 1f)
        prefs.edit().putFloat(KEY_HYPNAGOGIC_PROGRESS, capped).apply()
        _hypnagogicProgress.value = capped
    }

    private val _hypnagogicInsights = MutableStateFlow(loadHypnagogicInsights())
    val hypnagogicInsights: StateFlow<List<HypnagogicInsight>> = _hypnagogicInsights.asStateFlow()

    private fun loadHypnagogicInsights(): List<HypnagogicInsight> {
        val raw = prefs.getString(KEY_HYPNAGOGIC_INSIGHTS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) {
                val obj = arr.getJSONObject(it)
                val tagsArr = obj.optJSONArray("tags")
                val tagsList = mutableListOf<String>()
                if (tagsArr != null) {
                    for (i in 0 until tagsArr.length()) {
                        tagsList.add(tagsArr.getString(i))
                    }
                }
                HypnagogicInsight(
                    id = obj.getString("id"),
                    tags = tagsList,
                    note = obj.getString("note"),
                    relaxationScore = obj.optInt("relaxationScore", 4),
                    vividnessScore = obj.optInt("vividnessScore", 3),
                    insightSummary = obj.optString("insightSummary", ""),
                    actionCreated = if (obj.has("actionCreated") && !obj.isNull("actionCreated")) obj.getString("actionCreated") else null,
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    dateStr = obj.optString("dateStr", SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()))
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveHypnagogicInsight(
        tags: List<String>,
        note: String,
        relaxationScore: Int,
        vividnessScore: Int,
        insightSummary: String,
        actionCreated: String? = null
    ) {
        val today = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault()).format(Date())
        val item = HypnagogicInsight(
            id = "insight_${System.currentTimeMillis()}",
            tags = tags,
            note = note,
            relaxationScore = relaxationScore,
            vividnessScore = vividnessScore,
            insightSummary = insightSummary,
            actionCreated = actionCreated,
            timestamp = System.currentTimeMillis(),
            dateStr = today
        )
        val current = _hypnagogicInsights.value.toMutableList()
        current.add(0, item)

        val arr = JSONArray()
        current.forEach { ins ->
            val obj = JSONObject()
            obj.put("id", ins.id)
            val tArr = JSONArray()
            ins.tags.forEach { tArr.put(it) }
            obj.put("tags", tArr)
            obj.put("note", ins.note)
            obj.put("relaxationScore", ins.relaxationScore)
            obj.put("vividnessScore", ins.vividnessScore)
            obj.put("insightSummary", ins.insightSummary)
            obj.put("actionCreated", ins.actionCreated)
            obj.put("timestamp", ins.timestamp)
            obj.put("dateStr", ins.dateStr)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_HYPNAGOGIC_INSIGHTS, arr.toString()).apply()
        _hypnagogicInsights.value = current
    }

    // --- AFFIRMATION KNOWLEDGE STATE ---
    private val _affirmationKnowledgeProgress = MutableStateFlow(prefs.getFloat(KEY_AFFIRMATION_PROGRESS, 0.85f))
    val affirmationKnowledgeProgress: StateFlow<Float> = _affirmationKnowledgeProgress.asStateFlow()

    private val _savedCurrentBelief = MutableStateFlow(prefs.getString(KEY_AFFIRMATION_CURRENT_BELIEF, "Not confident enough") ?: "Not confident enough")
    val savedCurrentBelief: StateFlow<String> = _savedCurrentBelief.asStateFlow()

    private val _savedTransformedBelief = MutableStateFlow(prefs.getString(KEY_AFFIRMATION_TRANSFORMED_BELIEF, "I am learning to trust myself and speak my truth.") ?: "I am learning to trust myself and speak my truth.")
    val savedTransformedBelief: StateFlow<String> = _savedTransformedBelief.asStateFlow()

    fun updateAffirmationProgress(progress: Float) {
        val capped = progress.coerceIn(0f, 1f)
        prefs.edit().putFloat(KEY_AFFIRMATION_PROGRESS, capped).apply()
        _affirmationKnowledgeProgress.value = capped
    }

    fun saveBeliefTransformation(current: String, transformed: String) {
        prefs.edit()
            .putString(KEY_AFFIRMATION_CURRENT_BELIEF, current)
            .putString(KEY_AFFIRMATION_TRANSFORMED_BELIEF, transformed)
            .apply()
        _savedCurrentBelief.value = current
        _savedTransformedBelief.value = transformed
    }

    // --- MANIFESTATION GUIDED FLOW & PROGRESS ---
    private val _manifestationProgress = MutableStateFlow(prefs.getFloat(KEY_MANIFESTATION_PROGRESS, 0.40f))
    val manifestationProgress: StateFlow<Float> = _manifestationProgress.asStateFlow()

    fun updateManifestationProgress(progress: Float) {
        val capped = progress.coerceIn(0f, 1f)
        prefs.edit().putFloat(KEY_MANIFESTATION_PROGRESS, capped).apply()
        _manifestationProgress.value = capped
    }

    private val _activeManifestationVision = MutableStateFlow(loadActiveManifestationVision())
    val activeManifestationVision: StateFlow<ManifestationVisionData> = _activeManifestationVision.asStateFlow()

    private fun loadActiveManifestationVision(): ManifestationVisionData {
        val raw = prefs.getString(KEY_ACTIVE_MANIFESTATION_VISION, null) ?: return ManifestationVisionData(
            dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
        )
        return try {
            val obj = JSONObject(raw)
            ManifestationVisionData(
                id = obj.optString("id", "vision_primary"),
                intention = obj.optString("intention", "Build a successful digital business"),
                category = obj.optString("category", "Achievement"),
                whatText = obj.optString("whatText", "Build a profitable online business"),
                whyText = obj.optString("whyText", "Freedom and independence"),
                whenText = obj.optString("whenText", "Within 2 years"),
                howKnowText = obj.optString("howKnowText", "First 100 paying customers"),
                whereAreYou = obj.optString("whereAreYou", "Sunlit modern home studio overlooking greenery"),
                whatDoing = obj.optString("whatDoing", "Designing impactful software solutions calmly"),
                whoWith = obj.optString("whoWith", "Supportive dream team and loved ones"),
                whatChanged = obj.optString("whatChanged", "Complete financial sovereignty and zero anxiety"),
                emotion = obj.optString("emotion", "Free"),
                emotionEmoji = obj.optString("emotionEmoji", "🕊️"),
                emotionWhy = obj.optString("emotionWhy", "I want freedom because I want control over how I spend my time."),
                thinkTrait = obj.optString("thinkTrait", "They think in abundant possibilities and long-term compounding."),
                believeTrait = obj.optString("believeTrait", "They believe they are fully worthy of extraordinary wealth."),
                doTrait = obj.optString("doTrait", "They consistently take high-leverage action and finish what they start."),
                dontTrait = obj.optString("dontTrait", "They do not procrastinate or seek outside validation."),
                todayAction = obj.optString("todayAction", "Finish my high-converting landing page and ship to early users."),
                timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                dateStr = obj.optString("dateStr", SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()))
            )
        } catch (e: Exception) {
            ManifestationVisionData(
                dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
            )
        }
    }

    fun saveActiveManifestationVision(vision: ManifestationVisionData) {
        val today = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
        val updated = vision.copy(dateStr = today, timestamp = System.currentTimeMillis())
        val obj = JSONObject()
        obj.put("id", updated.id)
        obj.put("intention", updated.intention)
        obj.put("category", updated.category)
        obj.put("whatText", updated.whatText)
        obj.put("whyText", updated.whyText)
        obj.put("whenText", updated.whenText)
        obj.put("howKnowText", updated.howKnowText)
        obj.put("whereAreYou", updated.whereAreYou)
        obj.put("whatDoing", updated.whatDoing)
        obj.put("whoWith", updated.whoWith)
        obj.put("whatChanged", updated.whatChanged)
        obj.put("emotion", updated.emotion)
        obj.put("emotionEmoji", updated.emotionEmoji)
        obj.put("emotionWhy", updated.emotionWhy)
        obj.put("thinkTrait", updated.thinkTrait)
        obj.put("believeTrait", updated.believeTrait)
        obj.put("doTrait", updated.doTrait)
        obj.put("dontTrait", updated.dontTrait)
        obj.put("todayAction", updated.todayAction)
        obj.put("timestamp", updated.timestamp)
        obj.put("dateStr", updated.dateStr)

        prefs.edit().putString(KEY_ACTIVE_MANIFESTATION_VISION, obj.toString()).apply()
        _activeManifestationVision.value = updated
    }

    // --- GRATITUDE JOURNAL STATE ---
    private val _gratitudeEntries = MutableStateFlow(loadGratitudeEntries())
    val gratitudeEntries: StateFlow<List<GratitudeEntry>> = _gratitudeEntries.asStateFlow()

    private val _gratitudeStreak = MutableStateFlow(prefs.getInt(KEY_GRATITUDE_STREAK, 3))
    val gratitudeStreak: StateFlow<Int> = _gratitudeStreak.asStateFlow()

    fun saveGratitudeEntry(entry: GratitudeEntry) {
        val current = _gratitudeEntries.value.toMutableList()
        val today = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
        val formatted = if (entry.dateStr.isBlank()) entry.copy(dateStr = today) else entry
        
        val index = current.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            current[index] = formatted
        } else {
            current.add(0, formatted)
            val newStreak = _gratitudeStreak.value + 1
            prefs.edit().putInt(KEY_GRATITUDE_STREAK, newStreak).apply()
            _gratitudeStreak.value = newStreak
        }
        saveGratitudeList(current)
    }

    fun deleteGratitudeEntry(id: String) {
        val current = _gratitudeEntries.value.toMutableList()
        current.removeAll { it.id == id }
        saveGratitudeList(current)
    }

    private fun loadGratitudeEntries(): List<GratitudeEntry> {
        val raw = prefs.getString(KEY_GRATITUDE_ENTRIES, null) ?: return getDefaultGratitudeEntries()
        return try {
            val arr = JSONArray(raw)
            if (arr.length() == 0) return getDefaultGratitudeEntries()
            List(arr.length()) {
                val obj = arr.getJSONObject(it)
                GratitudeEntry(
                    id = obj.optString("id", "g_$it"),
                    item1 = obj.optString("item1", ""),
                    item2 = obj.optString("item2", ""),
                    item3 = obj.optString("item3", ""),
                    category = obj.optString("category", "Daily Abundance"),
                    moodEmoji = obj.optString("moodEmoji", "🙏"),
                    moodLabel = obj.optString("moodLabel", "Grateful"),
                    reflection = obj.optString("reflection", ""),
                    dateStr = obj.optString("dateStr", "Today"),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            getDefaultGratitudeEntries()
        }
    }

    private fun saveGratitudeList(list: List<GratitudeEntry>) {
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("item1", it.item1)
            obj.put("item2", it.item2)
            obj.put("item3", it.item3)
            obj.put("category", it.category)
            obj.put("moodEmoji", it.moodEmoji)
            obj.put("moodLabel", it.moodLabel)
            obj.put("reflection", it.reflection)
            obj.put("dateStr", it.dateStr)
            obj.put("timestamp", it.timestamp)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_GRATITUDE_ENTRIES, arr.toString()).apply()
        _gratitudeEntries.value = list
    }

    private fun getDefaultGratitudeEntries(): List<GratitudeEntry> {
        return listOf(
            GratitudeEntry(
                id = "default_g_1",
                item1 = "Crystal clear mental focus and quiet morning hours to build.",
                item2 = "Unexpected financial flow from an aligned client collaboration.",
                item3 = "Loving support and unconditional belief from family and friends.",
                category = "Wealth & Money",
                moodEmoji = "✨",
                moodLabel = "Abundant",
                reflection = "When I notice abundance already present, anxiety dissolves and creation feels effortless.",
                dateStr = "Today",
                timestamp = System.currentTimeMillis()
            ),
            GratitudeEntry(
                id = "default_g_2",
                item1 = "Vibrant physical health and boundless energy throughout the workday.",
                item2 = "The strength gained through solving difficult challenges calmly.",
                item3 = "Access to unlimited knowledge and high-leverage digital tools.",
                category = "Mindset & Growth",
                moodEmoji = "🌿",
                moodLabel = "Peaceful",
                reflection = "Every obstacle was a teacher preparing me for higher wealth consciousness.",
                dateStr = "Yesterday",
                timestamp = System.currentTimeMillis() - 86400000L
            )
        )
    }

    // --- ACTIONS STORE ---
    private val _wealthActions = MutableStateFlow(loadWealthActions())
    val wealthActions: StateFlow<List<WealthActionItem>> = _wealthActions.asStateFlow()

    private fun loadWealthActions(): List<WealthActionItem> {
        val raw = prefs.getString(KEY_WEALTH_ACTIONS, null) ?: return getDefaultWealthActions()
        return try {
            val arr = JSONArray(raw)
            if (arr.length() == 0) return getDefaultWealthActions()
            List(arr.length()) {
                val obj = arr.getJSONObject(it)
                WealthActionItem(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    subtitle = obj.getString("subtitle"),
                    duration = obj.optString("duration", "10 min"),
                    isCompleted = obj.optBoolean("isCompleted", false)
                )
            }
        } catch (e: Exception) {
            getDefaultWealthActions()
        }
    }

    fun toggleWealthAction(id: String) {
        val current = _wealthActions.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = current[index]
            current[index] = item.copy(isCompleted = !item.isCompleted)
            saveWealthActions(current)
        }
    }

    fun addWealthAction(title: String, subtitle: String = "Hypnagogic Session Action", duration: String = "15 min") {
        val current = _wealthActions.value.toMutableList()
        val newItem = WealthActionItem(
            id = "act_${System.currentTimeMillis()}",
            title = title,
            subtitle = subtitle,
            duration = duration,
            isCompleted = false
        )
        current.add(0, newItem)
        saveWealthActions(current)
    }

    private fun saveWealthActions(list: List<WealthActionItem>) {
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("title", it.title)
            obj.put("subtitle", it.subtitle)
            obj.put("duration", it.duration)
            obj.put("isCompleted", it.isCompleted)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_WEALTH_ACTIONS, arr.toString()).apply()
        _wealthActions.value = list
    }

    private fun getDefaultWealthActions(): List<WealthActionItem> {
        return listOf(
            WealthActionItem("action_hypnagogic", "Hypnagogic Nap Practice", "10 min quiet subconscious observation & capture", "10 min", false),
            WealthActionItem("action_affirmations", "Affirmation Chanting Routine", "Rewire neural pathways with sacred I AM scripts", "5 min", false),
            WealthActionItem("action_gratitude", "Daily Gratitude Alignment", "Anchor 3 forms of abundance received today", "5 min", false),
            WealthActionItem("action_manifestations", "Manifestation Visualization", "Immerse in the sensory feeling of the wish fulfilled", "10 min", false)
        )
    }

    companion object {
        private const val KEY_FUTURE_SELF_TEXT = "key_future_self_text"
        private const val KEY_OATH_SEALED = "key_oath_sealed"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_PERSONAL_OATHS = "key_personal_oaths"
        private const val KEY_SEALED_DATE = "key_sealed_date"
        private const val KEY_SIGNATURE_SVG = "key_signature_svg"
        private const val KEY_BROKEN_RECORDS = "key_broken_records"
        private const val KEY_FAVORITES = "key_favorites"
        private const val KEY_MANIFESTATIONS = "key_manifestations"
        private const val KEY_MANIFESTATION_PROGRESS = "key_manifestation_progress"
        private const val KEY_ACTIVE_MANIFESTATION_VISION = "key_active_manifestation_vision"
        private const val KEY_AFFIRMATION_PROGRESS = "key_affirmation_progress"
        private const val KEY_AFFIRMATION_CURRENT_BELIEF = "key_affirmation_current_belief"
        private const val KEY_AFFIRMATION_TRANSFORMED_BELIEF = "key_affirmation_transformed_belief"
        private const val KEY_GRATITUDE_ENTRIES = "key_gratitude_entries"
        private const val KEY_GRATITUDE_STREAK = "key_gratitude_streak"
        private const val KEY_HYPNAGOGIC_PROGRESS = "key_hypnagogic_progress"
        private const val KEY_HYPNAGOGIC_INSIGHTS = "key_hypnagogic_insights"
        private const val KEY_WEALTH_ACTIONS = "key_wealth_actions"

        const val DEFAULT_FUTURE_SELF =
            "I am financially free and live with unwavering purpose. I run a successful enterprise that creates immense value for the world. I wake up with crystalline clarity, high energy, and effortless discipline. I handle complex challenges calmly instead of avoiding them. I make decisions from abundance, never scarcity. I inspire those around me by embodying sovereignty, wealth consciousness, and grounded peace."
    }
}
