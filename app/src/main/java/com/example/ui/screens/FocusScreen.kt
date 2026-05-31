package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.StudyViewModel

@Composable
fun FocusScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val isHindi = profile.language == "Hindi"

    val timerSecondsLeft by viewModel.timerSecondsLeft.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val timerTotalDuration by viewModel.timerTotalDuration.collectAsStateWithLifecycle()
    val isBreakMode by viewModel.isBreakMode.collectAsStateWithLifecycle()
    val pomodoroCount by viewModel.pomodoroCount.collectAsStateWithLifecycle()

    val screenUsageMinutes by viewModel.screenUsageMinutes.collectAsStateWithLifecycle()
    val entertainmentMinutes by viewModel.entertainmentMinutes.collectAsStateWithLifecycle()
    val studyMinutes by viewModel.studyMinutes.collectAsStateWithLifecycle()
    val showWarning by viewModel.showScreenTimeWarning.collectAsStateWithLifecycle()

    var distractionToggle by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // --- POMODORO TIMER CARD ---
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isBreakMode) {
                            if (isHindi) "⏸️ आराम का समय (Break Interval)" else "⏸️ Relaxing Break Time"
                        } else {
                            if (isHindi) "🔥 ध्यान केन्द्रित चक्र (Active Focus Session)" else "🔥 Deep Focus Session"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isBreakMode) Color(0xFF66BB6A) else MaterialTheme.colorScheme.primary
                    )

                    // TIMER CLOCK RADIAL CIRCLE
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(10.dp)
                    ) {
                        val progressNormalized = if (timerTotalDuration > 0) {
                            timerSecondsLeft.toFloat() / timerTotalDuration.toFloat()
                        } else 1f

                        val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        val primaryColor = if (isBreakMode) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = trackColor,
                                radius = size.minDimension / 2f,
                                style = Stroke(width = 8.dp.toPx())
                            )
                            drawArc(
                                color = primaryColor,
                                startAngle = -90f,
                                sweepAngle = 360f * progressNormalized,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Countdown Text display
                        val minutes = timerSecondsLeft / 60
                        val seconds = timerSecondsLeft % 60
                        val formatStr = "%02d:%02d".format(minutes, seconds)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = formatStr,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("pomodoro_countdown")
                            )
                            Text(
                                text = if (isHindi) "Completed: $pomodoroCount" else "Sessions: $pomodoroCount",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Toggles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (isTimerRunning) viewModel.pauseFocusTimer() else viewModel.startFocusTimer()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) Color(0xFFEF5350) else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("timer_control_btn")
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isTimerRunning) {
                                    if (isHindi) "रोकें (Pause)" else "Pause"
                                } else {
                                    if (isHindi) "शुरू करें" else "Start"
                                }
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetFocusTimer() },
                            modifier = Modifier.testTag("timer_reset_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset timer")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHindi) "रिसेट" else "Reset")
                        }
                    }
                }
            }
        }

        // --- DISTRACTION BLOCKER TOOLBAR ---
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (distractionToggle) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    }
                ),
                shape = RoundedCornerShape(16.dp)
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
                            text = if (isHindi) "🔇 विकर्षण ब्लॉकर (Distraction Blocker)" else "🔇 Silent Distraction Blocker",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = if (isHindi) "अध्ययन के दौरान सोशल मीडिया नोटिफिकेशन्स ब्लॉक करें" else "Auto-mute simulated notifications of social networks during pomodoro.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = distractionToggle,
                        onCheckedChange = { distractionToggle = it },
                        modifier = Modifier.testTag("distraction_toggle")
                    )
                }
            }
        }

        // --- SCREEN TIME LIMITS / COMPARISON TRACKER ---
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isHindi) "📱 मोबाइल स्क्रीन टाइम ट्रैकर" else "📱 Screen Time Blocker & Analytics",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // EXCEED WARNING BOX
                    AnimatedVisibility(
                        visible = showWarning,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = "Limit Exceeded", tint = Color(0xFFC62828))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isHindi) "⚠️ सीमा चेतावनी! (Limit Exceeded)" else "⚠️ Entertainment Limit Exceeded!",
                                        color = Color(0xFFC62828),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = if (isHindi) "मनोरंजन की समय सीमा ${profile.dailyScreenLimitMinutes} मिनट से अधिक हो गई है! अपनी पढ़ाई पर लौटें।"
                                        else "You configured screen limit of ${profile.dailyScreenLimitMinutes} mins. Put your device down and return to lessons!",
                                        color = Color(0xFFC62828),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // Comparison Metrics
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Study Time progress
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(if (isHindi) "📖 अध्ययन समय (Self Study)" else "📖 Deep Study Sessions", fontSize = 13.sp)
                            Text("$studyMinutes Min", fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(
                            progress = { (studyMinutes.toFloat() / 240f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )

                        // Entertainment progress
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(if (isHindi) "🎮 मनोरंजन व गेम्स टाइम" else "🎮 Entertainment (Social Media)", fontSize = 13.sp)
                            Text("$entertainmentMinutes Min", fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(
                            progress = { (entertainmentMinutes.toFloat() / profile.dailyScreenLimitMinutes.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = if (showWarning) Color(0xFFD32F2F) else Color(0xFFFFB300),
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                    // Simulated screen adjuster for demo testing (Feature 4 requirement: compare study vs entertainment)
                    Column {
                        Text(
                            text = if (isHindi) "सिमुलेटेड मनोरंजन समय समायोजित करें (Adjust Demo Entertainment Mins):" 
                            else "Adjust Simulated Entertainment (Demo): $entertainmentMinutes Min",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = entertainmentMinutes.toFloat(),
                            onValueChange = { viewModel.setSimulatedEntertainmentMinutes(it.toInt()) },
                            valueRange = 10f..250f,
                            modifier = Modifier.testTag("screen_slider")
                        )
                    }
                }
            }
        }
    }
}
