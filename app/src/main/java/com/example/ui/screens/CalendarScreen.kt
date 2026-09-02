package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TrainingExercise(
    val id: String,
    val title: String,
    val details: String,
    var isDone: Boolean = false
)

enum class ActivityLevel(val color: Color) {
    NONE(Color(0xFFE0E0E0)),
    LOW(Color(0xFFB0BEC5)),
    MODERATE(Color(0xFF42A5F5)),
    HIGH(Color(0xFF66BB6A)),
    VERY_HIGH(Color(0xFFEC407A))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember { mutableIntStateOf(14) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    BackHandler {
        onBack()
    }

    val exercises = remember {
        mutableStateListOf(
            TrainingExercise("1", "Exercise: Sit and wait", "10 min · 3 approaches", isDone = true),
            TrainingExercise("2", "Exercise: Retrieving", "15 min · 2 approaches", isDone = false),
            TrainingExercise("3", "Exercise: Give me your paw", "20 min · 3 approaches", isDone = false)
        )
    }

    // Heat map activity level per day in May 2025
    val dayActivity = remember {
        mapOf(
            1 to ActivityLevel.LOW,
            2 to ActivityLevel.MODERATE,
            3 to ActivityLevel.VERY_HIGH,
            4 to ActivityLevel.HIGH,
            5 to ActivityLevel.LOW,
            6 to ActivityLevel.MODERATE,
            7 to ActivityLevel.HIGH,
            8 to ActivityLevel.VERY_HIGH,
            9 to ActivityLevel.HIGH,
            10 to ActivityLevel.MODERATE,
            11 to ActivityLevel.LOW,
            12 to ActivityLevel.VERY_HIGH,
            13 to ActivityLevel.HIGH,
            14 to ActivityLevel.VERY_HIGH,
            15 to ActivityLevel.MODERATE,
            16 to ActivityLevel.HIGH,
            17 to ActivityLevel.VERY_HIGH,
            18 to ActivityLevel.LOW,
            19 to ActivityLevel.MODERATE,
            20 to ActivityLevel.HIGH,
            21 to ActivityLevel.VERY_HIGH,
            22 to ActivityLevel.HIGH,
            23 to ActivityLevel.MODERATE,
            24 to ActivityLevel.LOW,
            25 to ActivityLevel.HIGH,
            26 to ActivityLevel.VERY_HIGH,
            27 to ActivityLevel.HIGH,
            28 to ActivityLevel.MODERATE,
            29 to ActivityLevel.HIGH,
            30 to ActivityLevel.VERY_HIGH,
            31 to ActivityLevel.HIGH
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Activity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1E2026)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E2026)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Calendar picker */ }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Calendar Picker",
                            tint = Color(0xFF1E2026)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF9F9FB))
            )
        },
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF9F9FB)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Month Dropdown Header (May 2025)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFEDE7F6))
                        .clickable { /* Select month */ }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "May 2025",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF5E35B1)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown",
                        tint = Color(0xFF5E35B1),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            item {
                // Full Month Calendar Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Weekday names
                        val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            weekDays.forEach { day ->
                                Text(
                                    text = day,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF9E9E9E)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Calendar Grid for May 2025 (May 1st is Thursday -> 3 empty offset cells)
                        val offset = 3
                        val totalCells = 35 // 5 rows of 7

                        for (week in 0 until 5) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (dayCol in 0 until 7) {
                                    val cellIndex = week * 7 + dayCol
                                    val dayNumber = cellIndex - offset + 1

                                    if (dayNumber in 1..31) {
                                        val isSelected = dayNumber == selectedDay
                                        val level = dayActivity[dayNumber] ?: ActivityLevel.NONE

                                        Column(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color(0xFFEDE7F6) else Color.Transparent)
                                                .clickable { selectedDay = dayNumber },
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "$dayNumber",
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color(0xFF5E35B1) else Color(0xFF212121)
                                            )
                                            // Activity dot indicator
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(level.color)
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(36.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Today's training header with + Add Exercise button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's training",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF1E2026)
                    )

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFEDE7F6))
                            .clickable { showAddExerciseDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color(0xFF5E35B1),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Exercise",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5E35B1)
                        )
                    }
                }
            }

            items(exercises) { exercise ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            val index = exercises.indexOfFirst { it.id == exercise.id }
                            if (index != -1) {
                                exercises[index] = exercises[index].copy(isDone = !exercises[index].isDone)
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (exercise.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (exercise.isDone) "Done" else "Not done",
                            tint = if (exercise.isDone) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exercise.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E2026)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = exercise.details,
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showAddExerciseDialog) {
        var exTitle by remember { mutableStateOf("") }
        var exDuration by remember { mutableStateOf("15 min") }
        var exReps by remember { mutableStateOf("3 approaches") }

        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text("Add Exercise", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = exTitle,
                        onValueChange = { exTitle = it },
                        label = { Text("Exercise Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = exDuration,
                        onValueChange = { exDuration = it },
                        label = { Text("Duration (e.g. 15 min)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = exReps,
                        onValueChange = { exReps = it },
                        label = { Text("Approaches (e.g. 3 approaches)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (exTitle.isNotBlank()) {
                            exercises.add(
                                TrainingExercise(
                                    id = System.currentTimeMillis().toString(),
                                    title = if (exTitle.startsWith("Exercise:")) exTitle else "Exercise: $exTitle",
                                    details = "$exDuration · $exReps",
                                    isDone = false
                                )
                            )
                            showAddExerciseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E35B1))
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
