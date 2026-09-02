package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WealthIdentityStore
import kotlinx.coroutines.delay

enum class HypnagogicPhase {
    ONBOARDING,
    ACTIVE_SESSION,
    CAPTURE_REFLECT
}

@Composable
fun HypnagogicOnboardingScreen(
    identityStore: WealthIdentityStore,
    initialPhase: HypnagogicPhase = HypnagogicPhase.ONBOARDING,
    onExit: () -> Unit
) {
    var phase by remember { mutableStateOf(initialPhase) }
    var currentStep by remember { mutableIntStateOf(1) } // 1 to 10

    // Capture flow state
    val selectedTags = remember { mutableStateListOf<String>() }
    var memoryNote by remember { mutableStateOf("") }
    var relaxationRating by remember { mutableIntStateOf(4) }
    var vividnessRating by remember { mutableIntStateOf(3) }
    var keyInsightText by remember { mutableStateOf("") }
    var createActionTitle by remember { mutableStateOf("") }
    var captureSubStep by remember { mutableIntStateOf(1) } // 1: Notice tags & notes, 2: Ratings, 3: Insight, 4: Action

    // Update store progress as user navigates steps
    LaunchedEffect(currentStep, phase) {
        if (phase == HypnagogicPhase.ONBOARDING) {
            val progressRatio = currentStep / 10f
            identityStore.updateHypnagogicProgress(progressRatio)
        } else if (phase == HypnagogicPhase.ACTIVE_SESSION || phase == HypnagogicPhase.CAPTURE_REFLECT) {
            identityStore.updateHypnagogicProgress(1.0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF3EDF7), // Soft Lavender/Lilac top matching user reference
                        Color(0xFFF7F4FA),
                        Color(0xFFFCFAFE),
                        Color(0xFFFFFFFF),
                        Color(0xFFFBF8FE)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        when (phase) {
            HypnagogicPhase.ONBOARDING -> {
                OnboardingStepView(
                    step = currentStep,
                    totalSteps = 10,
                    onNext = {
                        if (currentStep < 10) {
                            currentStep++
                        } else {
                            phase = HypnagogicPhase.ACTIVE_SESSION
                        }
                    },
                    onPrevious = {
                        if (currentStep > 1) {
                            currentStep--
                        } else {
                            onExit()
                        }
                    },
                    onExit = onExit,
                    onStartSession = {
                        phase = HypnagogicPhase.ACTIVE_SESSION
                    }
                )
            }
            HypnagogicPhase.ACTIVE_SESSION -> {
                ActiveHypnagogicSessionView(
                    onSessionComplete = {
                        phase = HypnagogicPhase.CAPTURE_REFLECT
                    },
                    onExit = onExit
                )
            }
            HypnagogicPhase.CAPTURE_REFLECT -> {
                CaptureReflectFlowView(
                    subStep = captureSubStep,
                    selectedTags = selectedTags,
                    onToggleTag = { tag ->
                        if (selectedTags.contains(tag)) {
                            selectedTags.remove(tag)
                        } else {
                            selectedTags.add(tag)
                        }
                    },
                    memoryNote = memoryNote,
                    onMemoryNoteChange = { memoryNote = it },
                    relaxationRating = relaxationRating,
                    onRelaxationChange = { relaxationRating = it },
                    vividnessRating = vividnessRating,
                    onVividnessChange = { vividnessRating = it },
                    keyInsightText = keyInsightText,
                    onKeyInsightChange = { keyInsightText = it },
                    createActionTitle = createActionTitle,
                    onCreateActionChange = { createActionTitle = it },
                    onNextSubStep = {
                        if (captureSubStep < 4) {
                            if (captureSubStep == 1 && keyInsightText.isEmpty() && memoryNote.isNotBlank()) {
                                keyInsightText = memoryNote
                            }
                            if (captureSubStep == 3 && createActionTitle.isEmpty() && keyInsightText.isNotBlank()) {
                                createActionTitle = keyInsightText
                            }
                            captureSubStep++
                        } else {
                            // Finish and Save
                            identityStore.saveHypnagogicInsight(
                                tags = selectedTags.toList(),
                                note = memoryNote,
                                relaxationScore = relaxationRating,
                                vividnessScore = vividnessRating,
                                insightSummary = keyInsightText,
                                actionCreated = if (createActionTitle.isNotBlank()) createActionTitle else null
                            )
                            if (createActionTitle.isNotBlank()) {
                                identityStore.addWealthAction(
                                    title = createActionTitle,
                                    subtitle = "Created from Hypnagogic Insight",
                                    duration = "15 min"
                                )
                            }
                            identityStore.updateHypnagogicProgress(1.0f)
                            onExit()
                        }
                    },
                    onSkipAction = {
                        identityStore.saveHypnagogicInsight(
                            tags = selectedTags.toList(),
                            note = memoryNote,
                            relaxationScore = relaxationRating,
                            vividnessScore = vividnessRating,
                            insightSummary = keyInsightText,
                            actionCreated = null
                        )
                        identityStore.updateHypnagogicProgress(1.0f)
                        onExit()
                    },
                    onExit = onExit
                )
            }
        }
    }
}

/**
 * 10-Screen Onboarding Step View matching the exact layout of the user reference image.
 */
@Composable
fun OnboardingStepView(
    step: Int,
    totalSteps: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onExit: () -> Unit,
    onStartSession: () -> Unit
) {
    val progressAnimated by animateFloatAsState(
        targetValue = step.toFloat() / totalSteps.toFloat(),
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "onboardingProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // --- TOP NAVIGATION BAR (Exact design as screenshot: Back <, Progress Line, Close X) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPrevious,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("onboarding_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Back",
                    tint = Color(0xFF1E1926),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Sleek Horizontal Progress Line
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE5DEEE))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressAnimated)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFC084FC), // Lavender / Lilac
                                    Color(0xFF818CF8), // Soft Indigo
                                    Color(0xFFEC4899)  // Cherry pink
                                )
                            )
                        )
                )
            }

            IconButton(
                onClick = onExit,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("onboarding_close_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF1E1926),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- STEP CONTENT (Animated Transition) ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "stepContentAnimation"
            ) { targetStep ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center
                ) {
                    when (targetStep) {
                        1 -> Screen1Hook()
                        2 -> Screen2WhatIsHypnagogia()
                        3 -> Screen3WhatChanges()
                        4 -> Screen4WhatMightYouExperience()
                        5 -> Screen5DontForceIt()
                        6 -> Screen6WhatAreWePracticing()
                        7 -> Screen7WhyUseThisPractice()
                        8 -> Screen8Prepare()
                        9 -> Screen9HowToEnterState()
                        10 -> Screen10ReadyForFirstSession(
                            onStartSession = onStartSession,
                            onTakeActionLater = onExit
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTTOM CTA BUTTON (Exact clean pill from screenshot) ---
        if (step < 10) {
            val buttonText = when (step) {
                8 -> "MY SPACE IS READY"
                else -> "Continue"
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(28.dp), spotColor = Color(0x33A855F7))
                    .testTag("onboarding_continue_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF4F1F8), // Soft off-white lilac container matching reference
                    contentColor = Color(0xFF1E1926)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

/* ==========================================================================
   INDIVIDUAL 10 SCREENS
   ========================================================================== */

@Composable
fun Screen1Hook() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = "There is a moment\nbetween waking\nand sleeping.",
            fontFamily = FontFamily.Serif,
            fontSize = 34.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 44.sp,
            color = Color(0xFF201A24)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "A quiet threshold where everyday thinking softens and consciousness naturally begins to shift.",
            fontSize = 17.sp,
            lineHeight = 26.sp,
            color = Color(0xFF6B6275),
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun Screen2WhatIsHypnagogia() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = "That moment has a name.\n\nIt is called hypnagogia.",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 42.sp,
            color = Color(0xFF201A24)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "The natural, effortless transition from wakefulness into sleep.",
            fontSize = 17.sp,
            lineHeight = 26.sp,
            color = Color(0xFF6B6275)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Center subtle blurred transition graphic
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
            border = BorderStroke(1.dp, Color(0xFFE8E0F0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TransitionNode(label = "WAKE", color = Color(0xFF6366F1))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFC4B5FD), modifier = Modifier.size(18.dp))
                TransitionNode(label = "HYPNAGOGIA", color = Color(0xFFEC4899), isHighlighted = true)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFC4B5FD), modifier = Modifier.size(18.dp))
                TransitionNode(label = "SLEEP", color = Color(0xFF8B5CF6))
            }
        }
    }
}

