package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CurriculumData
import com.example.data.entity.DoubtItem
import com.example.data.entity.NoteItem
import com.example.ui.utils.TtsHelper
import com.example.viewmodel.StudyViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StudyHubScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val isHindi = profile.language == "Hindi"

    val notes by viewModel.notesList.collectAsStateWithLifecycle()
    val searchStr by viewModel.searchQuery.collectAsStateWithLifecycle()
    val doubts by viewModel.doubtsHistory.collectAsStateWithLifecycle()
    val communityTips by viewModel.communityTips.collectAsStateWithLifecycle()

    val aiExplainingAnswer by viewModel.isExplainingAnswer.collectAsStateWithLifecycle()
    val aiExplanationText by viewModel.aiExplanationOutput.collectAsStateWithLifecycle()

    val solvingDoubt by viewModel.isSolvingDoubt.collectAsStateWithLifecycle()
    val solvedDoubtText by viewModel.doubtExplanationResult.collectAsStateWithLifecycle()

    // Mock Quizzes State Flow
    val isQuizActive by viewModel.isQuizActive.collectAsStateWithLifecycle()
    val isQuizFinished by viewModel.isQuizFinished.collectAsStateWithLifecycle()
    val questionsList by viewModel.currentQuestionsList.collectAsStateWithLifecycle()
    val qIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val selOptionIndex by viewModel.selectedOptionIndex.collectAsStateWithLifecycle()
    val quizScore by viewModel.quizScore.collectAsStateWithLifecycle()
    val quizSubject by viewModel.currentQuizSubject.collectAsStateWithLifecycle()
    val weakSubjectMsg by viewModel.weakSubjectAnalysis.collectAsStateWithLifecycle()

    val emergencyRevisionPlan by viewModel.currentEmergencyRevisionPlan.collectAsStateWithLifecycle()

    // Major sub-tabs inside Study Hub
    val categories = listOf("Doubt Solver", "Question Bank", "Mock Tests", "Notes Vault", "Revision Plans", "Community")
    val categoriesHindi = listOf("डाउट सॉल्वर", "प्रश्नावली", "मॉक टेस्ट", "नोट्स वॉल्ट", "रिवीजन प्लान", "कम्युनिटी")
    var subSectionIndex by remember { mutableStateOf(0) }

    // Dialog controllers
    var showExplanationDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var selectedSubjectFilter by remember { mutableStateOf("Science") }
    var doubtInputText by remember { mutableStateOf("") }
    var simulatedImgSelected by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Categories horizontal chip group
        ScrollableTabRow(
            selectedTabIndex = subSectionIndex,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            categories.forEachIndexed { idx, label ->
                val displayLabel = if (isHindi) categoriesHindi[idx] else label
                Tab(
                    selected = subSectionIndex == idx,
                    onClick = { subSectionIndex = idx },
                    text = { Text(displayLabel, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            when (subSectionIndex) {
                // =============== 1. DOUBT SOLVER ===============
                0 -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) "📖 फोटो खींचें या डाउट टाइप करें" else "📖 Solve Any Academic Doubt (AI)",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    OutlinedTextField(
                                        value = doubtInputText,
                                        onValueChange = { doubtInputText = it },
                                        placeholder = {
                                            Text(if (isHindi) "यहाँ अपना मैथ, साइंस या अन्य प्रश्न लिखें..." else "Type your Physics, Maths equation or Chemistry doubt...")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .testTag("doubt_textfield")
                                    )

                                    // PHOTO ATTACHMENT SIMULATOR
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { simulatedImgSelected = !simulatedImgSelected },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (simulatedImgSelected) Color(0xFF66BB6A) else MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = MaterialTheme.colorScheme.onSurface
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("upload_image_btn")
                                        ) {
                                            Icon(
                                                imageVector = if (simulatedImgSelected) Icons.Default.CheckCircle else Icons.Default.PhotoCamera,
                                                contentDescription = null,
                                                tint = if (simulatedImgSelected) Color.White else MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                if (simulatedImgSelected) {
                                                    if (isHindi) "फोटो लोड हो गया" else "Photo Loaded"
                                                } else {
                                                    if (isHindi) "प्रश्न का फोटो अपलोड करें" else "Upload Question Photo"
                                                }
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                if (doubtInputText.isNotBlank()) {
                                                    viewModel.submitStudentDoubtText(doubtInputText)
                                                    doubtInputText = ""
                                                    simulatedImgSelected = false
                                                }
                                            },
                                            enabled = !solvingDoubt,
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("solve_doubt_btn")
                                        ) {
                                            if (solvingDoubt) {
                                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                            } else {
                                                Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (isHindi) "हल करें" else "Ask AI")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // CURRENT ACTIVE SOLUTION POPUP / OUTPUT
                        if (solvedDoubtText.isNotEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isHindi) "💡 त्वरित एआई व्याख्या" else "💡 Instant AI Solution Explanation",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                            IconButton(onClick = { TtsHelper.speak(solvedDoubtText) }) {
                                                Icon(Icons.Default.VolumeUp, contentDescription = "Listen answer")
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(solvedDoubtText, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = if (isHindi) "⏳ डाउट इतिहास (Doubt History)" else "⏳ Doubt History Logs",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        if (doubts.isEmpty()) {
                            item {
                                Text(
                                    text = if (isHindi) "कोई डाउट रिकॉर्ड नहीं है।" else "Clean slate! No historical doubts asked yet.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(doubts) { d ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = d.questionText,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(onClick = { viewModel.deleteDoubtRecord(d.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "delete doubt")
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                                .fillMaxWidth()
                                        ) {
                                            Text(d.answerText, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // =============== 2. PREVIOUS YEAR QUESTION BANK ===============
                1 -> {
                    val questionsFiltered = CurriculumData.boardQuestions.filter { it.subject == selectedSubjectFilter }
                    val subjects = listOf("Mathematics", "Science", "Social Science", "English", "Hindi")

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            ScrollableTabRow(
                                selectedTabIndex = subjects.indexOf(selectedSubjectFilter),
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                subjects.forEach { sub ->
                                    Tab(
                                        selected = selectedSubjectFilter == sub,
                                        onClick = { selectedSubjectFilter = sub },
                                        text = { Text(sub) }
                                    )
                                }
                            }
                        }

                        items(questionsFiltered) { bq ->
                            var expanded by remember { mutableStateOf(false) }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded }
                                    .animateContentSize()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            SuggestionChip(
                                                onClick = {},
                                                label = { Text("Year: ${bq.year}") }
                                            )
                                            if (bq.isRepeated) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                SuggestionChip(
                                                    onClick = {},
                                                    label = { Text(if (isHindi) "🔥 बार-बार रिपीटेड" else "🔥 Frequently Repeated") },
                                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                                        labelColor = MaterialTheme.colorScheme.error
                                                    )
                                                )
                                            }
                                        }

                                        Icon(
                                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = bq.question,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )

                                    if (expanded) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = if (isHindi) "📖 हल और स्पष्टीकरण (Official Solution):" else "📖 Official Solved Explanation:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = bq.answer,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                            onClick = {
                                                viewModel.explainAnswerWithAI(bq.question, bq.answer)
                                                showExplanationDialog = true
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("ai_explain_answer_btn"),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (isHindi) "Explain This Answer via AI (AI स्पष्टीकरण)" else "Ask AI Simple Explanations (Hindi/English)")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // =============== 3. MOCK TESTS & QUIZZES ===============
                2 -> {
                    val subjects = listOf("Mathematics", "Science", "Social Science", "English", "Hindi")

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!isQuizActive && !isQuizFinished) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = if (isHindi) "🎯 त्वरित विषय-वार मूल्यांकन" else "🎯 Subject Mock Assessment Test",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = if (isHindi) "परीक्षा जैसे प्रश्नों के मॉक टेस्ट हल करें, त्वरित स्कोर पाएं और अपनी कमजोरियों को पहचानें।"
                                        else "Solve swift multiple choice board questions, receive immediate scores, and get automatic performance recommendation suggestions.",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // WEAK ANALYSIS CARD WIDGET
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Analytics, contentDescription = "Weak Analysis", tint = MaterialTheme.colorScheme.error)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = if (isHindi) "📊 कमजोरी विश्लेषण (Weak Subject Analysis)" else "📊 Weak Subject Analysis Feedback",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                                Text(
                                                    text = weakSubjectMsg,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = if (isHindi) "स्टार्ट करने के लिए विषय चुनें (Select Subject to start test):" else "Select quiz study subject:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        subjects.forEach { s ->
                                            Button(
                                                onClick = { viewModel.startNewSubjectQuiz(s) },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                            ) {
                                                Text(s)
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (isQuizActive && questionsList.isNotEmpty()) {
                            val currentQ = questionsList[qIndex]

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Subject: $quizSubject", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text(text = "${qIndex + 1}/${questionsList.size}", fontWeight = FontWeight.Bold)
                                    }

                                    LinearProgressIndicator(
                                        progress = { (qIndex.toFloat() / questionsList.size.toFloat()).coerceIn(0f, 1f) },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Text(text = currentQ.question, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                                    // Display options
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        currentQ.options.forEachIndexed { optIdx, option ->
                                            val isSelected = selOptionIndex == optIdx
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { viewModel.selectQuizOption(optIdx) }
                                                    .testTag("quiz_option_$optIdx")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    RadioButton(
                                                        selected = isSelected,
                                                        onClick = { viewModel.selectQuizOption(optIdx) }
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(text = option, fontSize = 14.sp)
                                                }
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = { viewModel.submitQuizAnswer() },
                                        enabled = selOptionIndex != -1,
                                        modifier = Modifier.fillMaxWidth().testTag("submit_quiz_answer_btn")
                                    ) {
                                        Text(if (isHindi) "उत्तर जमा करें" else "Submit Answer")
                                    }
                                }
                            }
                        } else if (isQuizFinished) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = "Success", tint = Color(0xFF4CAF50), modifier = Modifier.size(56.dp))
                                    Text(
                                        text = if (isHindi) "मॉक टेस्ट समाप्त! 🎉" else "Assessment Finished! 🎉",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = if (isHindi) "आपका स्कोर: $quizScore / ${questionsList.size}" else "Your score: $quizScore / ${questionsList.size}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = if (isHindi) "कमजोरी विश्लेषण और विश्लेषण फ़ीडबैक आपके डैशबोर्ड पर अपडेट कर दिया गया है। अपनी कमियों पर ध्यान दें!"
                                        else "Instant feedback updated! Check overall mock charts or reviews to target topics you made errors in.",
                                        textAlign = TextAlign.Center,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Button(
                                        onClick = { viewModel.startNewSubjectQuiz(quizSubject) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (isHindi) "दोबारा टेस्ट दें" else "Retry Assessment")
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.startNewSubjectQuiz("Mathematics") /* reset view */ },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (isHindi) "मुख्य मेनू पर जाएं" else "Back to Quiz Menu")
                                    }
                                }
                            }
                        }
                    }
                }

                // =============== 4. NOTES VAULT ===============
                3 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = searchStr,
                                onValueChange = { viewModel.modifySearchNotes(it) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                placeholder = { Text(if (isHindi) "नोट्स खोजें..." else "Search notes...") },
                                modifier = Modifier.weight(1f).testTag("search_notes_input")
                            )

                            IconButton(
                                onClick = { showAddNoteDialog = true },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                    .testTag("add_note_fab")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Notes", tint = Color.White)
                            }
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (notes.isEmpty()) {
                                item {
                                    Text(
                                        text = if (isHindi) "कोई अध्ययन नोट नहीं मिला। '+' दबाकर अपना पहला नोट बनाएं।" else "No study notes stored. Tap '+' to safeguard custom study summaries!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                items(notes) { n ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(text = n.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                    Text(text = n.subject, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    IconButton(onClick = { TtsHelper.speak(n.content) }) {
                                                        Icon(Icons.Default.VolumeUp, contentDescription = "Speak notes")
                                                    }
                                                    IconButton(onClick = { viewModel.deleteStudentNote(n.id) }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "delete", tint = Color.Gray)
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(text = n.content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = n.dateTime, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // =============== 5. EMERGENCY EXAM MODE ===============
                4 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Emergency, contentDescription = "Emergency", tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isHindi) "🚨 आपातकालीन परीक्षा तैयारी मोड" else "🚨 Emergency Crash Revision Mode",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Text(
                                    text = if (isHindi) "परीक्षा में कम दिन बचे हैं? चिंता न करें! 30-दिन, 15-दिन या 7-दिन की क्रैश रिवीजन टाइमलाइन चुनें और केवल उच्च-प्राथमिकता वाले बार-बार रिपीट होने वाले टॉपिक्स पर फोकस करें!"
                                    else "Exams closed? Select crash 30, 15 or 7 days timeline to focus strictly on board repeated syllabus of highest-weights. Filter by your class!",
                                    fontSize = 12.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    FilterChip(
                                        selected = emergencyRevisionPlan == "30-day",
                                        onClick = { viewModel.selectEmergencyPlan("30-day") },
                                        label = { Text("30-Day Plan") }
                                    )
                                    FilterChip(
                                        selected = emergencyRevisionPlan == "15-day",
                                        onClick = { viewModel.selectEmergencyPlan("15-day") },
                                        label = { Text("15-Day Plan") }
                                    )
                                    FilterChip(
                                        selected = emergencyRevisionPlan == "7-day",
                                        onClick = { viewModel.selectEmergencyPlan("7-day") },
                                        label = { Text("7-Day Plan") }
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isHindi) "🎯 उच्च-प्राथमिकता वाले विषय सूची:" else "🎯 High-Priority Topics Focused",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )

                        // Load dynamic custom prioritized lists based on class
                        val priorityTopics = if (emergencyRevisionPlan == "30-day") {
                            listOf(
                                "Mathematics: Quadratic Equations & Trigonometry (Solve 10 yrs repeating items)",
                                "Science: Chemical reactions equations & Light reflection Refraction formula review",
                                "Social Science: Indian Nationalism summaries & Map practice sessions",
                                "Hindi / English: Daily 2 chapter critical summaries & core grammar practice sheets"
                            )
                        } else if (emergencyRevisionPlan == "15-day") {
                            listOf(
                                "Mathematics: Surface Areas & Volumes & Standard statistics calculations",
                                "Science: Carbon and its compounds & Electric currents diagrams",
                                "English: Writing skills formats review (Letter, reports) & direct-indirect rules"
                            )
                        } else {
                            listOf(
                                "Mathematics: Full formula booklet revision & 3 practice test series papers",
                                "Science: Balance chemical equations & Lens structures",
                                "General: Read study community motivation tips & sleep 8 hours to retain memory"
                            )
                        }

                        priorityTopics.forEach { topic ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = "priority", tint = Color(0xFFFFB300))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = topic, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // =============== 6. STUDY COMMUNITY ===============
                5 -> {
                    var newTipInput by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = if (isHindi) "🤝 सुरक्षित विद्यार्थी समुदाय" else "🤝 Moderated Peer Study Community",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                OutlinedTextField(
                                    value = newTipInput,
                                    onValueChange = { newTipInput = it },
                                    placeholder = { Text(if (isHindi) "अपनी कोई पढ़ाई की टिप्स या मोटिवेशन साझा करें..." else "Share a useful formulas study tip or motivation...") },
                                    modifier = Modifier.fillMaxWidth().testTag("community_input")
                                )
                                Button(
                                    onClick = {
                                        viewModel.shareCommunityTip(newTipInput)
                                        newTipInput = ""
                                    },
                                    modifier = Modifier.align(Alignment.End).testTag("share_community_btn")
                                ) {
                                    Text(if (isHindi) "साझा करें" else "Post Tip")
                                }
                            }
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(communityTips) { tip ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.AccountCircle, contentDescription = "user", tint = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(tip.author, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(tip.content, fontSize = 13.sp)
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable { viewModel.likeTip(tip.id, tip.likes) }
                                        ) {
                                            Icon(Icons.Default.ThumbUp, contentDescription = "like", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "${tip.likes}", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- 1. POPUP DIALOG FOR AI EXPLANATIONS ---
    if (showExplanationDialog) {
        Dialog(onDismissRequest = { showExplanationDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "🤖 AI ट्यूटर की आसान व्याख्या" else "🤖 AI Smart Tutor Breakdown",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = { TtsHelper.speak(aiExplanationText) }) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "TTS Read Aloud")
                        }
                    }

                    Divider()

                    Box(
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (aiExplainingAnswer) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                CircularProgressIndicator()
                                Text(if (isHindi) "AI सोच रहा है..." else "Asking Gemini models...")
                            }
                        } else {
                            Text(
                                text = aiExplanationText,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Button(
                        onClick = { showExplanationDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(if (isHindi) "ठीक है (Got it!)" else "Close Dialog")
                    }
                }
            }
        }
    }

    // --- 2. ADD NOTE DIALOG ---
    if (showAddNoteDialog) {
        var noteTitle by remember { mutableStateOf("") }
        var noteSubject by remember { mutableStateOf("Science") }
        var noteContent by remember { mutableStateOf("") }

        val subjects = listOf("Mathematics", "Science", "Social Science", "English", "Hindi")

        Dialog(onDismissRequest = { showAddNoteDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isHindi) "नया नोट जोड़ें" else "Create Study Note",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text(if (isHindi) "शीर्षक (Title)" else "Note Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column {
                        Text(text = if (isHindi) "विषय (Subject):" else "Select Subject:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            subjects.forEach { s ->
                                FilterChip(
                                    selected = noteSubject == s,
                                    onClick = { noteSubject = s },
                                    label = { Text(s) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text(if (isHindi) "नोट सामग्री (Write body)" else "Write notes here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddNoteDialog = false }) {
                            Text(if (isHindi) "रद्द करें" else "Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                                    viewModel.createStudentNote(noteTitle, noteSubject, noteContent)
                                    showAddNoteDialog = false
                                }
                            }
                        ) {
                            Text(if (isHindi) "सहेजें" else "Save Note")
                        }
                    }
                }
            }
        }
    }
}
