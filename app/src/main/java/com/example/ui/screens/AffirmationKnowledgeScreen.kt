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
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.local.WealthIdentityStore

enum class AffirmationKnowledgeViewMode {
    OVERVIEW_HUB,
    INTERACTIVE_FLOW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AffirmationKnowledgeScreen(
    identityStore: WealthIdentityStore,
    initialMode: AffirmationKnowledgeViewMode = AffirmationKnowledgeViewMode.INTERACTIVE_FLOW,
    initialStep: Int = 1,
    onNavigateAffirmationAction: () -> Unit = {},
    onNavigateIAm: () -> Unit = {},
    onExit: () -> Unit = {}
) {
    val context = LocalContext.current
    var currentMode by remember { mutableStateOf(initialMode) }
    var currentStep by remember { mutableIntStateOf(initialStep.coerceIn(1, 6)) }

    val savedCurrentBelief by identityStore.savedCurrentBelief.collectAsState()
    val savedTransformedBelief by identityStore.savedTransformedBelief.collectAsState()
    val progressVal by identityStore.affirmationKnowledgeProgress.collectAsState()

    var currentBeliefText by remember(savedCurrentBelief) { mutableStateOf(savedCurrentBelief) }
    var transformedBeliefText by remember(savedTransformedBelief) { mutableStateOf(savedTransformedBelief) }
    var actionCommitment by remember { mutableStateOf("Take 1 bold action today aligned with my new identity") }

    fun commitProgress() {
        identityStore.saveBeliefTransformation(
            current = currentBeliefText.ifBlank { "Not confident enough" },
            transformed = transformedBeliefText.ifBlank { "I am learning to trust myself and act with conviction." }
        )
        identityStore.updateAffirmationProgress((currentStep / 6f).coerceIn(0.15f, 1f))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (currentMode == AffirmationKnowledgeViewMode.OVERVIEW_HUB) "Affirmations Masterclass" else "Affirmation Knowledge Flow",
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1926)
                        )
                        Text(
                            text = if (currentMode == AffirmationKnowledgeViewMode.OVERVIEW_HUB) "6 Core Screens · Words to Reality" else "Screen $currentStep of 6",
                            fontSize = 12.sp,
                            color = Color(0xFF756F84)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentMode == AffirmationKnowledgeViewMode.INTERACTIVE_FLOW && currentStep > 1) {
                                currentStep--
                            } else if (currentMode == AffirmationKnowledgeViewMode.INTERACTIVE_FLOW && currentMode != initialMode) {
                                currentMode = AffirmationKnowledgeViewMode.OVERVIEW_HUB
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
                    if (currentMode == AffirmationKnowledgeViewMode.INTERACTIVE_FLOW) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFEBEE),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable {
                                    commitProgress()
                                    currentMode = AffirmationKnowledgeViewMode.OVERVIEW_HUB
                                }
                        ) {
                            Text(
                                text = "Overview",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE91E63),
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
                label = "AffirmationKnowledgeTransition"
            ) { state ->
                val mode = state.first
                val step = state.second

                if (mode == AffirmationKnowledgeViewMode.OVERVIEW_HUB) {
                    AffirmationOverviewHub(
                        progress = progressVal,
                        savedCurrentBelief = savedCurrentBelief,
                        savedTransformedBelief = savedTransformedBelief,
                        onStartFlow = { startStep ->
                            currentStep = startStep
                            currentMode = AffirmationKnowledgeViewMode.INTERACTIVE_FLOW
                        },
                        onStartAction = {
                            commitProgress()
                            onNavigateAffirmationAction()
                        }
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
                            progress = { step / 6f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFE91E63),
                            trackColor = Color(0xFFFFCDD2)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        when (step) {
                            1 -> AffirmationScreen1WhatAre(
                                onContinue = {
                                    commitProgress()
                                    currentStep = 2
                                }
                            )
                            2 -> AffirmationScreen2MindListening(
                                onContinue = {
                                    commitProgress()
                                    currentStep = 3
                                }
                            )
                            3 -> AffirmationScreen3NotMagic(
                                onContinue = {
                                    commitProgress()
                                    currentStep = 4
                                }
                            )
                            4 -> AffirmationScreen4CurrentBelief(
                                currentBelief = currentBeliefText,
                                transformedBelief = transformedBeliefText,
                                onCurrentBeliefChange = { currentBeliefText = it },
                                onTransformedBeliefChange = { transformedBeliefText = it },
                                onContinue = {
                                    commitProgress()
                                    currentStep = 5
                                }
                            )
                            5 -> AffirmationScreen5BelievableBridge(
                                currentBelief = currentBeliefText,
                                transformedBelief = transformedBeliefText,
                                onUpdateBridge = { transformedBeliefText = it },
                                onContinue = {
                                    commitProgress()
                                    currentStep = 6
                                }
                            )
                            6 -> AffirmationScreen6WordsToIdentity(
                                transformedAffirmation = transformedBeliefText,
                                actionCommitment = actionCommitment,
                                onActionCommitmentChange = { actionCommitment = it },
                                onAddActionToDaily = {
                                    commitProgress()
                                    identityStore.addWealthAction(
                                        title = actionCommitment.ifBlank { "Affirmation Alignment Practice" },
                                        subtitle = "Anchor new identity in real-world behavior",
                                        duration = "10 min"
                                    )
                                    Toast.makeText(context, "Action added to your daily Action tab!", Toast.LENGTH_SHORT).show()
                                },
                                onStartAffirmations = {
                                    commitProgress()
                                    identityStore.updateAffirmationProgress(1.0f)
                                    onNavigateAffirmationAction()
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
// OVERVIEW HUB (Summary of 6 Screens)
// -------------------------------------------------------------------------------------------------
@Composable
fun AffirmationOverviewHub(
    progress: Float,
    savedCurrentBelief: String,
    savedTransformedBelief: String,
    onStartFlow: (Int) -> Unit,
    onStartAction: () -> Unit
) {
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
                        color = Color(0xFFE91E63).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "6-SCREEN KNOWLEDGE FLOW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF4081),
                            letterSpacing = 0.6.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Text(
                        text = "${(progress * 100).toInt()}% Complete",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF80AB)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Words Shape Your Inner World",
                    fontFamily = FontFamily.Serif,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Words + Emotion + Repetition + Action",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF80AB)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "You are always affirming something. The question is not whether you affirm—the question is: What are you affirming every day?",
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFFD1D5DB)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { onStartFlow(1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_affirmation_flow_btn"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63),
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "START 6-SCREEN FLOW",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "6-SCREEN CURRICULUM",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF756F84),
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        AffirmationLessonItem(
            step = 1,
            title = "Screen 1 — What Are Affirmations?",
            subtitle = "Your words shape your inner world.",
            body = "Every day you speak to yourself. Automatic negative thoughts are also affirmations. Learn how to consciously choose declarations.",
            onClick = { onStartFlow(1) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        AffirmationLessonItem(
            step = 2,
            title = "Screen 2 — Mind Is Already Listening",
            subtitle = "You are always affirming something.",
            body = "Thought → Repetition → Belief → Behavior. Your internal monologue directs your self-image 24/7.",
            onClick = { onStartFlow(2) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        AffirmationLessonItem(
            step = 3,
            title = "Screen 3 — Don't Work Like Magic",
            subtitle = "Words alone don't create change.",
            body = "Affirmations are not magic spells. Real transformation occurs at the intersection of Words + Emotion + Repetition + Action.",
            onClick = { onStartFlow(3) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        AffirmationLessonItem(
            step = 4,
            title = "Screen 4 — Find Your Current Belief",
            subtitle = "What do you currently believe about yourself?",
            body = "Interactive diagnosis: Complete 'Deep down I believe...' and construct your new empowering direction.",
            onClick = { onStartFlow(4) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        AffirmationLessonItem(
            step = 5,
            title = "Screen 5 — Choose the Right Affirmation",
            subtitle = "Make it believable.",
            body = "Avoid massive cognitive rejection. Build believable psychological bridges that honor where you are while speaking toward where you want to go.",
            onClick = { onStartFlow(5) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        AffirmationLessonItem(
            step = 6,
            title = "Screen 6 — Turn Words Into Identity",
            subtitle = "Repeat it. Feel it. Live it.",
            body = "The 4-step execution framework: Read 👁️, Repeat 🗣️, Reflect 🧠, Act ⚡. Seamless launch to Action Tab.",
            onClick = { onStartFlow(6) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Saved Bridge Transformation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFFFCDD2))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "YOUR ACTIVE BELIEF BRIDGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC2185B),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("❌", fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Current: \"$savedCurrentBelief\"",
                        fontSize = 13.sp,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✅", fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New Direction: \"$savedTransformedBelief\"",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onStartAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(21.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("START YOUR AFFIRMATIONS (ACTION)", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AffirmationLessonItem(
    step: Int,
    title: String,
    subtitle: String,
    body: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFEBEE),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$step",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E63)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1926)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE91E63)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = body,
                    fontSize = 12.sp,
                    color = Color(0xFF616161),
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 6 INTERACTIVE SCREENS
// -------------------------------------------------------------------------------------------------

/** SCREEN 1 — WHAT ARE AFFIRMATIONS? */
@Composable
fun AffirmationScreen1WhatAre(onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFEBEE),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 1 · WHAT ARE AFFIRMATIONS?",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Surface(
            shape = CircleShape,
            color = Color(0xFFFFEBEE),
            modifier = Modifier.size(76.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your words shape\nyour inner world.",
            fontFamily = FontFamily.Serif,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926),
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Har din aap apne aap se kuch na kuch kehte rehte ho.",
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            color = Color(0xFF616161),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Negative examples card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFFFCDD2))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "AUTOMATIC INTERNAL DIALOGUE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC2185B),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                AffirmationQuoteBubble("“Main nahi kar sakta.” (I can't do this.)")
                Spacer(modifier = Modifier.height(6.dp))
                AffirmationQuoteBubble("“Mere saath hamesha galat hota hai.” (Things always go wrong.)")
                Spacer(modifier = Modifier.height(6.dp))
                AffirmationQuoteBubble("“Main enough nahi hoon.” (I'm not enough.)")

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Ye bhi repeated affirmations hi hain—bas negative direction mein.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE53935),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Definition Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1926))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Affirmation = consciously chosen statement jo aap baar-baar apne mind ko dete ho.",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("affirmation_continue_btn_1"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun AffirmationQuoteBubble(text: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFFF1F2),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 12.5.sp,
            color = Color(0xFF9E2A2B),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

/** SCREEN 2 — YOUR MIND IS ALREADY LISTENING */
@Composable
fun AffirmationScreen2MindListening(onContinue: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFEBEE),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 2 · YOUR MIND IS ALREADY LISTENING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "You are always\naffirming something.",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926),
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Aapka inner dialogue continuously aapki self-image ko influence karta hai.",
            fontSize = 14.sp,
            color = Color(0xFF616161),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // The Negative Loop Sequence
        Text(
            text = "THE REINFORCEMENT LOOP",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF756F84),
            letterSpacing = 0.6.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                LoopStepRow(
                    tag = "THOUGHT",
                    title = "“I'm not good enough.”",
                    color = Color(0xFFFF8A80)
                )

                LoopArrow()

                LoopStepRow(
                    tag = "REPETITION",
                    title = "Thought repeats hundreds of times unnoticed",
                    color = Color(0xFFFF5252)
                )

                LoopArrow()

                LoopStepRow(
                    tag = "BELIEF",
                    title = "Self-doubt hardens into baseline identity",
                    color = Color(0xFFE53935)
                )

                LoopArrow()

                LoopStepRow(
                    tag = "BEHAVIOR",
                    title = "You avoid big opportunities & stay small",
                    color = Color(0xFFC62828)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Punchline Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)),
            border = BorderStroke(1.dp, Color(0xFFFF80AB))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "The question is not whether you affirm.",
                    fontSize = 14.sp,
                    color = Color(0xFF880E4F),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "The question is: What are you affirming every day?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC2185B)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("affirmation_continue_btn_2"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun LoopStepRow(tag: String, title: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = color.copy(alpha = 0.15f),
            modifier = Modifier.width(90.dp)
        ) {
            Text(
                text = tag,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF212121),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LoopArrow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 38.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Text("↓", fontSize = 14.sp, color = Color(0xFFBDBDBD), fontWeight = FontWeight.Bold)
    }
}

/** SCREEN 3 — AFFIRMATIONS DON'T WORK LIKE MAGIC */
@Composable
fun AffirmationScreen3NotMagic(onContinue: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFEBEE),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 3 · AFFIRMATIONS DON'T WORK LIKE MAGIC",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "Words alone don't\ncreate change.",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926),
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Affirmations magic spells nahi hain. Sirf 100 baar 'I am successful' bolne se automatically success nahi aayegi.",
            fontSize = 14.sp,
            color = Color(0xFF616161),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Real change tab stronger hota hai jab 4 elements judte hain:",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 4 Elements
        ElementCard(
            emoji = "🗣️",
            name = "Words",
            description = "What you repeatedly tell yourself consciously."
        )

        Spacer(modifier = Modifier.height(8.dp))

        ElementCard(
            emoji = "❤️",
            name = "Emotion",
            description = "The genuine feeling and emotional frequency you connect with it."
        )

        Spacer(modifier = Modifier.height(8.dp))

        ElementCard(
            emoji = "🔁",
            name = "Repetition",
            description = "How consistently you practice daily to forge neural myelination."
        )

        Spacer(modifier = Modifier.height(8.dp))

        ElementCard(
            emoji = "⚡",
            name = "Action",
            description = "How you behave differently in the real world to validate the belief."
        )

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("affirmation_continue_btn_3"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun ElementCard(emoji: String, name: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                Text(text = description, fontSize = 12.5.sp, color = Color(0xFF616161), lineHeight = 17.sp)
            }
        }
    }
}

/** SCREEN 4 — FIND YOUR CURRENT BELIEF (Interactive) */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AffirmationScreen4CurrentBelief(
    currentBelief: String,
    transformedBelief: String,
    onCurrentBeliefChange: (String) -> Unit,
    onTransformedBeliefChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    val beliefChips = listOf(
        "Not good enough",
        "Capable",
        "Behind everyone",
        "Lucky",
        "Not confident",
        "Unworthy",
        "Powerful"
    )

    val sampleTransformations = mapOf(
        "Not good enough" to "I am continually growing and more than enough for my vision.",
        "Behind everyone" to "I run my own race at my own divinely appointed pace.",
        "Not confident" to "I am learning to trust myself and act with conviction.",
        "Unworthy" to "I am inherently worthy of massive abundance and peace.",
        "Capable" to "I master whatever skill my future demands with ease.",
        "Lucky" to "I prepare relentlessly, and opportunity meets my standard.",
        "Powerful" to "I direct my focus and energy with unshakable sovereignty."
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFEBEE),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 4 · FIND YOUR CURRENT BELIEF",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "What do you currently\nbelieve about yourself?",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926),
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Complete this sentence:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF756F84)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "“Deep down, I believe that I am ______.”",
            fontSize = 16.sp,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE91E63)
        )

        Spacer(modifier = Modifier.height(14.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            beliefChips.forEach { chip ->
                val isSelected = currentBelief.equals(chip, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFFE91E63) else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFFE91E63) else Color(0xFFFFCDD2)),
                    modifier = Modifier.clickable {
                        onCurrentBeliefChange(chip)
                        sampleTransformations[chip]?.let { onTransformedBeliefChange(it) }
                    }
                ) {
                    Text(
                        text = chip,
                        fontSize = 12.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color(0xFF424242),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentBelief,
            onValueChange = onCurrentBeliefChange,
            label = { Text("Or describe your current belief here") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE91E63),
                unfocusedBorderColor = Color(0xFFFFCDD2),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "What belief would you like to build instead?",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = transformedBelief,
            onValueChange = onTransformedBeliefChange,
            placeholder = { Text("e.g. I am learning to trust myself.") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4CAF50),
                unfocusedBorderColor = Color(0xFFC8E6C9),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Transformation Preview Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1926))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TRANSFORMATION BLUEPRINT",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF80AB),
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current: $currentBelief",
                    fontSize = 13.sp,
                    color = Color(0xFFB0BEC5)
                )
                Text(
                    text = "↓ Transformation",
                    fontSize = 12.sp,
                    color = Color(0xFFFF4081),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "New Direction: $transformedBelief",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE8F5E9)
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("affirmation_continue_btn_4"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

/** SCREEN 5 — CHOOSE THE RIGHT AFFIRMATION (The Bridge) */
@Composable
fun AffirmationScreen5BelievableBridge(
    currentBelief: String,
    transformedBelief: String,
    onUpdateBridge: (String) -> Unit,
    onContinue: () -> Unit
) {
    val bridgeStarters = listOf(
        "I am learning to...",
        "Every day I become more...",
        "I am practicing to handle...",
        "I am choosing to believe..."
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFEBEE),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 5 · CHOOSE THE RIGHT AFFIRMATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "Make it believable.",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Agar current belief hai 'I always fail' aur aap bolte ho 'I am the greatest person in the world', to mind usse reject kar deta hai.",
            fontSize = 13.5.sp,
            color = Color(0xFF616161),
            lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bridge Comparison
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Current: I always fail.",
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF1F2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("❌", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Too disconnected:\n“I am unstoppable and perfect.”",
                            fontSize = 12.5.sp,
                            color = Color(0xFFB71C1C),
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✅", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Better Bridge:\n“I am learning from every experience and becoming stronger.”",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CHOOSE OR REFINE YOUR BRIDGE PHRASE",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF756F84),
            letterSpacing = 0.6.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bridgeStarters.take(2).forEach { starter ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFEBEE),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onUpdateBridge("$starter ")
                        }
                ) {
                    Text(
                        text = starter,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E63),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = transformedBelief,
            onValueChange = onUpdateBridge,
            label = { Text("Your Believable Bridge Affirmation") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE91E63),
                unfocusedBorderColor = Color(0xFFFFCDD2),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Core Lesson Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            border = BorderStroke(1.dp, Color(0xFFFFE0B2))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.SelfImprovement, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "CORE LESSON",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Start where you are.\nSpeak toward where you want to go.",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBF360C)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("affirmation_continue_btn_5"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Text(text = "Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

/** SCREEN 6 — TURN WORDS INTO IDENTITY */
@Composable
fun AffirmationScreen6WordsToIdentity(
    transformedAffirmation: String,
    actionCommitment: String,
    onActionCommitmentChange: (String) -> Unit,
    onAddActionToDaily: () -> Unit,
    onStartAffirmations: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFEBEE),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "SCREEN 6 · TURN WORDS INTO IDENTITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "Repeat it. Feel it. Live it.",
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1926)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Final 4-step framework
        ActionFrameworkCard(
            stepNum = "1",
            icon = "👁️",
            title = "READ",
            desc = "Apni affirmation consciously padho."
        )

        Spacer(modifier = Modifier.height(8.dp))

        ActionFrameworkCard(
            stepNum = "2",
            icon = "🗣️",
            title = "REPEAT",
            desc = "Usse regularly repeat karo (feel every syllable)."
        )

        Spacer(modifier = Modifier.height(8.dp))

        ActionFrameworkCard(
            stepNum = "3",
            icon = "🧠",
            title = "REFLECT",
            desc = "Notice karo ki aapke purane thoughts kya keh rahe hain."
        )

        Spacer(modifier = Modifier.height(8.dp))

        ActionFrameworkCard(
            stepNum = "4",
            icon = "⚡",
            title = "ACT",
            desc = "Aaj ek aisa action lo jo aapki new identity support kare."
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Final message
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1926))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SEALED AFFIRMATION",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF80AB),
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "“$transformedAffirmation”",
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "An affirmation becomes more powerful when your actions begin to support the person you are becoming.",
                    fontSize = 12.5.sp,
                    color = Color(0xFFE0E0E0),
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Aligned Action Input
        OutlinedTextField(
            value = actionCommitment,
            onValueChange = onActionCommitmentChange,
            label = { Text("Today's 1 Supporting Action") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE91E63),
                unfocusedBorderColor = Color(0xFFFFCDD2),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onAddActionToDaily,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            shape = RoundedCornerShape(21.dp),
            border = BorderStroke(1.dp, Color(0xFFE91E63))
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add to Action Tab", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Final CTA: Start Your Affirmations (Takes user to Affirmation Action page)
        Button(
            onClick = onStartAffirmations,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("start_affirmations_cta_btn"),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Start Your Affirmations", fontSize = 15.5.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ActionFrameworkCard(stepNum: String, icon: String, title: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFEBEE),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = icon, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "$stepNum. $title", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1926))
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = desc, fontSize = 12.sp, color = Color(0xFF616161))
            }
        }
    }
}