@Composable
private fun TransitionNode(label: String, color: Color, isHighlighted: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isHighlighted) color.copy(alpha = 0.12f) else Color(0xFFF3F0F7))
            .border(
                width = if (isHighlighted) 1.5.dp else 0.dp,
                color = if (isHighlighted) color else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlighted) color else Color(0xFF4A4453),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun Screen3WhatChanges() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = "As you drift toward sleep,\nyour experience of your mind can change.",
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 38.sp,
            color = Color(0xFF201A24)
        )

        Spacer(modifier = Modifier.height(28.dp))

        ObservationBullet(text = "Thoughts may become less structured and more associative.")
        ObservationBullet(text = "Subtle images may appear and disappear spontaneously.")
        ObservationBullet(text = "Your attention begins to effortlessly widen and drift.")

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "You don't need to make this happen. It unfolds naturally on its own.",
            fontSize = 15.5.sp,
            fontStyle = FontStyle.Italic,
            color = Color(0xFF7C7287),
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun ObservationBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFFA855F7))
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = text,
            fontSize = 16.5.sp,
            lineHeight = 24.sp,
            color = Color(0xFF332D3B)
        )
    }
}

@Composable
fun Screen4WhatMightYouExperience() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "What might you experience?",
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF201A24)
        )

        Spacer(modifier = Modifier.height(20.dp))

        ExperienceCard(
            category = "SEE",
            description = "Shapes, colors, patterns, fleeting faces, or brief dreamlike scenes.",
            icon = Icons.Default.Visibility,
            color = Color(0xFFEC4899)
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExperienceCard(
            category = "HEAR",
            description = "A sound, word, familiar voice, melody, or brief ambient noise.",
            icon = Icons.Default.Hearing,
            color = Color(0xFF8B5CF6)
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExperienceCard(
            category = "FEEL",
            description = "Floating, falling, gentle lightness, or subtle body tingling.",
            icon = Icons.Default.SelfImprovement,
            color = Color(0xFF3B82F6)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Or you may notice nothing unusual at all — and that is completely natural.",
            fontSize = 15.sp,
            color = Color(0xFF6B6275),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ExperienceCard(
    category: String,
    description: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, Color(0xFFEDE5F3))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 14.5.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF2D2835)
                )
            }
        }
    }
}

