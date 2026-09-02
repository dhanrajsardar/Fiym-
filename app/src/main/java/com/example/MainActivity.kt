package com.example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.WealthIdentityStore
import com.example.service.ScreenRecordingService
import com.example.ui.BroViewModel
import com.example.ui.components.InAppFloatingOrb
import com.example.ui.screens.AffirmationKnowledgeScreen
import com.example.ui.screens.AffirmationKnowledgeViewMode
import com.example.ui.screens.AffirmationScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CreatePlaylistScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.FutureSelfScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.GratitudeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HypnagogicOnboardingScreen
import com.example.ui.screens.HypnagogicPhase
import com.example.ui.screens.IAmScreen
import com.example.ui.screens.ManifestationJourneyScreen
import com.example.ui.screens.ManifestationPlaylistScreen
import com.example.ui.screens.ManifestationViewMode
import com.example.ui.screens.ManifestationsScreen
import com.example.ui.screens.OathScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val identityStore = remember { WealthIdentityStore(applicationContext) }

                val isServiceEnabled by viewModel.isServiceEnabled.collectAsStateWithLifecycle()
                val isLockScreenMessagesEnabled by viewModel.isLockScreenMessagesEnabled.collectAsStateWithLifecycle()
                val messageFrequencyMinutes by viewModel.messageFrequencyMinutes.collectAsStateWithLifecycle()
                val isScreenContextEnabled by viewModel.isScreenContextEnabled.collectAsStateWithLifecycle()
                val lastMotivationalMessage by viewModel.lastMotivationalMessage.collectAsStateWithLifecycle()
                val todayCheckIns by viewModel.todayCheckIns.collectAsStateWithLifecycle()
                val allMessages by viewModel.allMessages.collectAsStateWithLifecycle()
                val isThinking by viewModel.isThinking.collectAsStateWithLifecycle()
                val isScreenRecording by viewModel.isScreenRecording.collectAsStateWithLifecycle()
                val activeContext by viewModel.activeContext.collectAsStateWithLifecycle()
                val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()

                val projectionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                        val serviceIntent = Intent(this, ScreenRecordingService::class.java).apply {
                            putExtra(ScreenRecordingService.EXTRA_RESULT_CODE, result.resultCode)
                            putExtra(ScreenRecordingService.EXTRA_RESULT_DATA, result.data)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(serviceIntent)
                        } else {
                            startService(serviceIntent)
                        }
                        viewModel.updateScreenRecordingState(true)
                        Toast.makeText(this, "Screen context recording active", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Screen capture permission required", Toast.LENGTH_SHORT).show()
                    }
                }

                LaunchedEffect(intent) {
                    if (intent?.getBooleanExtra("OPEN_CHAT", false) == true) {
                        navController.navigate("chat")
                    }
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val isBottomBarVisible = currentRoute in listOf("home", "guide", "profile")

                val navigateToHome: () -> Unit = {
                    if (!navController.popBackStack("home", false)) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            AnimatedVisibility(
                                visible = isBottomBarVisible,
                                enter = slideInVertically(initialOffsetY = { it }),
                                exit = slideOutVertically(targetOffsetY = { it })
                            ) {
                                FiymBottomNavigationBar(
                                    currentRoute = currentRoute ?: "home",
                                    onNavigate = { targetRoute ->
                                        if (currentRoute != targetRoute) {
                                            navController.navigate(targetRoute) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = "home",
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // 1. Home Screen (Features: Goals, Manifestations, Affirmations, Future Self, I AM, Favorite, The Oath)
                                composable("home") {
                                    HomeScreen(
                                        identityStore = identityStore,
                                        onNavigateGoals = { navController.navigate("goals") },
                                        onNavigateManifestations = { navController.navigate("manifestation_playlist") },
                                        onNavigateAffirmations = { navController.navigate("affirmations") },
                                        onNavigateFutureSelf = { navController.navigate("future_self") },
                                        onNavigateIAm = { navController.navigate("iam") },
                                        onNavigateFavorites = { navController.navigate("favorites") },
                                        onNavigateOath = { navController.navigate("oath") },
                                        onNavigateHypnagogic = { navController.navigate("hypnagogic_onboarding") },
                                        onNavigateHypnagogicSession = { navController.navigate("hypnagogic_session") },
                                        onNavigateManifestationCourse = { navController.navigate("manifestation_course") },
                                        onNavigateManifestationJourney = { navController.navigate("manifestation_journey") },
                                        onNavigateAffirmationKnowledge = { navController.navigate("affirmation_knowledge") },
                                        onNavigateGratitude = { navController.navigate("gratitude") }
                                    )
                                }

                                // 1.0.0.2 Gratitude & Abundance Vault Screen
                                composable("gratitude") {
                                    GratitudeScreen(
                                        identityStore = identityStore,
                                        onBack = navigateToHome
                                    )
                                }

                                // 1.0.0 Affirmation Knowledge Flow (6 Screens)
                                composable("affirmation_knowledge") {
                                    AffirmationKnowledgeScreen(
                                        identityStore = identityStore,
                                        initialMode = AffirmationKnowledgeViewMode.INTERACTIVE_FLOW,
                                        onNavigateAffirmationAction = { navController.navigate("affirmations") },
                                        onNavigateIAm = { navController.navigate("iam") },
                                        onExit = navigateToHome
                                    )
                                }

                                // 1.0.0.1 Affirmation Overview Hub
                                composable("affirmation_course") {
                                    AffirmationKnowledgeScreen(
                                        identityStore = identityStore,
                                        initialMode = AffirmationKnowledgeViewMode.OVERVIEW_HUB,
                                        onNavigateAffirmationAction = { navController.navigate("affirmations") },
                                        onNavigateIAm = { navController.navigate("iam") },
                                        onExit = navigateToHome
                                    )
                                }

                                // 1.0 Hypnagogic Nap Onboarding (10 screens & hand-off)
                                composable("hypnagogic_onboarding") {
                                    HypnagogicOnboardingScreen(
                                        identityStore = identityStore,
                                        onExit = navigateToHome
                                    )
                                }

                                // 1.0.1 Direct Hypnagogic Nap Active Action Session (from Action Tab)
                                composable("hypnagogic_session") {
                                    HypnagogicOnboardingScreen(
                                        identityStore = identityStore,
                                        initialPhase = HypnagogicPhase.ACTIVE_SESSION,
                                        onExit = navigateToHome
                                    )
                                }

                                // 1.0.2 Manifestation Course (5 Lessons Hub)
                                composable("manifestation_course") {
                                    ManifestationJourneyScreen(
                                        identityStore = identityStore,
                                        initialMode = ManifestationViewMode.COURSE_HUB,
                                        onNavigateVisionBoard = { navController.navigate("manifestation_playlist") },
                                        onNavigateActionTab = { navController.navigate("home") },
                                        onExit = navigateToHome
                                    )
                                }

                                // 1.0.3 Manifestation Guided 10-Screen Journey
                                composable("manifestation_journey") {
                                    ManifestationJourneyScreen(
                                        identityStore = identityStore,
                                        initialMode = ManifestationViewMode.GUIDED_JOURNEY,
                                        onNavigateVisionBoard = { navController.navigate("manifestation_playlist") },
                                        onNavigateActionTab = { navController.navigate("home") },
                                        onExit = navigateToHome
                                    )
                                }

                                // 1.1 Future Self Screen
                                composable("future_self") {
                                    FutureSelfScreen(
                                        identityStore = identityStore,
                                        onBack = navigateToHome
                                    )
                                }

                                // 1.2 The Oath Screen
                                composable("oath") {
                                    OathScreen(
                                        identityStore = identityStore,
                                        onBack = navigateToHome
                                    )
                                }

                                // 1.3 I AM Screen
                                composable("iam") {
                                    IAmScreen(
                                        identityStore = identityStore,
                                        onBack = navigateToHome
                                    )
                                }

                                // 1.4 Favorites Screen
                                composable("favorites") {
                                    FavoritesScreen(
                                        identityStore = identityStore,
                                        onBack = navigateToHome
                                    )
                                }

                                // 2. Center Guide Screen (Fiym Wealth Consciousness & Live Orb)
                                composable("guide") {
                                    DashboardScreen(
                                        isServiceEnabled = isServiceEnabled,
                                        isScreenRecording = isScreenRecording,
                                        todayCheckIns = todayCheckIns,
                                        lastMotivationalMessage = lastMotivationalMessage,
                                        activeContext = activeContext,
                                        onToggleService = { viewModel.toggleService(it) },
                                        onRequestScreenRecording = {
                                            if (isScreenRecording) {
                                                stopService(Intent(this@MainActivity, ScreenRecordingService::class.java))
                                                viewModel.updateScreenRecordingState(false)
                                                Toast.makeText(this@MainActivity, "Screen recording stopped", Toast.LENGTH_SHORT).show()
                                            } else {
                                                val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                                                projectionLauncher.launch(projectionManager.createScreenCaptureIntent())
                                            }
                                        },
                                        onOpenChat = { navController.navigate("chat") },
                                        onOpenAffirmations = { navController.navigate("affirmations") },
                                        onOpenManifestationPlaylist = { navController.navigate("manifestation_playlist") },
                                        onCreatePlaylist = { navController.navigate("create_playlist") }
                                    )
                                }

                                // 3. Profile Screen (Matches Profile Page.png)
                                composable("profile") {
                                    ProfileScreen(
                                        onOpenSettings = { navController.navigate("settings") }
                                    )
                                }

                                // 4. Goals Screen (Matches Goal page.png)
                                composable("goals") {
                                    GoalsScreen(
                                        onBack = navigateToHome
                                    )
                                }

                                // 5. Calendar Screen (Matches calendar page.png)
                                composable("calendar") {
                                    CalendarScreen(
                                        onBack = navigateToHome
                                    )
                                }

                                // 6. Affirmations Screen (Matches affirmation.jpeg & references)
                                composable("affirmations") {
                                    AffirmationScreen(
                                        identityStore = identityStore,
                                        onBack = navigateToHome,
                                        onNavigateFavorites = { navController.navigate("favorites") },
                                        onNavigateProfile = { navController.navigate("profile") }
                                    )
                                }

                                // 7. Manifestations Screen (Matches Menifistation (1), (2), (3).jpeg)
                                composable("manifestation_playlist") {
                                    ManifestationsScreen(
                                        identityStore = identityStore,
                                        onBack = navigateToHome
                                    )
                                }

                                // 8. Create Playlist Screen (Matches add playlist page.jpeg)
                                composable("create_playlist") {
                                    CreatePlaylistScreen(
                                        onBack = {
                                            navController.navigate("manifestation_playlist") {
                                                popUpTo("manifestation_playlist") { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        },
                                        onPlaylistCreated = {
                                            Toast.makeText(this@MainActivity, "Manifestation playlist generated", Toast.LENGTH_SHORT).show()
                                            navController.navigate("manifestation_playlist") {
                                                popUpTo("manifestation_playlist") { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }

                                // 9. Chat Screen
                                composable("chat") {
                                    ChatScreen(
                                        messages = allMessages,
                                        isThinking = isThinking,
                                        activeContext = activeContext,
                                        onSendMessage = { viewModel.sendMessage(it) },
                                        onBack = navigateToHome
                                    )
                                }

                                // 10. Settings Screen
                                composable("settings") {
                                    SettingsScreen(
                                        isLockScreenMessagesEnabled = isLockScreenMessagesEnabled,
                                        messageFrequencyMinutes = messageFrequencyMinutes,
                                        isScreenContextEnabled = isScreenContextEnabled,
                                        apiKey = apiKey,
                                        onToggleLockScreenMessages = { viewModel.setLockScreenMessages(it) },
                                        onChangeMessageFrequency = { viewModel.setMessageFrequency(it) },
                                        onToggleScreenContext = { viewModel.setScreenContextEnabled(it) },
                                        onSaveApiKey = {
                                            viewModel.saveApiKey(it)
                                            Toast.makeText(this@MainActivity, "Custom API Key Saved", Toast.LENGTH_SHORT).show()
                                        },
                                        onResetData = {
                                            viewModel.clearAllHistory()
                                            Toast.makeText(this@MainActivity, "Data Reset Complete", Toast.LENGTH_SHORT).show()
                                        },
                                        onBack = navigateToHome
                                    )
                                }

                                // 11. History Screen
                                composable("history") {
                                    HistoryScreen(
                                        messages = allMessages,
                                        onDeleteMessage = { viewModel.deleteMessage(it) },
                                        onClearAll = { viewModel.clearAllHistory() },
                                        onBack = navigateToHome
                                    )
                                }
                            }

                            // Floating Interactive Fiym Mascot Orb across app (exact same as Wealth Consciousness page)
                            if (currentRoute !in listOf("chat", "guide")) {
                                InAppFloatingOrb(
                                    onOpenChat = { navController.navigate("chat") },
                                    onOpenGuide = { navController.navigate("guide") },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(
                                            bottom = if (isBottomBarVisible) 85.dp else 24.dp,
                                            end = 16.dp
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshTodayCheckIns()
    }
}

@Composable
fun FiymBottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Rounded dark bottom floating navigation capsule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .shadow(16.dp, RoundedCornerShape(33.dp))
                .clip(RoundedCornerShape(33.dp))
                .background(Color(0xFF1E1728))
                .border(1.dp, Color(0xFF2C243B), RoundedCornerShape(33.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Button: Home
                val isHomeSelected = currentRoute == "home"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onNavigate("home") }
                        .testTag("nav_home"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isHomeSelected) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home",
                            tint = if (isHomeSelected) Color(0xFFCE93D8) else Color(0xFF756F84),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Home",
                            fontSize = 11.sp,
                            fontWeight = if (isHomeSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isHomeSelected) Color(0xFFCE93D8) else Color(0xFF756F84)
                        )
                    }
                }

                // Center Button: Fiym Floating Orb
                val isGuideSelected = currentRoute == "guide"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onNavigate("guide") }
                        .testTag("nav_orb"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFB39DDB),
                                        Color(0xFF7986CB),
                                        Color(0xFF5C6BC0)
                                    )
                                )
                            )
                            .border(
                                2.dp,
                                if (isGuideSelected) Color.White else Color(0xFFD1C4E9).copy(alpha = 0.6f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.85f))
                        )
                    }
                }

                // Right Button: Profile
                val isProfileSelected = currentRoute == "profile"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onNavigate("profile") }
                        .testTag("nav_profile"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isProfileSelected) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = if (isProfileSelected) Color(0xFFCE93D8) else Color(0xFF756F84),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Profile",
                            fontSize = 11.sp,
                            fontWeight = if (isProfileSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isProfileSelected) Color(0xFFCE93D8) else Color(0xFF756F84)
                        )
                    }
                }
            }
        }
    }
}

