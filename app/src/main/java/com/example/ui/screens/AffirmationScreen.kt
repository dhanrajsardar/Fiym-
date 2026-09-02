package com.example.ui.screens

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WealthIdentityStore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Data class representing a daily affirmation card.
 */
data class AffirmationItem(
    val id: String,
    val text: String,
    val category: String
)

/**
 * Ambient Calming Tone Player using low-latency AudioTrack sound synthesis
 * (Generates warm, gentle 432Hz ambient frequency for deep meditation & calm focus).
 */
class AmbientSoundEngine {
    private var audioTrack: AudioTrack? = null
    @Volatile
    private var isPlaying = false

    fun start() {
        if (isPlaying) return
        isPlaying = true

        Thread {
            try {
                val sampleRate = 44100
                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()

                val buffer = ShortArray(bufferSize)
                var angle1 = 0.0
                var angle2 = 0.0
                var angle3 = 0.0
                val freq1 = 432.0 // Primary calm frequency
                val freq2 = 216.0 // Warm sub-octave
                val freq3 = 528.0 // Solfeggio miracle tone overtone

                val rad1 = 2.0 * PI * freq1 / sampleRate
                val rad2 = 2.0 * PI * freq2 / sampleRate
                val rad3 = 2.0 * PI * freq3 / sampleRate

                while (isPlaying) {
                    for (i in buffer.indices) {
                        val sample = (
                                sin(angle1) * 0.22 +
                                        sin(angle2) * 0.12 +
                                        sin(angle3) * 0.06
                                )
                        buffer[i] = (sample * 32767).toInt().coerceIn(-32768, 32767).toShort()

                        angle1 = (angle1 + rad1) % (2.0 * PI)
                        angle2 = (angle2 + rad2) % (2.0 * PI)
                        angle3 = (angle3 + rad3) % (2.0 * PI)
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                // Audio synthesis gracefully ignored
            }
        }.start()
    }

    fun stop() {
        isPlaying = false
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignored
        }
        audioTrack = null
    }
}

/**
 * Returns today's curated collection of 7-8 daily affirmations.
 * Refreshes deterministically each day based on the calendar day of the year.
 */
fun getDailyAffirmations(): List<AffirmationItem> {
    val masterPool = listOf(
        AffirmationItem(
            id = "aff_1",
            text = "My confidence grows with every step I take towards my dreams, knowing I am capable and worthy",
            category = "GROWING CONFIDENCE STEPS"
        ),
        AffirmationItem(
            id = "aff_2",
            text = "I confidently pursue my fitness goals, knowing my body and mind are strong and capable",
            category = "CONFIDENT FITNESS PURSUIT"
        ),
        AffirmationItem(
            id = "aff_3",
            text = "I trust my intuition and make wise decisions, growing stronger in self-assurance",
            category = "TRUST INTUITION"
        ),
        AffirmationItem(
            id = "aff_4",
            text = "I radiate confidence and attract opportunities that align with my ambitious goals and strong work ethic",
            category = "RADIATING CONFIDENT OPPORTUNITY"
        ),
        AffirmationItem(
            id = "aff_5",
            text = "I embrace my single status with confidence, focusing on personal growth and future happiness",
            category = "EMBRACING SINGLE CONFIDENCE"
        ),
        AffirmationItem(
            id = "aff_6",
            text = "Abundance flows naturally into my life as I create immense value and remain open to limitless prosperity",
            category = "MAGNETIC WEALTH ALIGNMENT"
        ),
        AffirmationItem(
            id = "aff_7",
            text = "I release all fear and anxiety, grounding my mind in crystalline clarity and unbreakable peace",
            category = "UNSHAKABLE INNER CALM"
        ),
        AffirmationItem(
            id = "aff_8",
            text = "Every day, I am becoming the highest, most sovereign version of myself with boundless energy and joy",
            category = "SOVEREIGN IDENTITY EMPOWERMENT"
        ),
        AffirmationItem(
            id = "aff_9",
            text = "Money is an empowering tool that magnifies my positive impact and expands my personal freedom",
            category = "EXPANDING WEALTH FREEDOM"
        ),
        AffirmationItem(
            id = "aff_10",
            text = "I love and honor who I am today, celebrating every micro-victory on my journey to mastery",
            category = "SELF-LOVE & MASTERY"
        ),
        AffirmationItem(
            id = "aff_11",
            text = "I attract harmonious relationships that support my highest growth, authenticity, and peace",
            category = "ELEVATED CONNECTIONS"
        ),
        AffirmationItem(
            id = "aff_12",
            text = "I am worthy of profound success, limitless wealth, and deep fulfillment in all areas of my life",
            category = "INFINITE WORTH & ABUNDANCE"
        )
    )

    val calendar = Calendar.getInstance()
    val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val startIndex = (dayOfYear * 7) % masterPool.size

    val result = mutableListOf<AffirmationItem>()
    for (i in 0 until 8) {
        val index = (startIndex + i) % masterPool.size
        result.add(masterPool[index])
    }
    return result
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AffirmationScreen(
    identityStore: WealthIdentityStore? = null,
    onBack: () -> Unit,
    onNavigateFavorites: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Daily affirmations list (7-8 curated items for today)
    val dailyList = remember { getDailyAffirmations() }
    var currentIndex by remember { mutableIntStateOf(0) }

    // Audio & TTS Engine
    var isAmbientMusicPlaying by remember { mutableStateOf(false) }
    var isSpeakingAffirmation by remember { mutableStateOf(false) }
    val soundEngine = remember { AmbientSoundEngine() }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                tts?.setSpeechRate(0.88f) // Soft, calm cadence
                tts?.setPitch(1.02f)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            soundEngine.stop()
            try {
                tts?.stop()
                tts?.shutdown()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    // Toggle ambient music
    fun toggleAmbientMusic() {
        if (isAmbientMusicPlaying) {
            soundEngine.stop()
            isAmbientMusicPlaying = false
        } else {
            soundEngine.start()
            isAmbientMusicPlaying = true
        }
    }

    // Speak current affirmation
    fun speakCurrentAffirmation(text: String) {
        if (isSpeakingAffirmation) {
            tts?.stop()
            isSpeakingAffirmation = false
        } else {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AFFIRMATION_TTS")
            isSpeakingAffirmation = true
        }
    }

    // Current item
    val activeAffirmation = dailyList[currentIndex % dailyList.size]

    // Formatted header date: e.g. "SATURDAY AUG 15"
    val dateString = remember {
        val formatter = SimpleDateFormat("EEEE MMM d", Locale.ENGLISH)
        formatter.format(Date()).uppercase()
    }

    // Modal Sheet for Abundance Gems / Category Selector
    var showGemsSheet by remember { mutableStateOf(false) }

    // Multi-directional Gesture Drag State (X & Y translation)
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    // Drag helper for swipe in any direction (left, right, up, down)
    fun triggerSwipe(directionX: Float, directionY: Float) {
        coroutineScope.launch {
            if (abs(directionX) > abs(directionY)) {
                if (directionX < 0) {
                    // Swipe Left -> Next
                    offsetX.animateTo(-900f, tween(240, easing = FastOutSlowInEasing))
                    currentIndex = (currentIndex + 1) % dailyList.size
                    offsetX.snapTo(900f)
                    offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
                } else {
                    // Swipe Right -> Prev
                    offsetX.animateTo(900f, tween(240, easing = FastOutSlowInEasing))
                    currentIndex = (currentIndex - 1 + dailyList.size) % dailyList.size
                    offsetX.snapTo(-900f)
                    offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
                }
            } else {
                if (directionY < 0) {
                    // Swipe Up -> Next
                    offsetY.animateTo(-900f, tween(240, easing = FastOutSlowInEasing))
                    currentIndex = (currentIndex + 1) % dailyList.size
                    offsetY.snapTo(900f)
                    offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
                } else {
                    // Swipe Down -> Prev
                    offsetY.animateTo(900f, tween(240, easing = FastOutSlowInEasing))
                    currentIndex = (currentIndex - 1 + dailyList.size) % dailyList.size
                    offsetY.snapTo(-900f)
                    offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
                }
            }
        }
    }

    BackHandler {
        onBack()
    }

    // Exact atmospheric blurred pastel background gradient from reference images:
    // Pale lilac/lavender -> Soft powdery rose pink -> Warm peach apricot sunset glow
    val backgroundBrush = Brush.verticalGradient(
        0.00f to Color(0xFFEBE4F0), // Top: Pale lavender mist
        0.20f to Color(0xFFF3E6EE), // Upper: Gentle lilac petal
        0.45f to Color(0xFFF8E5EC), // Mid: Soft powdery pink glow
        0.70f to Color(0xFFFDE6DC), // Lower-mid: Delicate blush peach
        0.88f to Color(0xFFFCDCC6), // Lower: Warm apricot glow
        1.00f to Color(0xFFFCD4BC)  // Bottom: Warm peach-gold sunset
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ----------------------------------------------------
            // 1. TOP HEADER: "Hi Wealthy" + Date & Action Buttons
            // ----------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title and Date
                Column {
                    Text(
                        text = "Hi Wealthy",
                        fontFamily = FontFamily.Serif,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF2C2430),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateString,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        color = Color(0xFF8E7D93)
                    )
                }

                // Top Right Action Buttons: Gem Button + Music Play/Pause Button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Diamond/Gem Button (Lavender gradient with white diamond icon)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x33B18BB3))
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFC792C7),
                                        Color(0xFFB17DAE),
                                        Color(0xFFA570A2)
                                    )
                                )
                            )
                            .clickable { showGemsSheet = true }
                            .testTag("gem_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Diamond,
                            contentDescription = "Abundance Gems",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Ambient Background Music Button (White pill with Pause/Play)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(4.dp, CircleShape, spotColor = Color(0x22000000))
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { toggleAmbientMusic() }
                            .testTag("music_toggle_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAmbientMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isAmbientMusicPlaying) "Pause Calming Music" else "Play Calming Music",
                            tint = Color(0xFF3B2E3D),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ----------------------------------------------------
            // 2. LAYERED CARD STACK WITH MULTI-DIRECTIONAL SWIPE
            // ----------------------------------------------------
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Layer Card (Peeking underneath and to the side, matching reference images!)
                val bgRotation = 2.5f
                val bgOffsetX = 18.dp
                val bgOffsetY = 12.dp

                // Underlying Stacked Card 2 (Bottom depth glow)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .offset(y = 20.dp)
                        .height(440.dp)
                        .scale(0.94f)
                        .alpha(0.6f)
                        .shadow(16.dp, RoundedCornerShape(32.dp), spotColor = Color(0x30B695A6))
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFFFCF3EC))
                )

                // Underlying Stacked Card 1 (Side peeking card from references 2, 3)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .offset(x = bgOffsetX, y = bgOffsetY)
                        .height(450.dp)
                        .rotate(bgRotation)
                        .scale(0.97f)
                        .alpha(0.85f)
                        .shadow(20.dp, RoundedCornerShape(32.dp), spotColor = Color(0x3AB695A6))
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFFFFF6EE))
                ) {
                    // Ambient Mint/Rose Glowing Border
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(32.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.5.dp, Color(0xFFD6EFE0).copy(alpha = 0.7f))
                    ) {}
                }

                // Active Top Affirmation Card
                val dragX = offsetX.value
                val dragY = offsetY.value
                val rotationAngle = (dragX / 25f).coerceIn(-18f, 18f) + (dragY / 45f)
                val dragDistance = sqrt(dragX * dragX + dragY * dragY)
                val activeScale = (1f - (dragDistance / 2400f)).coerceIn(0.93f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(dragX.roundToInt(), dragY.roundToInt()) }
                        .rotate(rotationAngle)
                        .scale(activeScale)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    val threshold = 180f
                                    if (abs(offsetX.value) > threshold || abs(offsetY.value) > threshold) {
                                        triggerSwipe(offsetX.value, offsetY.value)
                                    } else {
                                        coroutineScope.launch {
                                            offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
                                        }
                                        coroutineScope.launch {
                                            offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
                                        }
                                    }
                                },
                                onDragCancel = {
                                    coroutineScope.launch {
                                        offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
                                        offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    coroutineScope.launch {
                                        offsetX.snapTo(offsetX.value + dragAmount.x)
                                        offsetY.snapTo(offsetY.value + dragAmount.y)
                                    }
                                }
                            )
                        }
                ) {
                    val isFav = identityStore?.let { store ->
                        val favs by store.favorites.collectAsState()
                        favs.any { it.content == activeAffirmation.text }
                    } ?: false

                    AffirmationCardView(
                        affirmation = activeAffirmation,
                        isFavorite = isFav,
                        isPlayingSpeech = isSpeakingAffirmation,
                        onToggleFavorite = {
                            identityStore?.toggleFavorite(
                                title = activeAffirmation.category,
                                content = activeAffirmation.text,
                                category = "Affirmation"
                            )
                        },
                        onPlayAudio = {
                            speakCurrentAffirmation(activeAffirmation.text)
                        },
                        onShare = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "\"${activeAffirmation.text}\"\n— Daily Affirmation (Fiym · Manifest Your Dreams)")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Affirmation"))
                        }
                    )
                }
            }

            // ----------------------------------------------------
            // 3. "A F F I R M A T I O N S" SECTION LABEL
            // ----------------------------------------------------
            Text(
                text = "A F F I R M A T I O N S",
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 5.sp,
                color = Color(0xFF6B5872),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // ----------------------------------------------------
            // 4. FLOATING BOTTOM NAVIGATION BAR (Matches References)
            // ----------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Button: Heart / Saved Favorites (White circle with heart)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape, spotColor = Color(0x20523C59))
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onNavigateFavorites() }
                        .testTag("nav_favorites_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Saved Favorites",
                        tint = Color(0xFF755B7B),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center Button: Active Glowing Rose Pill with Sparkle Stars (✦)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = Color(0x66DF7E96))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFD66F8A),
                                    Color(0xFFBE5974)
                                )
                            )
                        )
                        .clickable {
                            // Cycle next affirmation
                            currentIndex = (currentIndex + 1) % dailyList.size
                        }
                        .testTag("nav_affirmations_active_button"),
                    contentAlignment = Alignment.Center
                ) {
                    // Sparkling stars icon
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✦",
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }
                }

                // Right Button: User Profile (White circle with person icon)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape, spotColor = Color(0x20523C59))
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onNavigateProfile() }
                        .testTag("nav_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFF755B7B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // ----------------------------------------------------
    // GEMS BOTTOM SHEET MODAL (Category Explorer)
    // ----------------------------------------------------
    if (showGemsSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showGemsSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFFFAF3F7),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Abundance Gems",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF352B3B)
                    )
                    Text(
                        text = "${dailyList.size} Today",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF9E8B98)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                dailyList.forEachIndexed { idx, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (idx == currentIndex % dailyList.size) Color(0xFFF1DEEC) else Color.White)
                            .clickable {
                                currentIndex = idx
                                showGemsSheet = false
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = Color(0xFF8B6B8A)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = item.text,
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp,
                                color = Color(0xFF2C2430),
                                maxLines = 2
                            )
                        }
                        if (idx == currentIndex % dailyList.size) {
                            Text(text = "✦", fontSize = 16.sp, color = Color(0xFFC76282), modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * The individual Affirmation Card matching reference images exactly:
 * - Soft warm cream background
 * - Glowing subtle outer border (ambient mint/pink glow)
 * - Pink sparkle decoration at top center
 * - Large thin elegant serif typography (Italic)
 * - Uppercase category title below
 * - Bottom row: Favorite button (left), Play button (center), Share button (right)
 */
@Composable
fun AffirmationCardView(
    affirmation: AffirmationItem,
    isFavorite: Boolean,
    isPlayingSpeech: Boolean,
    onToggleFavorite: () -> Unit,
    onPlayAudio: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(470.dp)
            .shadow(28.dp, RoundedCornerShape(32.dp), spotColor = Color(0x3DB892A8)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6EE)),
        border = BorderStroke(1.5.dp, Color(0xFFD6EFE0).copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top sparkle decoration (✦ ✧ ✦ in delicate rose)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "✦  ✧  ✦",
                    fontSize = 15.sp,
                    color = Color(0xFFE5A8BA),
                    letterSpacing = 2.sp
                )
            }

            // Main Affirmation Text (Large, thin, elegant serif in deep plum)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = affirmation.text,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 24.sp,
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF352B3B)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Uppercase Category Title with wide letter spacing
                Text(
                    text = affirmation.category,
                    fontFamily = FontFamily.Serif,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp,
                    color = Color(0xFF9E8B98),
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Actions Row (Favorite, Play/Audio, Share)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Favorite Button (White circle with heart)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color(0x1A000000))
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onToggleFavorite() }
                        .testTag("card_favorite_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFE53935) else Color(0xFF9E8B98),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 2. Center Play / Speech Button (White circle with Play/Pause)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color(0x1A000000))
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onPlayAudio() }
                        .testTag("card_play_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlayingSpeech) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Listen to Affirmation",
                        tint = Color(0xFF755B7B),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 3. Share Button (White circle with Share icon)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color(0x1A000000))
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onShare() }
                        .testTag("card_share_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Affirmation",
                        tint = Color(0xFF9E8B98),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