@Composable
fun Screen5DontForceIt() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = "You don't have to see anything.",
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 36.sp,
            color = Color(0xFF201A24)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "You don't have to hear anything.\n\nYou don't have to create an experience.",
            fontSize = 19.sp,
            lineHeight = 30.sp,
            color = Color(0xFF4C4456)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2EBF9)),
            border = BorderStroke(1.dp, Color(0xFFE2D6EE))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Nothing happening is completely okay.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B21A8)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your only role is to notice, not to force.",
                    fontSize = 15.sp,
                    color = Color(0xFF4C1D95)
                )
            }
        }
    }
}

@Composable
fun Screen6WhatAreWePracticing() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = "This practice isn't about chasing strange experiences.",
            fontFamily = FontFamily.Serif,
            fontSize = 26.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 36.sp,
            color = Color(0xFF201A24)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "It is about learning to notice what happens as your mind drifts toward restful sleep.",
            fontSize = 16.5.sp,
            lineHeight = 24.sp,
            color = Color(0xFF665E70)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PillarChip(title = "Notice", subtitle = "Transitions")
            PillarChip(title = "Remember", subtitle = "Associations")
            PillarChip(title = "Reflect", subtitle = "Insights")
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "That is the entire practice.",
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
            fontStyle = FontStyle.Italic,
            color = Color(0xFF3B3344)
        )
    }
}

@Composable
private fun PillarChip(title: String, subtitle: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        border = BorderStroke(1.dp, Color(0xFFE7DFEF)),
        modifier = Modifier.width(96.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7C3AED)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF6B6275),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun Screen7WhyUseThisPractice() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = "Observation begins where ordinary thinking starts to fade.",
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 38.sp,
            color = Color(0xFF201A24)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "You may notice thoughts, images, or creative associations that you would normally overlook in a busy day.",
            fontSize = 16.5.sp,
            lineHeight = 26.sp,
            color = Color(0xFF4C4456)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            border = BorderStroke(1.dp, Color(0xFFEDE5F3))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "• The goal is not to predict the future.",
                    fontSize = 14.5.sp,
                    color = Color(0xFF5B5365)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• The goal is to notice your own experience more carefully and capture authentic inspiration.",
                    fontSize = 14.5.sp,
                    color = Color(0xFF5B5365)
                )
            }
        }
    }
}

@Composable
fun Screen8Prepare() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Before you begin",
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF201A24)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrepareCard(number = "01", title = "POSITION", desc = "Get comfortable (reclined chair or bed).")
        Spacer(modifier = Modifier.height(8.dp))
        PrepareCard(number = "02", title = "ENVIRONMENT", desc = "Choose a quiet, low-distraction place.")
        Spacer(modifier = Modifier.height(8.dp))
        PrepareCard(number = "03", title = "TIME", desc = "Give yourself 10 uninterrupted minutes of rest.")
        Spacer(modifier = Modifier.height(8.dp))
        PrepareCard(
            number = "04",
            title = "SAFETY",
            desc = "Do not practice when you need to drive or maintain active vigilance."
        )
    }
}

