package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.StudyViewModel

@Composable
fun PlannerScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val isHindi = profile.language == "Hindi"
    val isGenerating by viewModel.isGeneratingRoutine.collectAsStateWithLifecycle()
    val weekdays by viewModel.weekdayRoutines.collectAsStateWithLifecycle()
    val sundays by viewModel.sundayRoutines.collectAsStateWithLifecycle()

    var schoolTimeInput by remember { mutableStateOf("08:00 AM - 02:00 PM") }
    var coachingTimeInput by remember { mutableStateOf("04:30 PM - 06:30 PM") }
    var examDateInput by remember { mutableStateOf("2026-06-15") }

    var selectedTab by remember { mutableStateOf(0) } // 0 = Weekday, 1 = Sunday

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // AI ROUTINE PLANNER INPUT CARD
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "AI अध्ययन योजनाकार (AI Planner)" else "AI Study Routine Planner",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = "AI Action",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = if (isHindi) "अपनी दैनिक स्कूल, कोचिंग और परीक्षा का समय भरें ताकि AI आपके लिए पूर्ण रूप से संतुलित टाइम-टेबल तैयार कर सके!"
                        else "Describe your day below! Our server-side Gemini algorithm automatically constructs a balanced schedule including study, revision, breaks, wellness exercises, and deep sleep.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = schoolTimeInput,
                        onValueChange = { schoolTimeInput = it },
                        label = { Text(if (isHindi) "स्कूल का समय (जैसे: 8 AM - 2 PM)" else "School Hours (e.g. 8 AM - 2 PM)") },
                        leadingIcon = { Icon(Icons.Filled.Schedule, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("school_time_input")
                    )

                    OutlinedTextField(
                        value = coachingTimeInput,
                        onValueChange = { coachingTimeInput = it },
                        label = { Text(if (isHindi) "कोचिंग का समय (जैसे: 4 PM - 6 PM)" else "Coaching Hoursl (e.g. 4 PM - 6 PM)") },
                        leadingIcon = { Icon(Icons.Filled.Schedule, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("coaching_time_input")
                    )

                    OutlinedTextField(
                        value = examDateInput,
                        onValueChange = { examDateInput = it },
                        label = { Text(if (isHindi) "परीक्षा की तिथि (जैसे: 2026-06-15)" else "Exam Target Date (e.g. 2026-06-15)") },
                        leadingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("exam_date_input")
                    )

                    Button(
                        onClick = {
                            viewModel.generateAIPersonSchedules(
                                schoolTime = schoolTimeInput,
                                coachingTime = coachingTimeInput,
                                examDate = examDateInput
                            )
                        },
                        enabled = !isGenerating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_routine_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(if (isHindi) "AI प्लान बना रहा है..." else "AI Sync Generating...")
                        } else {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHindi) "AI रूटीन जनरेट करें (आज ही!)" else "Generate Personalized AI Schedule")
                        }
                    }
                }
            }
        }

        // SEPARATE WEEKDAY VS SUNDAY ROUTINE VIEW
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (isHindi) "सोम - शनि (Weekdays)" else "Weekdays Schedule") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (isHindi) "रविवार विशेष (Sundays)" else "Sunday Schedule") }
                )
            }
        }

        val displayList = if (selectedTab == 0) weekdays else sundays

        if (displayList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isHindi) "कोई भी दिनचर्या लोड नहीं है। ऊपर दिए गए बटन से जनरेट करें" else "No tasks loaded. Fill in timings above and hit the magic AI button!",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(displayList) { routine ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().animateContentSize()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(
                                            Brush.sweepGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.secondary
                                                )
                                            ),
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = routine.timeSlot,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = routine.activity,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Subject Badge
                        SuggestionChip(
                            onClick = {},
                            label = { Text(routine.subject) }
                        )
                    }
                }
            }
        }
    }
}
