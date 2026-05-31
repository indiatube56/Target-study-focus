package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.StudentProfile
import com.example.data.entity.StudyRoutine
import com.example.ui.utils.TtsHelper
import com.example.viewmodel.StudyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val isHindi = profile.language == "Hindi"
    val routines by viewModel.weekdayRoutines.collectAsStateWithLifecycle()
    val achievements by viewModel.dailyAchievements.collectAsStateWithLifecycle()
    val badges by viewModel.studyBadges.collectAsStateWithLifecycle()
    val waterCount by viewModel.waterIntakeCups.collectAsStateWithLifecycle()
    val eyeCount by viewModel.eyeRestLogCount.collectAsStateWithLifecycle()
    val sleepCount by viewModel.sleepHours.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // --- 1. Header & Quick Switchers ---
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "नमस्ते, ${profile.name} 👋" else "Welcome, ${profile.name} 👋",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${profile.studentClass} • ${profile.board}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                .testTag("edit_profile_btn")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Edit Profile Settings")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // BILINGUAL ADAPTIVE BANNER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .clickable {
                                TtsHelper.speak(
                                    if (isHindi) "आज फोकस, कल सफलता" else "Today Focus, Tomorrow Success"
                                )
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "“आज फोकस, कल सफलता”",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("app_tagline")
                            )
                            Text(
                                text = if (isHindi) "टैप करें - आवाज सुनें" else "Tap here to listen tagline motivation",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Language Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = !isHindi,
                            onClick = { viewModel.selectLanguage("English") },
                            label = { Text("English") },
                            leadingIcon = { if (!isHindi) Icon(Icons.Default.Check, contentDescription = null) }
                        )
                        FilterChip(
                            selected = isHindi,
                            onClick = { viewModel.selectLanguage("Hindi") },
                            label = { Text("हिंदी") },
                            leadingIcon = { if (isHindi) Icon(Icons.Default.Check, contentDescription = null) }
                        )

                        // Light / Dark Theme Switcher
                        val isDark = profile.themeMode == "Dark"
                        IconButton(
                            onClick = { viewModel.selectTheme(if (isDark) "Light" else "Dark") }
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Toggle Light/Dark Theme",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Streak & Attendance & Goals Cards ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("streak_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Color(0xFFFF8A65),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${profile.completedStreak} ${if (isHindi) "दिन" else "Days"}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "लगातार पढ़ाई streak" else "Study Streak",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Target Goals Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("goals_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.EmojiEvents,
                            contentDescription = "Target Score Goal",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${profile.scoreGoal}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "लक्ष्य स्कोर" else "Target Score",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // --- 3. Attendance Marker Check-in ---
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "दैनिक अध्ययन हाज़िरी" else "Daily Attendance",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (isHindi) "आज की उपस्थिति दर्ज करें और अपनी streak बचाएं!" else "Mark check-in to save your study streak!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Button(
                        onClick = { viewModel.markAttendanceCheckIn() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("attendance_btn")
                    ) {
                        Text(if (isHindi) "चेक-इन करें" else "Check In")
                    }
                }
            }
        }

        // --- 4. Today's Routine Task List Checklist (Features 1 & 8) ---
        item {
            Text(
                text = if (isHindi) "आज का रूटीन (Weekday)" else "Today's Study Checklist",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (routines.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHindi) "रूटीन खाली है! 'AI Study Planner' में जाकर जनरेट करें।" else "No routines loaded yet! Go to 'AI Study Planner' tab to generate one instantly.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(routines) { r ->
                RoutineChecklistItem(
                    routine = r,
                    isHindi = isHindi,
                    onCheckedChange = { isChecked ->
                        viewModel.toggleRoutineTaskCompleted(r.id, isChecked)
                    }
                )
            }
        }

        // --- 5. Health & Wellness Section (Feature 26) ---
        item {
            Text(
                text = if (isHindi) "स्वास्थ्य एवं आदतें (Health & Wellness)" else "Health & Wellness Habits",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Water drinking Tracker
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WaterDrop, contentDescription = "Water", tint = Color(0xFF29B6F6))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "पानी पीने की रिमाइंडर्स" else "Water Hydration Log",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isHindi) "logged: $waterCount / 8 cups" else "logged: $waterCount / 8 cups requested",
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.logWaterCups() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("+ Cup")
                        }
                    }
                }

                // Eye Rest reminders
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = "Eye rest", tint = Color(0xFF66BB6A))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "आँख-आराम (20-20-20 नियम)" else "Eye-Rest Tracker",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isHindi) "आज के विश्राम: $eyeCount बार" else "Rested today: $eyeCount times",
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.logEyeRest() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(if (isHindi) "रिलैक्स" else "Relax")
                        }
                    }
                }

                // Sleep Tracking Habit
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hotel, contentDescription = "Sleep", tint = Color(0xFFBAC5E5))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "नींद की अवधि" else "Sleep Duration Tracker",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isHindi) "सोने का समय: $sleepCount घंटे (ideal 7.5)" else "Slept: $sleepCount hours (ideal 7.5)",
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { viewModel.adjustSleepHours(-0.5) }) {
                                Icon(Icons.Default.Remove, contentDescription = "decrease")
                            }
                            IconButton(onClick = { viewModel.adjustSleepHours(0.5) }) {
                                Icon(Icons.Default.Add, contentDescription = "increase")
                            }
                        }
                    }
                }
            }
        }

        // --- 6. Badges & Streaks Awards (Feature 16) ---
        item {
            Text(
                text = if (isHindi) "मेरे मेडल्स व उपलब्धियां" else "My Badges & Achievement Streak",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                badges.forEach { b ->
                    SuggestionChip(
                        onClick = { TtsHelper.speak("Congratulations on earning badge: ${b.first}") },
                        label = { Text(b.first) },
                        icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = "badge icon", tint = Color(0xFFFFC107)) }
                    )
                }
            }
        }
    }

    // --- Edit Profile Dialog ---
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(profile.name) }
        var tempClass by remember { mutableStateOf(profile.studentClass) }
        var tempBoard by remember { mutableStateOf(profile.board) }
        var tempGoal by remember { mutableStateOf(profile.scoreGoal) }
        var tempScreen by remember { mutableStateOf(profile.dailyScreenLimitMinutes) }

        val classesAvailable = listOf("Class 6", "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12")
        val boardsAvailable = listOf("CBSE", "ICSE", "UP Board", "Bihar Board", "State Board")

        Dialog(onDismissRequest = { showEditProfileDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isHindi) "विद्यार्थी प्रोफ़ाइल सेटिंग्स" else "Student Profile Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text(if (isHindi) "आपका नाम" else "Your Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Class Dropdown alternative simple list selection
                    Column {
                        Text(if (isHindi) "कक्षा चुनें (Select Class):" else "Select Class:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            classesAvailable.forEach { c ->
                                FilterChip(
                                    selected = tempClass == c,
                                    onClick = { tempClass = c },
                                    label = { Text(c) }
                                )
                            }
                        }
                    }

                    // Board selection alternative simple list
                    Column {
                        Text(if (isHindi) "बोर्ड चुनें (Select Board):" else "Select Board:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            boardsAvailable.forEach { b ->
                                FilterChip(
                                    selected = tempBoard == b,
                                    onClick = { tempBoard = b },
                                    label = { Text(b) }
                                )
                            }
                        }
                    }

                    // Score Goal slider
                    Column {
                        Text(
                            text = if (isHindi) "लक्ष्य स्कोर: $tempGoal%" else "Score Target Goal: $tempGoal%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Slider(
                            value = tempGoal.toFloat(),
                            onValueChange = { tempGoal = it.toInt() },
                            valueRange = 50f..100f
                        )
                    }

                    // Daily Screen Limit
                    Column {
                        Text(
                            text = if (isHindi) "स्क्रीन समय सीमा (Screen Limit): $tempScreen मिनट" else "Daily Screen Limit: $tempScreen Mins",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Slider(
                            value = tempScreen.toFloat(),
                            onValueChange = { tempScreen = it.toInt() },
                            valueRange = 30f..300f
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showEditProfileDialog = false }) {
                            Text(if (isHindi) "रद्द करें" else "Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.updateStudentProfile(
                                    name = tempName,
                                    studentClass = tempClass,
                                    board = tempBoard,
                                    subjectPrefs = "Maths, Science",
                                    scoreGoal = tempGoal,
                                    screenLimit = tempScreen
                                )
                                showEditProfileDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(if (isHindi) "बचाएँ" else "Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineChecklistItem(
    routine: StudyRoutine,
    isHindi: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .testTag("routine_item_${routine.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (routine.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = routine.isCompleted,
                onCheckedChange = { onCheckedChange(it == true) }
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = routine.activity,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (routine.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(routine.subject) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = routine.timeSlot,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