@Composable
private fun PrepareCard(number: String, title: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        border = BorderStroke(1.dp, Color(0xFFECE4F2))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = number,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA855F7)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF332D3B),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = desc,
                    fontSize = 14.sp,
                    color = Color(0xFF554D5E),
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
fun Screen9HowToEnterState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "5 Simple Steps",
            fontFamily = FontFamily.Serif,
            fontSize = 26.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF201A24)
        )

        Spacer(modifier = Modifier.height(14.dp))

        StepRow(step = 1, text = "Get comfortable.")
        StepRow(step = 2, text = "Close your eyes.")
        StepRow(step = 3, text = "Let your body relax naturally.")
        StepRow(step = 4, text = "Allow your thoughts to drift.")
        StepRow(step = 5, text = "Notice whatever appears.")

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF)),
            border = BorderStroke(1.dp, Color(0xFFE9D5FF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Don't chase it.\nDon't stop it.\nJust notice.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF581C87)
                )
            }
        }
    }
}

@Composable
private fun StepRow(step: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFF8B5CF6)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$step", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 15.5.sp,
            color = Color(0xFF2D2735),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Screen10ReadyForFirstSession(
    onStartSession: () -> Unit,
    onTakeActionLater: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Stage pill
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF3E8FF),
            border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF7C3AED),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "KNOWLEDGE COMPLETE · ACTION STAGE",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C3AED),
                    letterSpacing = 0.5.sp
                )
            }
        }

        Text(
            text = "You are going to Action now.",
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF201A24),
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "You have mastered the foundational science and observation principles. Real transformation occurs through the active 10-minute session.\n\nYou can start immediately right here, or resume whenever you're ready from your Action tab.",
            fontSize = 15.sp,
            lineHeight = 23.sp,
            color = Color(0xFF554D5E)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Session launch card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFFC084FC)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3E8FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = null,
                        tint = Color(0xFF9333EA),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "HYPNAGOGIC NAP ACTION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7E22CE),
                    letterSpacing = 1.sp
                )

                Text(
                    text = "10-Minute Quiet Immersion",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1926)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Observe  →  Remember  →  Capture Insight",
                    fontSize = 13.sp,
                    color = Color(0xFF7C7287)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onStartSession,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_first_session_btn"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED),
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "START ACTION SESSION NOW",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onTakeActionLater,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("take_action_later_btn"),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF6B21A8)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Take Action Later in Action Tab",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/* ==========================================================================
   10-MINUTE ACTIVE HYPNAGOGIC SESSION (Minimal, Quiet, Relaxing)
   ========================================================================== */

