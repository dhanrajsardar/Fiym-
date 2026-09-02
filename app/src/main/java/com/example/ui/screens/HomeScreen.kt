package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.local.WealthActionItem
import com.example.data.local.WealthIdentityStore

data class KnowledgeLesson(
    val id: String,
    val title: String,
    val subtitle: String,
    val progress: Float,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTintColor: Color
)

@Composable
fun HomeScreen(
    identityStore: WealthIdentityStore,
    onNavigateGoals: () -> Unit,
    onNavigateManifestations: () -> Unit,
    onNavigateAffirmations: () -> Unit,
    onNavigateFutureSelf: () -> Unit,
    onNavigateIAm: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigateOath: () -> Unit,
    onNavigateHypnagogic: () -> Unit = {},
    onNavigateHypnagogicSession: () -> Unit = {},
    onNavigateManifestationCourse: () -> Unit = {},
    onNavigateManifestationJourney: () -> Unit = {},
    onNavigateAffirmationKnowledge: () -> Unit = {},
    onNavigateGratitude: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var activeKnowledgeLesson by remember { mutableStateOf<KnowledgeLesson?>(null) }
    var showGratitudeActionDialog by remember { mutableStateOf(false) }

    val isOathSealed by identityStore.isOathSealed.collectAsState()
    val personalOaths by identityStore.personalOaths.collectAsState()
    val totalOathCount = 4 + personalOaths.size

    val hypnagogicProgress by identityStore.hypnagogicProgress.collectAsState()
    val manifestationProgress by identityStore.manifestationProgress.collectAsState()
    val affirmationProgress by identityStore.affirmationKnowledgeProgress.collectAsState()
    val wealthActions by identityStore.wealthActions.collectAsState()

    val knowledgeLessons = remember(hypnagogicProgress, manifestationProgress, affirmationProgress) {
        listOf(
            KnowledgeLesson(
                id = "affirmations",
                title = "Affirmations",
                subtitle = "Lesson 1 · Neural Rewiring & Frequency",
                progress = affirmationProgress,
                icon = Icons.Default.Favorite,
                iconBgColor = Color(0xFFFFEBEE),
                iconTintColor = Color(0xFFE91E63)
            ),
            KnowledgeLesson(
                id = "hypnagogic",
                title = "Hypnagogic Nap",
                subtitle = "Lesson 2 · 10-Step Subconscious Entry",
                progress = hypnagogicProgress,
                icon = Icons.Default.NightsStay,
                iconBgColor = Color(0xFFEDE7F6),
                iconTintColor = Color(0xFF673AB7)
            ),
            KnowledgeLesson(
                id = "gratitude",
                title = "Gratitude",
                subtitle = "Lesson 3 · Abundance Frequency Shift",
                progress = 0.70f,
                icon = Icons.Default.LocalFlorist,
                iconBgColor = Color(0xFFE8F5E9),
                iconTintColor = Color(0xFF4CAF50)
            ),
            KnowledgeLesson(
                id = "manifestations",
                title = "Manifestation",
                subtitle = "Lesson 1 · Understanding Intention",
                progress = manifestationProgress,
                icon = Icons.Default.AutoAwesome,
                iconBgColor = Color(0xFFFFF3E0),
                iconTintColor = Color(0xFFFF9800)
            )
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Full screen exact background image
        Image(
            painter = painterResource(id = R.drawable.bg_home_soft_pink_1786884246670),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // 1. Top Header
                HomeTopHeader()
            }

            item {
                // 2. 8 Sacred Core Features Grid (Goals, Manifestation, Affirmations, Future Self, I AM, Favorite, The Oath, Gratitude)
                SacredFeaturesGrid(
                    onGoalsClick = onNavigateGoals,
                    onManifestationClick = onNavigateManifestations,
                    onAffirmationsClick = onNavigateAffirmations,
                    onFutureSelfClick = onNavigateFutureSelf,
                    onIAmClick = onNavigateIAm,
                    onFavoriteClick = onNavigateFavorites,
                    onOathClick = onNavigateOath,
                    onGratitudeClick = onNavigateGratitude
                )
            }

            item {
                // 3. The Oath Status Card (Promised Home Screen Feature)
                TheOathHomeCard(
                    isSealed = isOathSealed,
                    promiseCount = totalOathCount,
                    onClick = onNavigateOath
                )
            }

            item {
                // 4. Segmented Tabs (Knowledge vs Action)
                KnowledgeActionTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            if (selectedTab == 0) {
                items(knowledgeLessons) { lesson ->
                    KnowledgeLessonCard(
                        lesson = lesson,
                        onClick = {
                            when (lesson.id) {
                                "hypnagogic" -> onNavigateHypnagogic()
                                "manifestations" -> onNavigateManifestationCourse()
                                "affirmations" -> onNavigateAffirmationKnowledge()
                                else -> activeKnowledgeLesson = lesson
                            }
                        }
                    )
                }
            } else {
                items(wealthActions) { action ->
                    WealthActionCard(
                        action = action,
                        onToggle = {
                            identityStore.toggleWealthAction(action.id)
                        },
                        onExecuteAction = {
                            when (action.id) {
                                "action_hypnagogic" -> onNavigateHypnagogicSession()
                                "action_affirmations" -> onNavigateAffirmations()
                                "action_gratitude" -> onNavigateGratitude()
                                "action_manifestations" -> onNavigateManifestations()
                                else -> identityStore.toggleWealthAction(action.id)
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Modal Knowledge Lesson with 'You are going to Action now' Hand-off
    activeKnowledgeLesson?.let { lesson ->
        KnowledgeLessonDialog(
            lesson = lesson,
            onDismiss = { activeKnowledgeLesson = null },
            onStartAction = {
                activeKnowledgeLesson = null
                when (lesson.id) {
                    "affirmations" -> onNavigateAffirmations()
                    "gratitude" -> showGratitudeActionDialog = true
                    "manifestations" -> onNavigateManifestations()
                }
            }
        )
    }

    // Interactive 3-Blessing Gratitude Action Dialog
    if (showGratitudeActionDialog) {
        GratitudeActionDialog(
            onDismiss = { showGratitudeActionDialog = false },
            onComplete = { _, _, _ ->
                identityStore.toggleWealthAction("action_gratitude")
                showGratitudeActionDialog = false
            }
        )
    }
}

@Composable
fun HomeTopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome Back,",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF756F84)
            )
            Text(
                text = "Fiym · Manifest Your Dreams",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2026)
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD54F))
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.user_avatar_1786674983505),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * 7 Feature Buttons with the exact custom uploaded icon images and perfect sizing.
 */
@Composable
fun SacredFeaturesGrid(
    onGoalsClick: () -> Unit,
    onManifestationClick: () -> Unit,
    onAffirmationsClick: () -> Unit,
    onFutureSelfClick: () -> Unit,
    onIAmClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onOathClick: () -> Unit,
    onGratitudeClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: 4 items (Goals, Manifestation, Affirmations, Future Self)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomFeatureItem(
                title = "Goals",
                imageRes = R.drawable.ic_feat_goals_1786842414240,
                onClick = onGoalsClick,
                modifier = Modifier.weight(1f)
            )
            CustomFeatureItem(
                title = "Manifestation",
                imageRes = R.drawable.ic_feat_manifest_1786842424554,
                onClick = onManifestationClick,
                modifier = Modifier.weight(1f)
            )
            CustomFeatureItem(
                title = "Affirmations",
                imageRes = R.drawable.ic_feat_affirm_1786842436594,
                onClick = onNavigateAffirmationsClick(onAffirmationsClick),
                modifier = Modifier.weight(1f)
            )
            CustomFeatureItem(
                title = "Future Self",
                imageRes = R.drawable.ic_feat_future_1786842449844,
                onClick = onFutureSelfClick,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: 4 items (I AM, Favorite, The Oath, Gratitude)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomFeatureItem(
                title = "I AM",
                imageRes = R.drawable.ic_feat_iam_1786842467598,
                onClick = onIAmClick,
                modifier = Modifier.weight(1f)
            )
            CustomFeatureItem(
                title = "Favorite",
                imageRes = R.drawable.ic_feat_favorite_1786842481035,
                onClick = onFavoriteClick,
                modifier = Modifier.weight(1f)
            )
            CustomFeatureItem(
                title = "The Oath",
                imageRes = R.drawable.ic_feat_oath_1786842491879,
                onClick = onOathClick,
                modifier = Modifier.weight(1f)
            )
            CustomFeatureItem(
                title = "Gratitude",
                imageRes = R.drawable.feat_gratitude_icon_1786896132699,
                onClick = onGratitudeClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun onNavigateAffirmationsClick(action: () -> Unit): () -> Unit = action

@Composable
fun CustomFeatureItem(
    title: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.88f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("feature_btn_$title"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3142),
                maxLines = 1,
                letterSpacing = (-0.2).sp
            )
        }
    }
}

/**
 * The Oath Home Banner Card as requested.
 */
@Composable
fun TheOathHomeCard(
    isSealed: Boolean,
    promiseCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("home_oath_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSealed) Color(0xFF1E1728) else Color(0xFF2A2038)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_feat_oath_1786842491879),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isSealed) "THE OATH — SEALED" else "THE OATH — PENDING",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = if (isSealed) Color(0xFFFFD54F) else Color(0xFFE1BEE7)
                        )
                        if (isSealed) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isSealed) {
                            "You made $promiseCount sacred promises to yourself."
                        } else {
                            "Declare your promises to the person you are becoming."
                        },
                        fontSize = 12.sp,
                        color = Color(0xFFD1C8E8)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isSealed) "View Oath" else "Seal Now",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCE93D8)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFCE93D8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun KnowledgeActionTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = Color(0xCCFFFFFF),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.5.dp
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF1E2026),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .clip(RoundedCornerShape(6.dp)),
                    height = 3.dp,
                    color = Color(0xFF673AB7)
                )
            },
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                text = {
                    Text(
                        text = "Knowledge",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 0) Color(0xFF1E2026) else Color(0xFF756F84),
                        fontSize = 14.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                text = {
                    Text(
                        text = "Action",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 1) Color(0xFF1E2026) else Color(0xFF756F84),
                        fontSize = 14.sp
                    )
                }
            )
        }
    }
}

