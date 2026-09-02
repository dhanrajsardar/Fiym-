package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ManifestationVisionData
import com.example.data.local.WealthIdentityStore

enum class ManifestationViewMode {
    COURSE_HUB,
    GUIDED_JOURNEY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManifestationJourneyScreen(
    identityStore: WealthIdentityStore,
    initialMode: ManifestationViewMode = ManifestationViewMode.COURSE_HUB,
    initialStep: Int = 1,
    onNavigateVisionBoard: () -> Unit = {},
    onNavigateActionTab: () -> Unit = {},
    onExit: () -> Unit = {}
) {
    val context = LocalContext.current
    var currentMode by remember { mutableStateOf(initialMode) }
    var currentStep by remember { mutableIntStateOf(initialStep.coerceIn(1, 10)) }

    val savedVision by identityStore.activeManifestationVision.collectAsState()
    val progressVal by identityStore.manifestationProgress.collectAsState()

    // Working state for the 10-screen journey
    var intention by remember(savedVision) { mutableStateOf(savedVision.intention) }
    var category by remember(savedVision) { mutableStateOf(savedVision.category) }
    var whatText by remember(savedVision) { mutableStateOf(savedVision.whatText) }
    var whyText by remember(savedVision) { mutableStateOf(savedVision.whyText) }
    var whenText by remember(savedVision) { mutableStateOf(savedVision.whenText) }
    var howKnowText by remember(savedVision) { mutableStateOf(savedVision.howKnowText) }
    var whereAreYou by remember(savedVision) { mutableStateOf(savedVision.whereAreYou) }
    var whatDoing by remember(savedVision) { mutableStateOf(savedVision.whatDoing) }
    var whoWith by remember(savedVision) { mutableStateOf(savedVision.whoWith) }
    var whatChanged by remember(savedVision) { mutableStateOf(savedVision.whatChanged) }
    var selectedEmotion by remember(savedVision) { mutableStateOf(savedVision.emotion) }
    var emotionEmoji by remember(savedVision) { mutableStateOf(savedVision.emotionEmoji) }
    var emotionWhy by remember(savedVision) { mutableStateOf(savedVision.emotionWhy) }
    var thinkTrait by remember(savedVision) { mutableStateOf(savedVision.thinkTrait) }
    var believeTrait by remember(savedVision) { mutableStateOf(savedVision.believeTrait) }
    var doTrait by remember(savedVision) { mutableStateOf(savedVision.doTrait) }
    var dontTrait by remember(savedVision) { mutableStateOf(savedVision.dontTrait) }
    var todayAction by remember(savedVision) { mutableStateOf(savedVision.todayAction) }

    fun commitCurrentVision() {
        val updated = ManifestationVisionData(
            id = "vision_primary",
            intention = intention.ifBlank { "Build a successful digital business" },
            category = category,
            whatText = whatText.ifBlank { "Build a profitable online business" },
            whyText = whyText.ifBlank { "Freedom and independence" },
            whenText = whenText.ifBlank { "Within 2 years" },
            howKnowText = howKnowText.ifBlank { "First 100 paying customers" },
            whereAreYou = whereAreYou.ifBlank { "Sunlit modern home studio overlooking greenery" },
            whatDoing = whatDoing.ifBlank { "Designing impactful software solutions calmly" },
            whoWith = whoWith.ifBlank { "Supportive dream team and loved ones" },
            whatChanged = whatChanged.ifBlank { "Complete financial sovereignty and zero anxiety" },
            emotion = selectedEmotion,
            emotionEmoji = emotionEmoji,
            emotionWhy = emotionWhy.ifBlank { "I want freedom because I want control over how I spend my time." },
            thinkTrait = thinkTrait.ifBlank { "They think in abundant possibilities." },
            believeTrait = believeTrait.ifBlank { "They believe they are worthy of immense scale." },
            doTrait = doTrait.ifBlank { "They consistently finish what they start." },
            dontTrait = dontTrait.ifBlank { "They do not procrastinate or seek outside validation." },
            todayAction = todayAction.ifBlank { "Spend 30 minutes finishing the primary milestone." }
        )
        identityStore.saveActiveManifestationVision(updated)
        identityStore.updateManifestationProgress((currentStep / 10f).coerceIn(0.1f, 1f))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (currentMode == ManifestationViewMode.COURSE_HUB) "Manifestation Course" else "Manifestation Journey",
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1926)
                        )
                        Text(
                            text = if (currentMode == ManifestationViewMode.COURSE_HUB) "5 Lessons · Intention to Action" else "Step $currentStep of 10",
                            fontSize = 12.sp,
                            color = Color(0xFF756F84)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentMode == ManifestationViewMode.GUIDED_JOURNEY && currentStep > 1) {
                                currentStep--
                            } else if (currentMode == ManifestationViewMode.GUIDED_JOURNEY) {
                                currentMode = ManifestationViewMode.COURSE_HUB
                            } else {
                                onExit()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E1926)
                        )
                    }
                },
                actions = {
                    if (currentMode == ManifestationViewMode.GUIDED_JOURNEY) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF3E0),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable {
                                    commitCurrentVision()
                                    currentMode = ManifestationViewMode.COURSE_HUB
                                }
                        ) {
                            Text(
                                text = "Course Hub",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF8FE))
            )
        },
        containerColor = Color(0xFFFBF8FE)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentMode to currentStep,
                transitionSpec = {
                    if (targetState.second > initialState.second) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "ManifestationContentAnimation"
            ) { state ->
                val mode = state.first
                val step = state.second

                if (mode == ManifestationViewMode.COURSE_HUB) {
                    ManifestationCourseHub(
                        progress = progressVal,
                        savedVision = savedVision,
                        onStartGuidedJourney = { startStep ->
                            currentStep = startStep
                            currentMode = ManifestationViewMode.GUIDED_JOURNEY
                        },
                        onNavigateVisionBoard = onNavigateVisionBoard,
                        onNavigateActionTab = onNavigateActionTab
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { step / 10f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFFF9800),
                            trackColor = Color(0xFFFFE0B2)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        when (step) {
                            1 -> ManifestationScreen1Hook(
                                onContinue = {
                                    commitCurrentVision()
                                    currentStep = 2
                                }
                            )
                            2 -> ManifestationScreen2WhatIs(
                                onContinue = {
                                    commitCurrentVision()
                                    currentStep = 3
                                }
                            )
                            3 -> ManifestationScreen3WhatIsNot(
                                onContinue = {
                                    commitCurrentVision()
                                    currentStep = 4
                                }
                            )
                            4 -> ManifestationScreen4Clarity(
                                intention = intention,
                                category = category,
                                onIntentionChange = { intention = it },
                                onCategorySelect = { cat, sample ->
                                    category = cat
                                    intention = sample
                                },
                                onContinue = {
                                    commitCurrentVision()
                                    currentStep = 5
                                }
                            )
                            5 -> ManifestationScreen5Specific(
                                whatText = whatText,
                                whyText = whyText,
                                whenText = whenText,
                                howKnowText = howKnowText,
                                onWhatChange = { whatText = it },
                                onWhyChange = { whyText = it },
                                onWhenChange = { whenText = it },
                                onHowKnowChange = { howKnowText = it },
                                onContinue = {
                                    commitCurrentVision()
                                    currentStep = 6
                                }
                            )
                            6 -> ManifestationScreen6SeeIt(
                                whereAreYou = whereAreYou,
                                whatDoing = whatDoing,
                                whoWith = whoWith,
                                whatChanged = whatChanged,
                                onWhereChange = { whereAreYou = it },
                                onWhatDoingChange = { whatDoing = it },
                                onWhoWithChange = { whoWith = it },
                                onWhatChangedChange = { whatChanged = it },
                                onContinue = {
                                    commitCurrentVision()
                                    currentStep = 7
                                }
                            )
                            7 -> ManifestationScreen7FeelIt(
                                selectedEmotion = selectedEmotion,
                                emotionWhy = emotionWhy,
                                onEmotionSelect = { name, emoji ->
                                    selectedEmotion = name
                                    emotionEmoji = emoji
                                },
                                onEmotionWhyChange = { emotionWhy = it },
                                onContinue = {
                                    commitCurrentVision()
                                    currentStep = 8
                                }
                            )
                            8 -> ManifestationScreen8BecomeThePerson(
                                thinkTrait = thinkTrait,
                                believeTrait = believeTrait,
                                doTrait = doTrait,
                                dontTrait = dontTrait,
                                onThinkChange = { thinkTrait = it },
                                onBelieveChange = { believeTrait = it },
                                onDoChange = { doTrait = it },
                                onDontChange = { dontTrait = it },
                                onContinue = {
                                    commitCurrentVision()
                                    currentStep = 9
                                }
                            )
                            9 -> ManifestationScreen9AlignReality(
                                intention = intention,
                                doTrait = doTrait,
                                todayAction = todayAction,
                                onTodayActionChange = { todayAction = it },
                                onAddActionAndContinue = {
                                    commitCurrentVision()
                                    val actionName = todayAction.ifBlank { "Spend 30 minutes advancing my primary vision milestone" }
                                    identityStore.addWealthAction(
                                        title = actionName,
                                        subtitle = "Aligned Manifestation Action",
                                        duration = "30 min"
                                    )
                                    Toast.makeText(context, "Action added to daily Action tab!", Toast.LENGTH_SHORT).show()
                                    currentStep = 10
                                }
                            )
                            10 -> ManifestationScreen10ReturnVision(
                                intention = intention,
                                emotion = selectedEmotion,
                                emotionWhy = emotionWhy,
                                doTrait = doTrait,
                                todayAction = todayAction,
                                onSaveAndComplete = {
                                    commitCurrentVision()
                                    identityStore.updateManifestationProgress(1f)
                                    Toast.makeText(context, "Manifestation sealed & saved!", Toast.LENGTH_SHORT).show()
                                },
                                onViewVisionBoard = {
                                    commitCurrentVision()
                                    onNavigateVisionBoard()
                                },
                                onGoToAction = {
                                    commitCurrentVision()
                                    onNavigateActionTab()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// MANIFESTATION 5-LESSON COURSE HUB
// -------------------------------------------------------------------------------------------------
@Composable
fun ManifestationCourseHub(
    progress: Float,
    savedVision: ManifestationVisionData,
    onStartGuidedJourney: (Int) -> Unit,
    onNavigateVisionBoard: () -> Unit,
    onNavigateActionTab: () -> Unit
) {
    var expandedLessonIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1926)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFF9800).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "5 LESSONS COURSE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB74D),
                            letterSpacing = 0.6.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Text(
                        text = "${(progress * 100).toInt()}% Complete",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Manifestation Consciousness",
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Intention → Attention → Alignment → Action",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFCC80)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "What you repeatedly imagine changes what your reticular system notices, pursues, and prepares for. Manifestation is disciplined direction, not passive wishing.",
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFFD1D5DB)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { onStartGuidedJourney(1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_manifestation_journey_btn"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color(0xFF1E1926)
                    )
                ) {
                    Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "START 10-SCREEN GUIDED JOURNEY",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "COURSE CURRICULUM",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF756F84),
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Lesson 1
        ManifestationLessonCard(
            number = "1",
            title = "Lesson 1 — Understand",
            summary = "What manifestation actually means: Imagination + Direction, not magic.",
            fullText = "Manifestation is not about wishing harder or pretending reality doesn't exist. It begins when you deliberately choose what to create, bring it into repeated focus, and systematically align your decisions.",
            targetStep = 1,
            isExpanded = expandedLessonIndex == 1,
            onToggleExpand = { expandedLessonIndex = if (expandedLessonIndex == 1) -1 else 1 },
            onLaunch = { onStartGuidedJourney(1) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Lesson 2
        ManifestationLessonCard(
            number = "2",
            title = "Lesson 2 — Clarify",
            summary = "Define what you actually want: Transform vague wishes into concrete targets.",
            fullText = "You cannot intentionally create a direction you haven't clearly defined. Vague goals like 'I want to be rich' fail because they don't tell the nervous system what specific behavior to prioritize. Specify What, Why, When, and your proof of progress.",
            targetStep = 4,
            isExpanded = expandedLessonIndex == 2,
            onToggleExpand = { expandedLessonIndex = if (expandedLessonIndex == 2) -1 else 2 },
            onLaunch = { onStartGuidedJourney(4) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Lesson 3
        ManifestationLessonCard(
            number = "3",
            title = "Lesson 3 — Visualize",
            summary = "Build a vivid mental picture: Picture the life and environment around the result.",
            fullText = "Don't just imagine the end badge; immerse yourself into the physical environment (Where are you? What are you doing? Who is with you?). Pair this sensory realism with elevated emotion (Fulfilled, Peaceful, Powerful, Free).",
            targetStep = 6,
            isExpanded = expandedLessonIndex == 3,
            onToggleExpand = { expandedLessonIndex = if (expandedLessonIndex == 3) -1 else 3 },
            onLaunch = { onStartGuidedJourney(6) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Lesson 4
        ManifestationLessonCard(
            number = "4",
            title = "Lesson 4 — Align",
            summary = "Connect the vision to identity: Become the person who lives this life.",
            fullText = "Your vision isn't only about what you receive; it is about who you become. Define what your future self thinks, believes, consistently does, and the destructive habits they firmly leave behind.",
            targetStep = 8,
            isExpanded = expandedLessonIndex == 4,
            onToggleExpand = { expandedLessonIndex = if (expandedLessonIndex == 4) -1 else 4 },
            onLaunch = { onStartGuidedJourney(8) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Lesson 5
        ManifestationLessonCard(
            number = "5",
            title = "Lesson 5 — Act",
            summary = "Turn the intention into a real-world step: Today's single aligned action.",
            fullText = "A vision becomes powerful only when it alters today's calendar. Complete the loop by taking one concrete action today that directly flows from your future self's daily standard.",
            targetStep = 9,
            isExpanded = expandedLessonIndex == 5,
            onToggleExpand = { expandedLessonIndex = if (expandedLessonIndex == 5) -1 else 5 },
            onLaunch = { onStartGuidedJourney(9) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Active Manifestation Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFFFE0B2))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CURRENT SEALED VISION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = savedVision.dateStr,
                        fontSize = 11.5.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = savedVision.intention,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1926)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Core Value: ${savedVision.emotion} ${savedVision.emotionEmoji} · ${savedVision.category}",
                    fontSize = 12.5.sp,
                    color = Color(0xFF4B5563)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateVisionBoard,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(21.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D))
                    ) {
                        Icon(imageVector = Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFE65100))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Vision Board", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    }

                    Button(
                        onClick = onNavigateActionTab,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(21.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Action Tab", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ManifestationLessonCard(
    number: String,
    title: String,
    summary: String,
    fullText: String,
    targetStep: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onLaunch: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggleExpand() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFF3E0),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = number,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1926)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = summary,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        lineHeight = 16.sp
                    )
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Expand",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = fullText,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFF4B5563)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onLaunch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Practice This Lesson in Journey",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 10 INTERACTIVE MANIFESTATION SCREENS
// -------------------------------------------------------------------------------------------------

/** SCREEN 1 — THE HOOK */
@Composable
fun ManifestationScreen1Hook(onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 1 · THE HOOK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Surface(
            shape = CircleShape,
            color = Color(0xFFFFF3E0),
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "What you repeatedly imagine\ncan change what you notice,\npursue, and prepare for.",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926),
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "But imagination is only the beginning.",
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            color = Color(0xFF756F84),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFFFE0B2))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Manifestation = imagination + direction, not magic.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4B5563),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("manifestation_continue_btn"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

/** SCREEN 2 — WHAT IS MANIFESTATION? */
@Composable
fun ManifestationScreen2WhatIs(onContinue: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 2 · WHAT IS MANIFESTATION?",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "The 4-Pillar Pipeline",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Step 1
        ManifestationStepCard(
            title = "1. Clear Intention",
            text = "Manifestation begins with a clear intention. You decide what you want to create, experience, or become."
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Step 2
        ManifestationStepCard(
            title = "2. Focused Attention",
            text = "You bring that intention into your attention through thought, imagination, and repeated focus."
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Step 3
        ManifestationStepCard(
            title = "3. Identity Alignment",
            text = "You adopt the mindset, standards, and emotional frequency of the person who naturally lives that reality."
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Step 4
        ManifestationStepCard(
            title = "4. Grounded Action",
            text = "Then you act in alignment with it through deliberate, courageous daily execution."
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Formula badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1926))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CORE ARCHITECTURE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB74D),
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Intention → Attention → Alignment → Action",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("manifestation_continue_btn"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun ManifestationStepCard(title: String, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, fontSize = 13.sp, color = Color(0xFF4B5563), lineHeight = 19.sp)
        }
    }
}

/** SCREEN 3 — WHAT MANIFESTATION IS NOT */
@Composable
fun ManifestationScreen3WhatIsNot(onContinue: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 3 · WHAT MANIFESTATION IS NOT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "Healthy Expectations",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ManifestationMythCard(
            myth = "Manifestation is not about wishing harder.",
            truth = "Passively longing for an outcome creates desperation and anxiety."
        )

        Spacer(modifier = Modifier.height(10.dp))

        ManifestationMythCard(
            myth = "You cannot control every outcome.",
            truth = "You control your standard, your preparation, and your daily response."
        )

        Spacer(modifier = Modifier.height(10.dp))

        ManifestationMythCard(
            myth = "You cannot think your way around reality.",
            truth = "Physics, market dynamics, and human effort still govern results."
        )

        Spacer(modifier = Modifier.height(10.dp))

        ManifestationMythCard(
            myth = "You don't need to pretend everything is already perfect.",
            truth = "Acknowledge where you currently stand with complete honesty."
        )

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
            border = BorderStroke(1.dp, Color(0xFFC8E6C9))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "The goal is to become clear about what you want — and move toward it deliberately.",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32),
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("manifestation_continue_btn"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun ManifestationMythCard(myth: String, truth: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFFEE2E2))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = myth, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1926))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = truth, fontSize = 12.5.sp, color = Color(0xFF6B7280), lineHeight = 17.sp)
        }
    }
}

/** SCREEN 4 — START WITH CLARITY */
@Composable
fun ManifestationScreen4Clarity(
    intention: String,
    category: String,
    onIntentionChange: (String) -> Unit,
    onCategorySelect: (String, String) -> Unit,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 4 · START WITH CLARITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "You can't intentionally create a direction you haven't clearly defined.",
            fontFamily = FontFamily.Serif,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926),
            lineHeight = 29.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "WHAT DO YOU WANT?",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE65100),
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Grid
        CategorySampleRow(
            label = "Experience",
            sample = "I want to feel deeply free, present, and boundless.",
            emoji = "🕊️",
            isSelected = category == "Experience",
            onSelect = { onCategorySelect("Experience", "I want to feel deeply free, present, and boundless.") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        CategorySampleRow(
            label = "Achievement",
            sample = "I want to build a successful digital business.",
            emoji = "🏢",
            isSelected = category == "Achievement",
            onSelect = { onCategorySelect("Achievement", "I want to build a successful digital business.") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        CategorySampleRow(
            label = "Possession",
            sample = "I want a home of my own surrounded by peace.",
            emoji = "🏡",
            isSelected = category == "Possession",
            onSelect = { onCategorySelect("Possession", "I want a home of my own surrounded by peace.") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        CategorySampleRow(
            label = "Identity",
            sample = "I want to become deeply disciplined and sovereign.",
            emoji = "⚡",
            isSelected = category == "Identity",
            onSelect = { onCategorySelect("Identity", "I want to become deeply disciplined and sovereign.") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        CategorySampleRow(
            label = "Contribution",
            sample = "I want to help thousands of people through high-value work.",
            emoji = "🌍",
            isSelected = category == "Contribution",
            onSelect = { onCategorySelect("Contribution", "I want to help thousands of people through high-value work.") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Write your intention",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = intention,
            onValueChange = onIntentionChange,
            placeholder = { Text("e.g., I want to build a successful digital business.") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("manifestation_intention_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF9800),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            ),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("manifestation_continue_btn"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun CategorySampleRow(
    label: String,
    sample: String,
    emoji: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF3E0) else Color.White
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) Color(0xFFFF9800) else Color(0xFFF3F4F6)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFFE65100) else Color(0xFF756F84)
                )
                Text(
                    text = sample,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = Color(0xFF1E1926)
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/** SCREEN 5 — MAKE IT SPECIFIC */
@Composable
fun ManifestationScreen5Specific(
    whatText: String,
    whyText: String,
    whenText: String,
    howKnowText: String,
    onWhatChange: (String) -> Unit,
    onWhyChange: (String) -> Unit,
    onWhenChange: (String) -> Unit,
    onHowKnowChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 5 · MAKE IT SPECIFIC",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "What does success actually look like for you?",
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Transform vague wishes into high-resolution targets.",
            fontSize = 13.sp,
            color = Color(0xFF756F84)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Prompt 1: WHAT
        SpecificFieldBlock(
            prompt = "WHAT?",
            subtitle = "What exactly do you want?",
            value = whatText,
            placeholder = "e.g., Build a profitable online business with recurring revenue",
            onValueChange = onWhatChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Prompt 2: WHY
        SpecificFieldBlock(
            prompt = "WHY?",
            subtitle = "Why does it matter to you?",
            value = whyText,
            placeholder = "e.g., Freedom, sovereignty, and security for my family",
            onValueChange = onWhyChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Prompt 3: WHEN
        SpecificFieldBlock(
            prompt = "WHEN?",
            subtitle = "When would you like to move toward it?",
            value = whenText,
            placeholder = "e.g., Within the next 18-24 months",
            onValueChange = onWhenChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Prompt 4: HOW WILL YOU KNOW?
        SpecificFieldBlock(
            prompt = "HOW WILL YOU KNOW?",
            subtitle = "What would tell you that you're making progress?",
            value = howKnowText,
            placeholder = "e.g., First 100 paying customers & steady positive cash flow",
            onValueChange = onHowKnowChange
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("manifestation_continue_btn"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun SpecificFieldBlock(
    prompt: String,
    subtitle: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = prompt, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                Text(text = subtitle, fontSize = 11.5.sp, color = Color(0xFF6B7280))
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, fontSize = 12.5.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )
        }
    }
}

/** SCREEN 6 — SEE IT */
@Composable
fun ManifestationScreen6SeeIt(
    whereAreYou: String,
    whatDoing: String,
    whoWith: String,
    whatChanged: String,
    onWhereChange: (String) -> Unit,
    onWhatDoingChange: (String) -> Unit,
    onWhoWithChange: (String) -> Unit,
    onWhatChangedChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 6 · SEE IT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "Now imagine it as if it were real.",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Don't just picture the final result. Picture the life around it.",
            fontSize = 13.5.sp,
            color = Color(0xFF756F84)
        )

        Spacer(modifier = Modifier.height(18.dp))

        SpecificFieldBlock(
            prompt = "WHERE ARE YOU?",
            subtitle = "What does the environment look like?",
            value = whereAreYou,
            placeholder = "e.g., Sunlit modern workspace overlooking trees and garden",
            onValueChange = onWhereChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        SpecificFieldBlock(
            prompt = "WHAT ARE YOU DOING?",
            subtitle = "What are you actually doing?",
            value = whatDoing,
            placeholder = "e.g., Designing high-leverage software architectures calmly",
            onValueChange = onWhatDoingChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        SpecificFieldBlock(
            prompt = "WHO IS WITH YOU?",
            subtitle = "Who is part of this life?",
            value = whoWith,
            placeholder = "e.g., Supportive dream team, loyal partners, and family",
            onValueChange = onWhoWithChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        SpecificFieldBlock(
            prompt = "WHAT HAS CHANGED?",
            subtitle = "What is different from today?",
            value = whatChanged,
            placeholder = "e.g., Total financial sovereignty, zero scarcity stress",
            onValueChange = onWhatChangedChange
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("manifestation_continue_btn"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

/** SCREEN 7 — FEEL IT */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ManifestationScreen7FeelIt(
    selectedEmotion: String,
    emotionWhy: String,
    onEmotionSelect: (String, String) -> Unit,
    onEmotionWhyChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    val emotions = listOf(
        "Fulfilled" to "❤️",
        "Peaceful" to "🌿",
        "Powerful" to "🔥",
        "Excited" to "✨",
        "Free" to "🕊️",
        "Grateful" to "🙏"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 7 · FEEL IT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "What would this future mean to you?",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Choose the feeling you want to associate with this vision.",
            fontSize = 13.5.sp,
            color = Color(0xFF756F84)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Emotion chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            emotions.forEach { (name, emoji) ->
                val isSelected = selectedEmotion == name
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Color(0xFFFF9800) else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFFE65100) else Color(0xFFE5E7EB)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onEmotionSelect(name, emoji) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = emoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = name,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF1E1926)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "GO DEEPER",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE65100),
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Why would this feeling matter?",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = emotionWhy,
            onValueChange = onEmotionWhyChange,
            placeholder = { Text("e.g., I want freedom because I want control over how I spend my time and create value.") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF9800),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            ),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("manifestation_continue_btn"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

/** SCREEN 8 — BECOME THE PERSON */
@Composable
fun ManifestationScreen8BecomeThePerson(
    thinkTrait: String,
    believeTrait: String,
    doTrait: String,
    dontTrait: String,
    onThinkChange: (String) -> Unit,
    onBelieveChange: (String) -> Unit,
    onDoChange: (String) -> Unit,
    onDontChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 8 · BECOME THE PERSON",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "Your vision isn't only about what you get.",
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "It's also about who you become.",
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            color = Color(0xFFE65100)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "WHO IS THE PERSON WHO LIVES THIS LIFE?",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF756F84),
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        SpecificFieldBlock(
            prompt = "THEY THINK...",
            subtitle = "How do they think?",
            value = thinkTrait,
            placeholder = "e.g., They think in abundant long-term possibilities and compounding.",
            onValueChange = onThinkChange
        )

        Spacer(modifier = Modifier.height(10.dp))

        SpecificFieldBlock(
            prompt = "THEY BELIEVE...",
            subtitle = "What do they believe about themselves?",
            value = believeTrait,
            placeholder = "e.g., They believe they are fully worthy of extraordinary scale and peace.",
            onValueChange = onBelieveChange
        )

        Spacer(modifier = Modifier.height(10.dp))

        SpecificFieldBlock(
            prompt = "THEY DO...",
            subtitle = "What do they consistently do?",
            value = doTrait,
            placeholder = "e.g., They create deep value, take calculated risks, and finish what they start.",
            onValueChange = onDoChange
        )

        Spacer(modifier = Modifier.height(10.dp))

        SpecificFieldBlock(
            prompt = "THEY DON'T...",
            subtitle = "What habits do they leave behind?",
            value = dontTrait,
            placeholder = "e.g., They do not procrastinate, indulge in self-pity, or seek outside validation.",
            onValueChange = onDontChange
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("manifestation_create_future_self_btn"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
        ) {
            Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "CREATE FUTURE SELF & CONTINUE", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** SCREEN 9 — ALIGN IT WITH REALITY */
@Composable
fun ManifestationScreen9AlignReality(
    intention: String,
    doTrait: String,
    todayAction: String,
    onTodayActionChange: (String) -> Unit,
    onAddActionAndContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 9 · ALIGN IT WITH REALITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "A vision becomes useful when it changes what you do today.",
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926),
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFFFE0B2))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "ALIGNMENT CHAIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Vision: $intention", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E1926))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Future-Self Habit: $doTrait", fontSize = 12.5.sp, color = Color(0xFF4B5563))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ASK YOURSELF",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE65100),
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "What is one thing your future self would do today?",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "ONE ACTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF756F84))
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = todayAction,
            onValueChange = onTodayActionChange,
            placeholder = { Text("Today I will finish my landing page and send 10 emails.") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("today_aligned_action_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF9800),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            ),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onAddActionAndContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("add_to_action_btn"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "ADD TO ACTION & CONTINUE", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** SCREEN 10 — RETURN TO THE VISION */
@Composable
fun ManifestationScreen10ReturnVision(
    intention: String,
    emotion: String,
    emotionWhy: String,
    doTrait: String,
    todayAction: String,
    onSaveAndComplete: () -> Unit,
    onViewVisionBoard: () -> Unit,
    onGoToAction: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.padding(bottom = 14.dp)
        ) {
            Text(
                text = "SCREEN 10 · RETURN TO THE VISION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "See the vision.\nFeel the meaning.\nBecome the person.\nTake the step.",
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926),
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Visual Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFFFFB74D)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MY MANIFESTATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        letterSpacing = 0.8.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "SEALED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "I WANT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = intention, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1926))

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "I VALUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "$emotion — $emotionWhy", fontSize = 13.5.sp, color = Color(0xFF374151))

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "I AM BECOMING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = doTrait, fontSize = 13.5.sp, color = Color(0xFF374151))

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "TODAY I WILL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = todayAction, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF0FDF4),
            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "MANIFESTATION COMPLETE · Progress saved.",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF15803D)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onViewVisionBoard,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("view_my_vision_btn"),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(1.5.dp, Color(0xFFFFB74D))
            ) {
                Icon(imageVector = Icons.Default.GridView, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "VIEW MY VISION", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            }

            Button(
                onClick = onGoToAction,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("go_to_action_btn"),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "GO TO ACTION", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
