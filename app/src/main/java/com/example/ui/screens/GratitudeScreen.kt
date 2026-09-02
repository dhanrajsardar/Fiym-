package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.GratitudeEntry
import com.example.data.local.WealthIdentityStore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GratitudeScreen(
    identityStore: WealthIdentityStore,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Write Journal, 1: Abundance Vault, 2: Science & Insights

    val gratitudeEntries by identityStore.gratitudeEntries.collectAsState()
    val gratitudeStreak by identityStore.gratitudeStreak.collectAsState()

    var showPromptSheet by remember { mutableStateOf(false) }
    var showSealedCelebration by remember { mutableStateOf(false) }

    // Form states
    var item1Text by remember { mutableStateOf("") }
    var item2Text by remember { mutableStateOf("") }
    var item3Text by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Wealth & Money") }
    var selectedMoodEmoji by remember { mutableStateOf("✨") }
    var selectedMoodLabel by remember { mutableStateOf("Abundant") }
    var reflectionText by remember { mutableStateOf("") }
    var autoAddToActionTab by remember { mutableStateOf(true) }

    val categories = listOf(
        "Wealth & Money",
        "Daily Life",
        "Health & Energy",
        "Relationships",
        "Mindset & Growth",
        "Future Abundance"
    )

    val moodOptions = listOf(
        "✨" to "Abundant",
        "🌿" to "Peaceful",
        "🙏" to "Grateful",
        "☀️" to "Joyful",
        "🕊️" to "Free",
        "💎" to "Unstoppable"
    )

    val prompts = listOf(
        "What unexpected financial or personal blessing came your way recently?",
        "What simple luxury or comfort did you enjoy today that you once wished for?",
        "Who supported, inspired, or challenged you to grow today?",
        "What piece of wisdom or resilience did a difficult challenge teach you?",
        "What future reality are you already deeply thankful for as if it has happened?"
    )

    fun handleSaveGratitude() {
        if (item1Text.isBlank()) {
            Toast.makeText(context, "Please write at least 1 abundance point!", Toast.LENGTH_SHORT).show()
            return
        }

        val todayFormatted = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
        val newEntry = GratitudeEntry(
            id = "gratitude_${System.currentTimeMillis()}",
            item1 = item1Text.trim(),
            item2 = item2Text.trim(),
            item3 = item3Text.trim(),
            category = selectedCategory,
            moodEmoji = selectedMoodEmoji,
            moodLabel = selectedMoodLabel,
            reflection = reflectionText.trim(),
            dateStr = todayFormatted,
            timestamp = System.currentTimeMillis()
        )

        identityStore.saveGratitudeEntry(newEntry)

        if (autoAddToActionTab) {
            identityStore.addWealthAction(
                title = "Gratitude Frequency Maintenance",
                subtitle = "Anchor feeling of: ${newEntry.item1.take(35)}...",
                duration = "5 min"
            )
        }

        showSealedCelebration = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalFlorist,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Gratitude & Abundance",
                                fontFamily = FontFamily.Serif,
                                fontSize = 17.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1926)
                            )
                            Text(
                                text = "Conscious Frequency Anchoring",
                                fontSize = 11.5.sp,
                                color = Color(0xFF756F84)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("gratitude_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E1926)
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFF8E1),
                        border = BorderStroke(1.dp, Color(0xFFFFE082)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(text = "🔥", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$gratitudeStreak Days",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF8FE))
            )
        },
        containerColor = Color(0xFFFBF8FE)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Segmented Top Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF2E7D32),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF2E7D32),
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Write Journal",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Abundance Vault",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                            if (gratitudeEntries.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFE8F5E9),
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${gratitudeEntries.size}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "Insights",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> GratitudeComposerTab(
                        item1 = item1Text,
                        item2 = item2Text,
                        item3 = item3Text,
                        reflection = reflectionText,
                        selectedCategory = selectedCategory,
                        selectedMoodEmoji = selectedMoodEmoji,
                        selectedMoodLabel = selectedMoodLabel,
                        categories = categories,
                        moodOptions = moodOptions,
                        autoAddToActionTab = autoAddToActionTab,
                        onItem1Change = { item1Text = it },
                        onItem2Change = { item2Text = it },
                        onItem3Change = { item3Text = it },
                        onReflectionChange = { reflectionText = it },
                        onCategorySelect = { selectedCategory = it },
                        onMoodSelect = { emoji, label ->
                            selectedMoodEmoji = emoji
                            selectedMoodLabel = label
                        },
                        onToggleAutoAction = { autoAddToActionTab = it },
                        onOpenPrompts = { showPromptSheet = true },
                        onSave = { handleSaveGratitude() }
                    )
                    1 -> GratitudeVaultTab(
                        entries = gratitudeEntries,
                        onDeleteEntry = { id ->
                            identityStore.deleteGratitudeEntry(id)
                            Toast.makeText(context, "Entry removed from vault", Toast.LENGTH_SHORT).show()
                        },
                        onStartNew = { selectedTab = 0 }
                    )
                    2 -> GratitudeInsightsTab(
                        onWriteNow = { selectedTab = 0 }
                    )
                }

                // Celebration Overlay Dialog
                if (showSealedCelebration) {
                    GratitudeSealedDialog(
                        moodEmoji = selectedMoodEmoji,
                        item1 = item1Text,
                        onDismiss = {
                            showSealedCelebration = false
                            item1Text = ""
                            item2Text = ""
                            item3Text = ""
                            reflectionText = ""
                            selectedTab = 1 // Navigate to vault
                        }
                    )
                }
            }
        }
    }

    // Prompts Bottom Sheet
    if (showPromptSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPromptSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Abundance Spark Prompts",
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1926)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                prompts.forEach { prompt ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable {
                                if (item1Text.isBlank()) {
                                    item1Text = ""
                                }
                                reflectionText = "Prompt reflection: $prompt\n"
                                showPromptSheet = false
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBE7)),
                        border = BorderStroke(1.dp, Color(0xFFDCEDC8))
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 13.sp,
                            color = Color(0xFF33691E),
                            modifier = Modifier.padding(14.dp),
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 0: GRATITUDE COMPOSER
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GratitudeComposerTab(
    item1: String,
    item2: String,
    item3: String,
    reflection: String,
    selectedCategory: String,
    selectedMoodEmoji: String,
    selectedMoodLabel: String,
    categories: List<String>,
    moodOptions: List<Pair<String, String>>,
    autoAddToActionTab: Boolean,
    onItem1Change: (String) -> Unit,
    onItem2Change: (String) -> Unit,
    onItem3Change: (String) -> Unit,
    onReflectionChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onMoodSelect: (String, String) -> Unit,
    onToggleAutoAction: (Boolean) -> Unit,
    onOpenPrompts: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Hero Card with Serene Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3B2B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.gratitude_hero_banner_1786896147400),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    contentScale = ContentScale.Crop,
                    alpha = 0.55f
                )

                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = "SACRED FREQUENCY",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC8E6C9),
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = onOpenPrompts,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Prompts",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "“What you appreciate, appreciates.”",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Shift your Reticular Activating System from lack to effortless abundance.",
                        fontSize = 12.sp,
                        color = Color(0xFFE0E0E0),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mood / Emotional Resonance Picker
        Text(
            text = "HOW ARE YOU FEELING RIGHT NOW?",
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
            moodOptions.forEach { (emoji, label) ->
                val isSelected = selectedMoodEmoji == emoji
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) Color(0xFF2E7D32) else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF2E7D32) else Color(0xFFE0E0E0)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onMoodSelect(emoji, label) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(text = emoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFF616161),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Category Chips
        Text(
            text = "ABUNDANCE PILLAR",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF756F84),
            letterSpacing = 0.6.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Color(0xFFE8F5E9) else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF2E7D32) else Color(0xFFE0E0E0)),
                    modifier = Modifier.clickable { onCategorySelect(cat) }
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF1B5E20) else Color(0xFF424242),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // 3 Points of Abundance
        Text(
            text = "3 SACRED POINTS OF ABUNDANCE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20),
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        GratitudePointInput(
            index = "1",
            value = item1,
            onValueChange = onItem1Change,
            placeholder = "e.g. Unexpected revenue, an inspiring breakthrough, or loving support...",
            isRequired = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        GratitudePointInput(
            index = "2",
            value = item2,
            onValueChange = onItem2Change,
            placeholder = "e.g. Quiet morning coffee, good health, peaceful headspace...",
            isRequired = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        GratitudePointInput(
            index = "3",
            value = item3,
            onValueChange = onItem3Change,
            placeholder = "e.g. A future victory that feels already achieved in consciousness...",
            isRequired = false
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Deep Reflection Box
        Text(
            text = "DEEPEN THE FREQUENCY (OPTIONAL)",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF756F84),
            letterSpacing = 0.6.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = reflection,
            onValueChange = onReflectionChange,
            placeholder = { Text("Why do these blessings matter? What standard will you hold today?", fontSize = 13.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),
                unfocusedBorderColor = Color(0xFFC8E6C9),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action Tab Synchronization Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE8F5E9))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleAutoAction(!autoAddToActionTab) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (autoAddToActionTab) Color(0xFF2E7D32) else Color(0xFFEEEEEE),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (autoAddToActionTab) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Add to Action Tab Checklist",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1926)
                    )
                    Text(
                        text = "Automatically create a daily reminder to sustain this frequency.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF756F84)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("save_gratitude_btn"),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
        ) {
            Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SEAL & ANCHOR GRATITUDE",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun GratitudePointInput(
    index: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isRequired: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (value.isNotBlank()) Color(0xFF81C784) else Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (value.isNotBlank()) Color(0xFF2E7D32) else Color(0xFFE8F5E9),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = index,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (value.isNotBlank()) Color.White else Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, fontSize = 12.5.sp, color = Color(0xFF9E9E9E)) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                maxLines = 2
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: ABUNDANCE VAULT (Past Gratitude History)
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GratitudeVaultTab(
    entries: List<GratitudeEntry>,
    onDeleteEntry: (String) -> Unit,
    onStartNew: () -> Unit
) {
    var filterCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Wealth & Money", "Daily Life", "Health & Energy", "Relationships", "Mindset & Growth")

    val filtered = if (filterCategory == "All") entries else entries.filter { it.category == filterCategory }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Filter Row
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = filterCategory == cat
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) Color(0xFF2E7D32) else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF2E7D32) else Color(0xFFE0E0E0)),
                    modifier = Modifier.clickable { filterCategory = cat }
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color(0xFF424242),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.LocalFlorist, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Vault is Awaiting Abundance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1926)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Anchor your first 3 points of gratitude to lock in your daily streak.",
                        fontSize = 13.sp,
                        color = Color(0xFF756F84),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onStartNew,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Write Today's Entry", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.id }) { entry ->
                    GratitudeVaultCard(
                        entry = entry,
                        onDelete = { onDeleteEntry(entry.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun GratitudeVaultCard(
    entry: GratitudeEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, Color(0xFFE8F5E9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = "${entry.moodEmoji} ${entry.moodLabel}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(
                            text = entry.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF616161),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.dateStr,
                        fontSize = 11.5.sp,
                        color = Color(0xFF9E9E9E),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFBDBDBD), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Points List
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                VaultPointRow(number = "1", text = entry.item1)
                if (entry.item2.isNotBlank()) {
                    VaultPointRow(number = "2", text = entry.item2)
                }
                if (entry.item3.isNotBlank()) {
                    VaultPointRow(number = "3", text = entry.item3)
                }
            }

            // Reflection snippet if present
            if (entry.reflection.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F8E9), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "“${entry.reflection}”",
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.5.sp,
                    color = Color(0xFF558B2F),
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun VaultPointRow(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFC8E6C9),
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF212121),
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 2: SCIENCE & INSIGHTS
// -------------------------------------------------------------------------------------------------
@Composable
fun GratitudeInsightsTab(onWriteNow: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3B2B))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "THE NEUROSCIENCE OF ABUNDANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF81C784),
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Why Gratitude Changes Reality",
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "When you intentionally focus on what is already abundant, your brain releases dopamine and serotonin while calming the amygdala's scarcity alarm.",
                    fontSize = 13.sp,
                    color = Color(0xFFD1E7DD),
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        InsightItem(
            icon = Icons.Default.Psychology,
            title = "1. Reticular Activating System (RAS)",
            desc = "Your brain filters millions of sensory inputs per second. If you look for lack, you find lack. When you actively scan for abundance, your RAS highlights untapped opportunities."
        )

        Spacer(modifier = Modifier.height(10.dp))

        InsightItem(
            icon = Icons.Rounded.Diamond,
            title = "2. Frequency Coherence",
            desc = "Scarcity attracts scarcity because decisions are made in fear. Gratitude puts your nervous system into a relaxed parasympathetic state, enabling clear, high-leverage execution."
        )

        Spacer(modifier = Modifier.height(10.dp))

        InsightItem(
            icon = Icons.Default.AutoAwesome,
            title = "3. Pre-Gratitude for Future Reality",
            desc = "Feeling gratitude for a future manifestation BEFORE it physically arrives synchronizes your identity with the reality of having already accomplished it."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onWriteNow,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
            Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Write Gratitude Today", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InsightItem(icon: ImageVector, title: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE8F5E9))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1926))
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = desc, fontSize = 12.sp, color = Color(0xFF616161), lineHeight = 17.sp)
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// CELEBRATION DIALOG
// -------------------------------------------------------------------------------------------------
@Composable
fun GratitudeSealedDialog(
    moodEmoji: String,
    item1: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.size(70.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = moodEmoji, fontSize = 34.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Abundance Sealed!",
                    fontFamily = FontFamily.Serif,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your nervous system is now calibrated to abundance frequency.",
                    fontSize = 13.sp,
                    color = Color(0xFF616161),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F8E9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "“${item1.take(80)}${if (item1.length > 80) "..." else ""}”",
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(23.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("VIEW IN VAULT", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