@Composable
fun KnowledgeLessonCard(
    lesson: KnowledgeLesson,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("knowledge_lesson_${lesson.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(lesson.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = lesson.icon,
                    contentDescription = lesson.title,
                    tint = lesson.iconTintColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E2026)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = lesson.subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF756F84)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { lesson.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = lesson.iconTintColor,
                    trackColor = lesson.iconBgColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(36.dp)
            ) {
                CircularProgressIndicator(
                    progress = { lesson.progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 3.dp,
                    color = lesson.iconTintColor,
                    trackColor = lesson.iconBgColor
                )
                Text(
                    text = "${(lesson.progress * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2026)
                )
            }
        }
    }
}

@Composable
fun WealthActionCard(
    action: WealthActionItem,
    onToggle: () -> Unit,
    onExecuteAction: () -> Unit = onToggle
) {
    val isCoreAction = action.id.startsWith("action_")
    val iconInfo = when (action.id) {
        "action_hypnagogic" -> Triple(Icons.Default.NightsStay, Color(0xFFEDE7F6), Color(0xFF673AB7))
        "action_affirmations" -> Triple(Icons.Default.Favorite, Color(0xFFFFEBEE), Color(0xFFE91E63))
        "action_gratitude" -> Triple(Icons.Default.LocalFlorist, Color(0xFFE8F5E9), Color(0xFF4CAF50))
        "action_manifestations" -> Triple(Icons.Default.AutoAwesome, Color(0xFFFFF3E0), Color(0xFFFF9800))
        else -> Triple(Icons.Default.Stars, Color(0xFFEFF6FF), Color(0xFF3B82F6))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onExecuteAction() }
            .testTag("action_item_${action.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (action.isCompleted) Color(0xFFF0FDF4) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (action.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle completed",
                    tint = if (action.isCompleted) Color(0xFF22C55E) else Color(0xFF9CA3AF),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconInfo.second),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconInfo.first,
                    contentDescription = null,
                    tint = iconInfo.third,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.5.sp,
                    color = if (action.isCompleted) Color(0xFF15803D) else Color(0xFF1E2026)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = action.subtitle,
                    fontSize = 11.5.sp,
                    color = Color(0xFF756F84),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (action.isCompleted) Color(0xFFDCFCE7) else Color(0xFFF3F4F6)
                ) {
                    Text(
                        text = action.duration,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (action.isCompleted) Color(0xFF16A34A) else Color(0xFF6B7280),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                if (isCoreAction && !action.isCompleted) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = iconInfo.second,
                        modifier = Modifier.clickable { onExecuteAction() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = iconInfo.third,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "START",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = iconInfo.third
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Knowledge Lesson Dialog with rich teachings and the 'You are going to Action now' bridge.
 */
@Composable
fun KnowledgeLessonDialog(
    lesson: KnowledgeLesson,
    onDismiss: () -> Unit,
    onStartAction: () -> Unit
) {
    val details = when (lesson.id) {
        "affirmations" -> KnowledgeDetails(
            stage = "LESSON 1 · SCIENCE OF REPETITION",
            headline = "Affirmations & Neural Subconscious Rewiring",
            body1 = "Affirmations are not wishful thinking; they are targeted linguistic commands that stimulate neuroplasticity in the prefrontal cortex.",
            body2 = "When you chant 'I AM' statements with physiological certainty, your reticular activating system (RAS) filters the outer world to notice wealth opportunities aligned with your stated frequency.",
            takeaway1 = "Repetition weakens old scarcity neural grooves.",
            takeaway2 = "Speaking in the present tense signals certainty to the subconscious.",
            actionPrompt = "Start Affirmation Chanting Practice (5 min)"
        )
        "gratitude" -> KnowledgeDetails(
            stage = "LESSON 3 · ABUNDANCE FREQUENCY",
            headline = "Gratitude: The Geometry of Receiving",
            body1 = "Scarcity signals to the universe that you lack, which creates anxiety and poor decision-making. Gratitude signals that you are already abundantly supplied.",
            body2 = "Anchoring gratitude every day alters heart-rate variability (HRV) and synchronizes your emotional state to attract high-value collaborations and resources effortlessly.",
            takeaway1 = "Acknowledging 3 blessings daily expands dopamine and serotonin baseline.",
            takeaway2 = "Gratitude for small gains opens the conduit for massive scale.",
            actionPrompt = "Start Daily Gratitude Journaling (5 min)"
        )
        "manifestations" -> KnowledgeDetails(
            stage = "LESSON 4 · SUBTLE RESIDUE",
            headline = "Manifestation: Feeling the Wish Fulfilled",
            body1 = "The subconscious mind cannot distinguish between a vividly imagined experience and a physical reality.",
            body2 = "When you mentally immerse yourself into the exact sensory details (sounds, sights, emotional freedom) of your vision, your nervous system adopts the identity immediately.",
            takeaway1 = "3D sensory richness creates emotional resonance.",
            takeaway2 = "Release the desperate attachment to timing; assume victory.",
            actionPrompt = "Start Manifestation Visualizer (10 min)"
        )
        else -> KnowledgeDetails(
            stage = "SACRED KNOWLEDGE",
            headline = lesson.title,
            body1 = lesson.subtitle,
            body2 = "Knowledge prepares the mind, but consistent deliberate action shapes destiny.",
            takeaway1 = "Learn the principle deeply.",
            takeaway2 = "Embody the practice daily.",
            actionPrompt = "Start Action Now"
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = lesson.iconBgColor
                    ) {
                        Text(
                            text = details.stage,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = lesson.iconTintColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            letterSpacing = 0.5.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF6B7280))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = details.headline,
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1926),
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = details.body1,
                    fontSize = 14.5.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF4B5563)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = details.body2,
                    fontSize = 14.5.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF4B5563)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = lesson.iconBgColor.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "CORE PILLARS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = lesson.iconTintColor,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "• ${details.takeaway1}", fontSize = 13.sp, color = Color(0xFF374151))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "• ${details.takeaway2}", fontSize = 13.sp, color = Color(0xFF374151))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // The 'You are going to Action now' Bridge Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF8FE)),
                    border = BorderStroke(1.dp, Color(0xFFEDE5F5))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF3E8FF),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You are going to Action now.",
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1926)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Knowledge provides the map, but practicing creates the physical transformation.",
                            fontSize = 12.5.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onStartAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = lesson.iconTintColor,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = details.actionPrompt, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp),
                            shape = RoundedCornerShape(21.dp),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF6B7280))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Take Action Later in Action Tab", fontSize = 12.5.sp, color = Color(0xFF4B5563))
                        }
                    }
                }
            }
        }
    }
}

