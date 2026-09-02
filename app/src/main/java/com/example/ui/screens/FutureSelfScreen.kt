package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.WealthIdentityStore

data class GuidanceChip(
    val label: String,
    val starter: String,
    val promptQuestion: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutureSelfScreen(
    identityStore: WealthIdentityStore,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savedText by identityStore.futureSelfText.collectAsState()
    var textContent by remember(savedText) { mutableStateOf(savedText) }
    var isReadingMode by remember { mutableStateOf(false) }
    var activePromptGuide by remember { mutableStateOf<String?>(null) }

    val isFavorited = identityStore.isFavorite(textContent)

    val guidanceChips = remember {
        listOf(
            GuidanceChip(
                "Who I am",
                "\n\nWho I am: I am a grounded, sovereign individual who radiates quiet confidence and inner power.",
                "Imagine your identity: What core values and character define the person you have become?"
            ),
            GuidanceChip(
                "How I think",
                "\n\nHow I think: I think from abundance and long-term vision. I filter noise and focus strictly on high leverage.",
                "How do you process information, make calm decisions, and eliminate doubt?"
            ),
            GuidanceChip(
                "How I act",
                "\n\nHow I act: I execute with precision and discipline. I do what needs to be done without procrastination.",
                "What are your daily habits, body language, and daily actions?"
            ),
            GuidanceChip(
                "My standards",
                "\n\nMy standards: I refuse low-standard habits, toxic environments, and mediocre effort.",
                "What boundaries and non-negotiable rules govern your life?"
            ),
            GuidanceChip(
                "My life",
                "\n\nMy life: My daily life is peaceful, organized, rich in deep relationships and purposeful freedom.",
                "Describe your living environment, mornings, evenings, and lifestyle freedom."
            ),
            GuidanceChip(
                "My wealth",
                "\n\nMy wealth: I am financially sovereign with multiple growing cash flow streams and generational assets.",
                "What is your net worth, income streams, and relationship with money?"
            ),
            GuidanceChip(
                "My work",
                "\n\nMy work: I build enduring value that elevates thousands of lives while giving me absolute autonomy.",
                "What business, craft, or empire are you leading daily?"
            ),
            GuidanceChip(
                "Challenges",
                "\n\nHow I handle challenges: I view friction as fuel. When storms arrive, I remain composed and find the highest solution.",
                "When severe obstacles arise, how do you calmly confront and overcome them?"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_feat_future_1786842449844),
                            contentDescription = "Future Self Icon",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "My Future Self",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF1E2026)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E2026)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isReadingMode = !isReadingMode }
                    ) {
                        Icon(
                            imageVector = if (isReadingMode) Icons.Default.Edit else Icons.Default.Visibility,
                            contentDescription = if (isReadingMode) "Edit Mode" else "Read Mode",
                            tint = Color(0xFF7E57C2)
                        )
                    }
                    IconButton(
                        onClick = {
                            identityStore.toggleFavorite("My Future Self Identity", textContent, "Future Self")
                            Toast.makeText(
                                context,
                                if (!isFavorited) "Saved to Favorites" else "Removed from Favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorited) Color(0xFFE91E63) else Color(0xFF756F84)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD6EAF8),
                        Color(0xFFEAF3FA),
                        Color(0xFFFDE4EE),
                        Color(0xFFFFF6F1),
                        Color(0xFFFFF9F5)
                    )
                )
            )
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isReadingMode) {
                // Header Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1728)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Describe the person you are becoming.",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 23.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Be as specific as possible. Imagine you already have the life you want. Who are you? How do you think, feel, act, live, work, and handle challenges? What do you have?",
                                fontSize = 12.5.sp,
                                color = Color(0xFFD1C8E8),
                                lineHeight = 17.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_feat_future_1786842449844),
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                // Guidance Chips Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Guidance Sparks (Tap to inspire & add):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5E5470)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        guidanceChips.forEach { chip ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    activePromptGuide = chip.promptQuestion
                                    if (!textContent.contains(chip.label, ignoreCase = true)) {
                                        textContent = (textContent.trimEnd() + chip.starter).trimStart()
                                    }
                                    Toast.makeText(context, "Added '${chip.label}' section", Toast.LENGTH_SHORT).show()
                                },
                                label = {
                                    Text(
                                        text = chip.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF4A3B69)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = false,
                                    borderColor = Color(0xFFDDD4EC),
                                    borderWidth = 1.dp
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Dynamic Guide Prompt Toast Box
                    AnimatedVisibility(visible = activePromptGuide != null) {
                        activePromptGuide?.let { prompt ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { activePromptGuide = null },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = Color(0xFF673AB7),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = prompt,
                                        fontSize = 12.sp,
                                        color = Color(0xFF4A148C),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Dismiss",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7E57C2)
                                    )
                                }
                            }
                        }
                    }
                }

                // Large Continuous Writing Area
                OutlinedTextField(
                    value = textContent,
                    onValueChange = { textContent = it },
                    placeholder = {
                        Text(
                            text = "I am financially free. I run a successful business that creates real value. I wake up with clarity and discipline. I handle problems calmly instead of avoiding them. I...",
                            fontSize = 14.5.sp,
                            lineHeight = 22.sp,
                            color = Color(0xFF9E9E9E),
                            fontStyle = FontStyle.Italic
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .testTag("future_self_editor"),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF7E57C2),
                        unfocusedBorderColor = Color(0xFFE2DCED),
                        focusedTextColor = Color(0xFF1E2026),
                        unfocusedTextColor = Color(0xFF1E2026)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Normal
                    )
                )

                // Save Button
                Button(
                    onClick = {
                        identityStore.saveFutureSelfText(textContent)
                        Toast.makeText(context, "Future Self Vision Saved Successfully!", Toast.LENGTH_SHORT).show()
                        isReadingMode = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_save_future_self"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF673AB7)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save My Future Self",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                // Reading & Reflection Sanctuary View
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE0D8F0), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF673AB7))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "MY FUTURE IDENTITY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = Color(0xFF673AB7)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF3E5F5)
                            ) {
                                Text(
                                    text = "Active Blueprint",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF8E24AA),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = textContent.ifBlank { "You have not written your Future Self vision yet. Tap 'Edit' above to write it." },
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            color = Color(0xFF212121),
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { isReadingMode = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEDE7F6)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color(0xFF673AB7),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Edit Vision",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF673AB7)
                                )
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Embodying Future Self Consciousness...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF673AB7)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Embody Now",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
