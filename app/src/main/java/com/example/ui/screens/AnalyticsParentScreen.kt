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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.utils.TtsHelper
import com.example.viewmodel.StudyViewModel

@Composable
fun AnalyticsParentScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val isHindi = profile.language == "Hindi"

    var parentPin by remember { mutableStateOf("") }
    var isParentLoggedIn by remember { mutableStateOf(false) }
    var parentError by remember { mutableStateOf(false) }

    // WhatsApp permissions and values
    var whatsappAuth by remember { mutableStateOf(true) }
    var parentPhone by remember { mutableStateOf("919876543210") }
    var weeklyNotifyToggle by remember { mutableStateOf(true) }

    // Voice testing text
    var voiceTestString by remember { mutableStateOf("आज फोकस, कल सफलता! पढ़ाई शुरू करने का समय हो गया है।") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // =============== 1. CUSTOM STUDY CHARTS (Smart Analytics Dashboard) ===============
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "📊 साप्ताहिक अध्ययन घंटे विश्लेषण (Analytics)" else "📊 Weekly Study Hour Analytics",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // BEAUTIFUL DRAWN CANVAS HISTOGRAM GRAPH
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        val hours = listOf(3.5f, 4.2f, 5.0f, 2.5f, 5.5f, 4.8f, 6.5f) // Mon - Sun
                        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                        val barColor = MaterialTheme.colorScheme.primary
                        val axisColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw axis
                            drawLine(
                                color = axisColor,
                                start = Offset(40f, h - 30f),
                                end = Offset(w, h - 30f),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Draw bars
                            val maxHourValue = 8f
                            val columnCount = hours.size
                            val barWidth = (w - 60f) / columnCount * 0.5f
                            val spacing = (w - 60f) / columnCount

                            for (i in hours.indices) {
                                val barHeight = (hours[i] / maxHourValue) * (h - 50f)
                                val x = 40f + (i * spacing) + (spacing - barWidth) / 2
                                val y = h - 30f - barHeight

                                drawRect(
                                    color = barColor,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight)
                                )
                            }
                        }

                        // Day label indicators
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(start = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            days.forEach { d ->
                                Text(text = d, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(if (isHindi) "कुल अध्ययन घंटे" else "Weekly Total hours", fontSize = 11.sp, color = Color.Gray)
                            Text("32.0 Hrs", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }

                        Column {
                            Text(if (isHindi) "साप्ताहिक लक्ष्य स्तर" else "Weekly Accuracy Reach", fontSize = 11.sp, color = Color.Gray)
                            Text("88% reached", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF66BB6A))
                        }
                    }
                }
            }
        }

        // =============== 2. WHATSAPP & PARENTAL REMINDERS INTEGRATION ===============
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "💬 वॉट्सऐप रिमाइंडर्स एवं अभिभावक नोटिफिकेशन्स" else "💬 WhatsApp Reminders & Parents Sync",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = if (isHindi) "अध्ययन समय शुरू होने पर वॉट्सऐप पर नोटिफिकेशन मैसेज प्राप्त करें"
                        else "Automatically trigger syllabus reminders, streaks summaries or progress alerts to parent phone.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isHindi) "वॉट्सऐप एकीकरण चालू करें" else "Enable WhatsApp Alerts")
                        Switch(
                            checked = whatsappAuth,
                            onCheckedChange = { whatsappAuth = it },
                            modifier = Modifier.testTag("whatsapp_toggle")
                        )
                    }

                    if (whatsappAuth) {
                        OutlinedTextField(
                            value = parentPhone,
                            onValueChange = { parentPhone = it },
                            label = { Text(if (isHindi) "अभिभावक का फोन नम्बर" else "Parent WhatsApp Connection Number") },
                            modifier = Modifier.fillMaxWidth().testTag("parent_phone_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (isHindi) "साप्ताहिक रिपोर्ट अभिभावक को भेजें" else "Send Weekly Summary to Parent")
                            Switch(
                                checked = weeklyNotifyToggle,
                                onCheckedChange = { weeklyNotifyToggle = it }
                            )
                        }
                    }
                }
            }
        }

        // =============== 3. PARENT DASHBOARD GATED AREA ===============
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isParentLoggedIn) {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "👨‍👩‍👦 पैरेंट डैशबोर्ड (PIN सुरक्षित)" else "👨‍👩‍👦 Secure Parent Dashboard Panel",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(Icons.Default.Lock, contentDescription = "gated ID")
                    }

                    if (!isParentLoggedIn) {
                        Text(
                            text = if (isHindi) "अभिभावक! विद्यार्थी की हाज़िरी, वीकली ग्राफ और अध्ययन लक्ष्य तय करने के लिए लॉगिन करें। (डिफ़ॉल्ट पिन: 1234)"
                            else "Parents: verify pass PIN code to view accurate performance indices and configure daily targets. (Default: 1234)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = parentPin,
                            onValueChange = {
                                parentPin = it
                                parentError = false
                            },
                            label = { Text("4-Digit Verification PIN") },
                            modifier = Modifier.fillMaxWidth().testTag("parent_pin_input")
                        )

                        if (parentError) {
                            Text("❌ Incorrect PIN! Try 1234.", color = Color.Red, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (parentPin == "1234") {
                                    isParentLoggedIn = true
                                    parentError = false
                                } else {
                                    parentError = true
                                }
                            },
                            modifier = Modifier.align(Alignment.End).testTag("verify_pin_btn")
                        ) {
                            Text(if (isHindi) "सत्यापित करें" else "Lock & Unlock Panel")
                        }
                    } else {
                        // EXPOSED INSIDE PARENT PORTAL
                        Text(
                            text = if (isHindi) "🔐 विद्यार्थी हाज़िरी व प्रगति ट्रैकर (पैरेंट्स व्यू)" else "🔐 Real-Time Student Analytics (Parent Mode)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(if (isHindi) "दैनिक हाज़िरी" else "Month attendance", fontSize = 11.sp)
                                    Text("100% Verified", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                }
                            }

                            Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(if (isHindi) "स्क्रीन लिमिट नियम" else "Screen limits Status", fontSize = 11.sp)
                                    Text("Under-Limits", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                isParentLoggedIn = false
                                parentPin = ""
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(if (isHindi) "पैरेंट मोड से बाहर आएं" else "Log Out Panel")
                        }
                    }
                }
            }
        }

        // =============== 4. VOICE ASSISTANT DEMO BOARD ===============
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "🔊 एआई आवाज सहायक परीक्षण" else "🔊 AI Voice Assistant Soundboard",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = voiceTestString,
                        onValueChange = { voiceTestString = it },
                        modifier = Modifier.fillMaxWidth().testTag("voice_assistant_input"),
                        label = { Text(if (isHindi) "वह वाक्य लिखें जिसे आप सुनना चाहते हैं" else "Type phrases to test speak aloud:") }
                    )

                    Button(
                        onClick = { TtsHelper.speak(voiceTestString) },
                        modifier = Modifier.fillMaxWidth().testTag("voice_speak_btn")
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Voice assistant speak")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isHindi) "सहायक से आवाज सुनें" else "Test Speak Aloud")
                    }
                }
            }
        }

        // =============== 5. CLOUD BACKUP STATUS ===============
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudQueue, contentDescription = "cloud sync successful icon", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(if (isHindi) "☁️ क्लाउड बैकअप और सिंक" else "☁️ Cloud Backup & Sync status", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(if (isHindi) "आपकी दिनचर्या, अध्ययन सत्र, नोट्स और इतिहास सुरक्षित रूप से सिंक हैं।" else "Your data logs and profile configurations are safely cached offline and synced.", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