@Composable
fun ActiveHypnagogicSessionView(
    onSessionComplete: () -> Unit,
    onExit: () -> Unit
) {
    var secondsRemaining by remember { mutableIntStateOf(600) } // 10 minutes = 600s
    var isPaused by remember { mutableStateOf(false) }

    // Breathing pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(isPaused) {
        while (!isPaused && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
        if (secondsRemaining <= 0) {
            onSessionComplete()
        }
    }

    val elapsedSeconds = 600 - secondsRemaining
    val phaseDescription = when (elapsedSeconds) {
        in 0..60 -> "Phase 1: Get comfortable and close your eyes."
        in 61..180 -> "Phase 2: Let your body relax naturally."
        in 181..300 -> "Phase 3: Allow thoughts and associations to drift."
        in 301..480 -> "Phase 4: Quietly notice whatever appears."
        else -> "Phase 5: Remember what stands out as you return."
    }

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hypnagogic Session",
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF241E2B)
            )

            IconButton(onClick = onExit) {
                Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color(0xFF554D5E))
            }
        }

        // Center Pulsing Circle & Timer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFFC084FC).copy(alpha = 0.25f),
                                Color(0xFFDDD6FE).copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.5.dp, Color(0xFFC4B5FD).copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFF261E2E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "REST & NOTICE",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF7C3AED)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = phaseDescription,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF4C4456),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Bottom Session Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { isPaused = !isPaused },
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFD8B4FE)),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = null,
                    tint = Color(0xFF7C3AED)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPaused) "Resume" else "Pause",
                    color = Color(0xFF7C3AED),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onSessionComplete,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Finish & Capture",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/* ==========================================================================
   POST-SESSION CAPTURE & REFLECTION FLOW
   ========================================================================== */

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaptureReflectFlowView(
    subStep: Int,
    selectedTags: List<String>,
    onToggleTag: (String) -> Unit,
    memoryNote: String,
    onMemoryNoteChange: (String) -> Unit,
    relaxationRating: Int,
    onRelaxationChange: (Int) -> Unit,
    vividnessRating: Int,
    onVividnessChange: (Int) -> Unit,
    keyInsightText: String,
    onKeyInsightChange: (String) -> Unit,
    createActionTitle: String,
    onCreateActionChange: (String) -> Unit,
    onNextSubStep: () -> Unit,
    onSkipAction: () -> Unit,
    onExit: () -> Unit
) {
    val availableTags = listOf("Visual", "Sound", "Thought", "Sensation", "Nothing noticeable")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step $subStep of 4",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9333EA),
                letterSpacing = 0.5.sp
            )

            IconButton(onClick = onExit) {
                Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color(0xFF554D5E))
            }
        }

        LinearProgressIndicator(
            progress = { subStep / 4f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Color(0xFF9333EA),
            trackColor = Color(0xFFE9D5FF),
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (subStep) {
                1 -> {
                    // SUB-STEP 1: DON'T MOVE TOO QUICKLY
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "DON'T MOVE TOO QUICKLY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7E22CE),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Before you move on,\nremember what stood out.",
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            color = Color(0xFF241E2B),
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "What did you notice?",
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3D3546)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableTags.forEach { tag ->
                                val isSelected = selectedTags.contains(tag)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) Color(0xFF7C3AED) else Color.White)
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF7C3AED) else Color(0xFFD8B4FE),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable { onToggleTag(tag) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 13.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Color(0xFF3B3344)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Write whatever you remember:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3D3546)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = memoryNote,
                            onValueChange = onMemoryNoteChange,
                            placeholder = { Text("E.g., A floating golden sphere, sudden clarity about my client project...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0xFFD8B4FE),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }
                2 -> {
                    // SUB-STEP 2: REFLECTION RATINGS
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "YOUR EXPERIENCE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7E22CE),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "How did the state feel?",
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            color = Color(0xFF241E2B)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "How relaxed did you feel?",
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3D3546)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RatingScoreBar(
                            selectedScore = relaxationRating,
                            onSelectScore = onRelaxationChange
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "How vivid was it?",
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3D3546)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RatingScoreBar(
                            selectedScore = vividnessRating,
                            onSelectScore = onVividnessChange
                        )
                    }
                }
                3 -> {
                    // SUB-STEP 3: SAVE YOUR INSIGHT
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "SAVE YOUR INSIGHT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7E22CE),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Was there something\nworth remembering?",
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            color = Color(0xFF241E2B),
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Captured Insight:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3D3546)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = keyInsightText,
                            onValueChange = onKeyInsightChange,
                            placeholder = { Text("E.g., I should streamline the client onboarding flow and trust my intuition.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0xFFD8B4FE),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }
                4 -> {
                    // SUB-STEP 4: TURN IT INTO ACTION?
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "TRANSFORMATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7E22CE),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Turn insight into action?",
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            color = Color(0xFF241E2B)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        if (keyInsightText.isNotBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Your Insight:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7E22CE)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = keyInsightText,
                                        fontSize = 14.5.sp,
                                        color = Color(0xFF381E72)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Text(
                            text = "Tomorrow's Action:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3D3546)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = createActionTitle,
                            onValueChange = onCreateActionChange,
                            placeholder = { Text("E.g., Review onboarding flow and draft 3 improvements.") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0xFFD8B4FE),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onNextSubStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
            ) {
                Text(
                    text = when (subStep) {
                        4 -> if (createActionTitle.isNotBlank()) "Create Action & Complete" else "Save Only & Complete"
                        else -> "Next"
                    },
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (subStep == 4) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onSkipAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFC4B5FD))
                ) {
                    Text("Skip Action (Save Insight Only)", color = Color(0xFF6D28D9))
                }
            }
        }
    }
}

@Composable
private fun RatingScoreBar(
    selectedScore: Int,
    onSelectScore: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        (1..5).forEach { score ->
            val isSelected = selectedScore == score
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF7C3AED) else Color.White)
                    .border(
                        1.dp,
                        if (isSelected) Color(0xFF7C3AED) else Color(0xFFD8B4FE),
                        CircleShape
                    )
                    .clickable { onSelectScore(score) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$score",
                    fontSize = 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Color(0xFF3B3344)
                )
            }
        }
    }
}