private data class KnowledgeDetails(
    val stage: String,
    val headline: String,
    val body1: String,
    val body2: String,
    val takeaway1: String,
    val takeaway2: String,
    val actionPrompt: String
)

/**
 * Interactive Gratitude Alignment Action Dialog
 */
@Composable
fun GratitudeActionDialog(
    onDismiss: () -> Unit,
    onComplete: (String, String, String) -> Unit
) {
    var blessing1 by remember { mutableStateOf("") }
    var blessing2 by remember { mutableStateOf("") }
    var blessing3 by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = "ACTION STAGE · 5 MIN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF6B7280))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Daily Gratitude Alignment",
                    fontFamily = FontFamily.Serif,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1926)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Acknowledge 3 forms of abundance received today to anchor your frequency in wealth.",
                    fontSize = 13.5.sp,
                    color = Color(0xFF554D5E),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(text = "1. First Abundance Blessing", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = blessing1,
                    onValueChange = { blessing1 = it },
                    placeholder = { Text("e.g., An unexpected business conversation or clear energy", fontSize = 12.5.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "2. Second Abundance Blessing", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = blessing2,
                    onValueChange = { blessing2 = it },
                    placeholder = { Text("e.g., Health, safety, and nourishment for my family", fontSize = 12.5.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "3. Third Abundance Blessing", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = blessing3,
                    onValueChange = { blessing3 = it },
                    placeholder = { Text("e.g., Faith in my future self and expanding opportunities", fontSize = 12.5.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onComplete(blessing1, blessing2, blessing3)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("seal_gratitude_action_btn"),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SEAL GRATITUDE & COMPLETE ACTION",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
