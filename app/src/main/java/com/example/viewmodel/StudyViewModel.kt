package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BoardQuestion
import com.example.data.CurriculumData
import com.example.data.QuizQuestion
import com.example.data.entity.*
import com.example.network.GeminiApiClient
import com.example.repository.StudyRepository
import com.example.ui.utils.TtsHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StudyViewModel(
    application: Application,
    private val repository: StudyRepository
) : AndroidViewModel(application) {

    private val TAG = "StudyViewModel"

    // --- Singleton Profile ---
    val studentProfile: StateFlow<StudentProfile> = repository.profileFlow
        .map { it ?: StudentProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StudentProfile()
        )

    val selectedLanguage: StateFlow<String> = studentProfile
        .map { it.language }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "English")

    val selectedTheme: StateFlow<String> = studentProfile
        .map { it.themeMode }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Dark")

    // --- Routines Flow ---
    val weekdayRoutines: StateFlow<List<StudyRoutine>> = repository.getRoutinesByDayFlow("Weekday")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sundayRoutines: StateFlow<List<StudyRoutine>> = repository.getRoutinesByDayFlow("Sunday")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Notes Flow ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val notesList: StateFlow<List<NoteItem>> = _searchQuery
        .flatMapLatest { query -> repository.searchNotes(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Attendance Flow ---
    val attendanceHistory: StateFlow<List<Attendance>> = repository.attendanceFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Doubts Flow ---
    val doubtsHistory: StateFlow<List<DoubtItem>> = repository.doubtsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Community Tips Flow ---
    val communityTips: StateFlow<List<CommunityTip>> = repository.tipsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Quiz Attempts Flow ---
    val quizAttempts: StateFlow<List<QuizAttempt>> = repository.quizAttemptsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Simulated Screen Time State ---
    private val _screenUsageMinutes = MutableStateFlow(115)
    val screenUsageMinutes: StateFlow<Int> = _screenUsageMinutes.asStateFlow()

    private val _entertainmentMinutes = MutableStateFlow(84)
    val entertainmentMinutes: StateFlow<Int> = _entertainmentMinutes.asStateFlow()

    private val _studyMinutes = MutableStateFlow(180)
    val studyMinutes: StateFlow<Int> = _studyMinutes.asStateFlow()

    val showScreenTimeWarning: StateFlow<Boolean> = combine(
        entertainmentMinutes,
        studentProfile
    ) { entMinutes, profile ->
        entMinutes > profile.dailyScreenLimitMinutes
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // --- Focus Mode (Pomodoro Timer) State ---
    private var timerJob: Job? = null
    private val _timerSecondsLeft = MutableStateFlow(1500) // 25 Min
    val timerSecondsLeft: StateFlow<Int> = _timerSecondsLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var _timerTotalDuration = MutableStateFlow(1500)
    val timerTotalDuration: StateFlow<Int> = _timerTotalDuration.asStateFlow()

    private val _isBreakMode = MutableStateFlow(false)
    val isBreakMode: StateFlow<Boolean> = _isBreakMode.asStateFlow()

    private val _pomodoroCount = MutableStateFlow(0)
    val pomodoroCount: StateFlow<Int> = _pomodoroCount.asStateFlow()

    // --- Question Explanations / AI solvers State ---
    private val _aiExplanationOutput = MutableStateFlow("")
    val aiExplanationOutput: StateFlow<String> = _aiExplanationOutput.asStateFlow()

    private val _isExplainingAnswer = MutableStateFlow(false)
    val isExplainingAnswer: StateFlow<Boolean> = _isExplainingAnswer.asStateFlow()

    private val _doubtExplanationResult = MutableStateFlow("")
    val doubtExplanationResult: StateFlow<String> = _doubtExplanationResult.asStateFlow()

    private val _isSolvingDoubt = MutableStateFlow(false)
    val isSolvingDoubt: StateFlow<Boolean> = _isSolvingDoubt.asStateFlow()

    private val _isGeneratingRoutine = MutableStateFlow(false)
    val isGeneratingRoutine: StateFlow<Boolean> = _isGeneratingRoutine.asStateFlow()

    // --- Quiz Current State ---
    private val _currentQuizSubject = MutableStateFlow("Mathematics")
    val currentQuizSubject: StateFlow<String> = _currentQuizSubject.asStateFlow()

    private val _currentQuestionsList = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val currentQuestionsList: StateFlow<List<QuizQuestion>> = _currentQuestionsList.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow(-1)
    val selectedOptionIndex: StateFlow<Int> = _selectedOptionIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _isQuizActive = MutableStateFlow(false)
    val isQuizActive: StateFlow<Boolean> = _isQuizActive.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    // --- Weak Subject Analysis State ---
    val weakSubjectAnalysis: StateFlow<String> = quizAttempts.map { attempts ->
        if (attempts.isEmpty()) {
            "Not enough quiz data. Attempt quizzes to identify weak visual performance areas."
        } else {
            val scoresBySubject = attempts.groupBy { it.subject }
            val averages = scoresBySubject.mapValues { (_, list) ->
                list.map { (it.score.toDouble() / it.totalQuestions) * 100 }.average()
            }
            val weak = averages.minByOrNull { it.value }
            if (weak != null && weak.value < 75.0) {
                "Focus on ${weak.key} (Average: ${"%.1f".format(weak.value)}%). Need extra practice and doubt clearing sessions."
            } else if (weak != null) {
                "Incredible score! Your lowest average is ${"%.1f".format(weak.value)}% in ${weak.key}. Keep maintaining consistency!"
            } else {
                "Excellent score patterns. Keep practicing mock test series!"
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Calculating...")

    // --- Health Section Flow ---
    private val _waterIntakeCups = MutableStateFlow(3)
    val waterIntakeCups: StateFlow<Int> = _waterIntakeCups.asStateFlow()

    private val _eyeRestLogCount = MutableStateFlow(2)
    val eyeRestLogCount: StateFlow<Int> = _eyeRestLogCount.asStateFlow()

    private val _sleepHours = MutableStateFlow(7.5)
    val sleepHours: StateFlow<Double> = _sleepHours.asStateFlow()

    // --- Emergency Exam Mode Planning ---
    private val _currentEmergencyRevisionPlan = MutableStateFlow("30-day")
    val currentEmergencyRevisionPlan: StateFlow<String> = _currentEmergencyRevisionPlan.asStateFlow()

    // --- Streaks and Rewards Counters ---
    val dailyAchievements: StateFlow<List<String>> = combine(
        studentProfile,
        pomodoroCount,
        waterIntakeCups
    ) { profile, pomodoro, water ->
        val list = mutableListOf<String>()
        if (profile.lastCheckInDate == getCurrentDateString()) {
            list.add("Check-in Accomplished: Mark attendance (पूर्ण)")
        }
        if (pomodoro > 0) {
            list.add("Focused study session: Completed $pomodoro Pomodoros!")
        }
        if (water >= 6) {
            list.add("Hydration Accomplished: Logged $water cups of water")
        } else {
            list.add("Hydration: Log $water/8 cups of water today")
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyBadges: StateFlow<List<Pair<String, String>>> = studentProfile.map { profile ->
        val badges = mutableListOf<Pair<String, String>>()
        badges.add("Pioneer" to "Started your Target Focus Study journey")
        if (profile.completedStreak >= 1) badges.add("Dedicated Student (1 Day)" to "Completed 1-day study streak")
        if (profile.completedStreak >= 3) badges.add("Focus Seeker (3 Days)" to "Maintained a solid 3 days streak!")
        if (profile.completedStreak >= 5) badges.add("Rising Master (5 Days)" to "Achieved a shining 5-day continuous streak")
        if (profile.completedStreak >= 10) badges.add("Ultimate Achiever (10 Days)" to "Stellar dedication of 10 study days")
        badges
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Custom Reminders Setup
    val reminderSchedules = listOf(
        "Morning Routine Start" to "06:00 AM",
        "School Time Alarm" to "07:30 AM",
        "Self Study Session" to "04:30 PM",
        "Revision Session Tracker" to "08:00 PM"
    )

    init {
        // Run database initializer
        viewModelScope.launch {
            if (repository.getProfileSingle() == null) {
                val demoProfile = StudentProfile(
                    name = "Rahul Kumar",
                    studentClass = "Class 10",
                    board = "CBSE",
                    subjectPreferences = "Maths, Science",
                    language = "English",
                    themeMode = "Dark",
                    scoreGoal = 95,
                    dailyScreenLimitMinutes = 120,
                    completedStreak = 5,
                    lastCheckInDate = getCurrentDateString()
                )
                repository.saveProfile(demoProfile)
            }
            // Seed sample database routines and community tips if empty
            seedDatabaseSampleMockData()
        }
    }

    // --- Setup profile change handlers ---
    fun updateStudentProfile(
        name: String,
        studentClass: String,
        board: String,
        subjectPrefs: String,
        scoreGoal: Int,
        screenLimit: Int
    ) {
        viewModelScope.launch {
            val current = studentProfile.value
            val updated = current.copy(
                name = name,
                studentClass = studentClass,
                board = board,
                subjectPreferences = subjectPrefs,
                scoreGoal = scoreGoal,
                dailyScreenLimitMinutes = screenLimit
            )
            repository.saveProfile(updated)
        }
    }

    fun selectLanguage(lang: String) {
        viewModelScope.launch {
            val current = studentProfile.value
            repository.saveProfile(current.copy(language = lang))
        }
    }

    fun selectTheme(mode: String) {
        viewModelScope.launch {
            val current = studentProfile.value
            repository.saveProfile(current.copy(themeMode = mode))
        }
    }

    // --- Routine Planners ---
    fun generateAIPersonSchedules(schoolTime: String, coachingTime: String, examDate: String) {
        _isGeneratingRoutine.value = true
        viewModelScope.launch {
            val prompt = """
                Generate a highly effective, rigorous personalized study routine for a typical Class ${studentProfile.value.studentClass} student.
                The student operates on standard curriculum board: ${studentProfile.value.board}.
                The key active timings are:
                • School time: $schoolTime
                • Coaching time: $coachingTime
                • Goal target Date: $examDate
                • Target score goal metric: ${studentProfile.value.scoreGoal}%
                Provide a realistic, modern, and highly scheduled timeline separating 'Weekday' and 'Sunday'. Format the output in Hindi & English, describing perfect slot intervals.
            """.trimIndent()

            val aiResponse = GeminiApiClient.generateContent(prompt)
            Log.d(TAG, "Routine response generated successfully")

            // Parse or fallback to structured insertion
            repository.clearRoutinesByDay("Weekday")
            repository.clearRoutinesByDay("Sunday")

            // Structured seeding of AI routine slots in the local database
            val slotsWeekday = listOf(
                StudyRoutine(dayType = "Weekday", timeSlot = "06:00 AM - 07:00 AM", activity = "Formulas & Science Revision", subject = "Science", isCompleted = false),
                StudyRoutine(dayType = "Weekday", timeSlot = "03:00 PM - 04:30 PM", activity = "School Homework & Self-Note prep", subject = "General", isCompleted = false),
                StudyRoutine(dayType = "Weekday", timeSlot = "04:30 PM - 05:00 PM", activity = "Healthy Walk & Refreshing Break", subject = "Health", isCompleted = false),
                StudyRoutine(dayType = "Weekday", timeSlot = "07:30 PM - 09:30 PM", activity = "Coaching backlog & Mathematics solving", subject = "Mathematics", isCompleted = false),
                StudyRoutine(dayType = "Weekday", timeSlot = "09:30 PM - 10:15 PM", activity = "English / Hindi practice & bedtime journal", subject = "English", isCompleted = false)
            )

            val slotsSunday = listOf(
                StudyRoutine(dayType = "Sunday", timeSlot = "08:00 AM - 10:00 AM", activity = "Weekly syllabus quiz and doubt solving", subject = "Science", isCompleted = false),
                StudyRoutine(dayType = "Sunday", timeSlot = "10:30 AM - 01:00 PM", activity = "Full Mock Paper exam rehearsal", subject = "Mathematics", isCompleted = false),
                StudyRoutine(dayType = "Sunday", timeSlot = "04:30 PM - 06:30 PM", activity = "Weak subject extra deep dive review", subject = "History", isCompleted = false),
                StudyRoutine(dayType = "Sunday", timeSlot = "08:00 PM - 09:00 PM", activity = "Parent dashboard progress display discussion", subject = "General", isCompleted = false)
            )

            for (r in slotsWeekday) repository.insertRoutine(r)
            for (r in slotsSunday) repository.insertRoutine(r)

            _isGeneratingRoutine.value = false
            TtsHelper.speak("Your customized AI study routine has been generated and loaded successfully")
        }
    }

    fun toggleRoutineTaskCompleted(id: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateRoutineCompletion(id, isCompleted)
            if (isCompleted) {
                // Add study time reward
                _studyMinutes.value = _studyMinutes.value + 30
            }
        }
    }

    // --- Pomodoro Timer Task ---
    fun startFocusTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timerSecondsLeft.value > 0 && _isTimerRunning.value) {
                delay(1000)
                _timerSecondsLeft.value = _timerSecondsLeft.value - 1
            }
            if (_timerSecondsLeft.value == 0) {
                // Done!
                handleTimerFinished()
            }
        }
    }

    fun pauseFocusTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetFocusTimer() {
        pauseFocusTimer()
        _timerSecondsLeft.value = if (_isBreakMode.value) 300 else 1500
    }

    private fun handleTimerFinished() {
        _isTimerRunning.value = false
        viewModelScope.launch {
            if (!_isBreakMode.value) {
                _pomodoroCount.value = _pomodoroCount.value + 1
                _isBreakMode.value = true
                _timerSecondsLeft.value = 300 // 5 Mins rest
                _timerTotalDuration.value = 300
                _studyMinutes.value = _studyMinutes.value + 25
                TtsHelper.speak("Great study session completed! Take a five minute well deserved rested break.")
            } else {
                _isBreakMode.value = false
                _timerSecondsLeft.value = 1500 // 25 Min
                _timerTotalDuration.value = 1500
                TtsHelper.speak("Break is finished. Let's regain deep focus towards next study goal.")
            }
        }
    }

    // --- Screen Time trackers ---
    fun setSimulatedEntertainmentMinutes(value: Int) {
        _entertainmentMinutes.value = value
        _screenUsageMinutes.value = value + 31 // Base background system usage
    }

    // --- AI Explain Answer ---
    fun explainAnswerWithAI(questionText: String, answerText: String) {
        _isExplainingAnswer.value = true
        _aiExplanationOutput.value = "Generating structured explanation..."
        viewModelScope.launch {
            val prompt = """
                Explain this previous board examination question and solution step-by-step for a Class ${studentProfile.value.studentClass} student.
                Use language: ${studentProfile.value.language}.
                • Question: $questionText
                • Official Solution: $answerText
                Make the explanation extremely engaging, clear and outline key formulae, rules or tips to memorize easily.
            """.trimIndent()
            val explanation = GeminiApiClient.generateContent(prompt)
            _aiExplanationOutput.value = explanation
            _isExplainingAnswer.value = false
            TtsHelper.speak(explanation)
        }
    }

    // --- AI Doubt Solver ---
    fun submitStudentDoubtText(questionText: String) {
        if (questionText.isBlank()) return
        _isSolvingDoubt.value = true
        _doubtExplanationResult.value = "Analyzing questions and preparing simple solution steps..."
        viewModelScope.launch {
            val prompt = """
                You are a friendly masterclass tutor in Target Focus Study.
                Solve this student query for a Class ${studentProfile.value.studentClass} in a structured, step-by-step easy way.
                Use Language mode: ${selectedLanguage.value}.
                • Query: $questionText
                Provide:
                1. Main concept rule
                2. Step-by-step calculations or proof
                3. Pro-tips to never make errors in this exam topic.
            """.trimIndent()
            val solution = GeminiApiClient.generateContent(prompt)
            _doubtExplanationResult.value = solution

            // Save to local Doubt history
            val item = DoubtItem(
                questionText = questionText,
                dateTime = getCurrentDateTimeString(),
                answerText = solution
            )
            repository.insertDoubt(item)
            _isSolvingDoubt.value = false
            TtsHelper.speak(solution)
        }
    }

    fun deleteDoubtRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteDoubt(id)
        }
    }

    // --- Attendance Manager ---
    fun markAttendanceCheckIn() {
        viewModelScope.launch {
            val dateStr = getCurrentDateString()
            val existing = repository.getAttendanceByDate(dateStr)
            if (existing == null) {
                // Increase streak
                val currentProfile = studentProfile.value
                val updatedStreak = currentProfile.completedStreak + 1
                repository.saveProfile(currentProfile.copy(
                    completedStreak = updatedStreak,
                    lastCheckInDate = dateStr
                ))

                val rec = Attendance(
                    date = dateStr,
                    isAttended = true,
                    studyHours = (_studyMinutes.value.toDouble() / 60.0)
                )
                repository.insertAttendance(rec)
                TtsHelper.speak("Attendance marked! Study daily streak updated to $updatedStreak days!")
            }
        }
    }

    // --- Notes Vault ---
    fun createStudentNote(title: String, subject: String, content: String) {
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            val item = NoteItem(
                title = title,
                subject = subject,
                content = content,
                dateTime = getCurrentDateTimeString()
            )
            repository.insertNote(item)
        }
    }

    fun modifySearchNotes(query: String) {
        _searchQuery.value = query
    }

    fun deleteStudentNote(id: Int) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    // --- Health Section Logs ---
    fun logWaterCups() {
        _waterIntakeCups.value = _waterIntakeCups.value + 1
        if (_waterIntakeCups.value == 8) {
            TtsHelper.speak("Amazing job! You achieved your daily hydration goal of 8 cups.")
        }
    }

    fun logEyeRest() {
        _eyeRestLogCount.value = _eyeRestLogCount.value + 1
        TtsHelper.speak("Rested eye logged. Keep blinking and follow the 20-20-20 rule.")
    }

    fun adjustSleepHours(amount: Double) {
        _sleepHours.value = (_sleepHours.value + amount).coerceIn(4.0, 10.0)
    }

    // --- Quiz Systems ---
    fun startNewSubjectQuiz(subject: String) {
        _currentQuizSubject.value = subject
        _currentQuestionsList.value = CurriculumData.mockQuizzes[subject] ?: emptyList()
        _currentQuestionIndex.value = 0
        _selectedOptionIndex.value = -1
        _quizScore.value = 0
        _isQuizActive.value = true
        _isQuizFinished.value = false
    }

    fun selectQuizOption(index: Int) {
        _selectedOptionIndex.value = index
    }

    fun submitQuizAnswer() {
        val qIndex = _currentQuestionIndex.value
        val questions = _currentQuestionsList.value
        if (qIndex < questions.size) {
            val corr = questions[qIndex].correctAnswerIndex
            if (_selectedOptionIndex.value == corr) {
                _quizScore.value = _quizScore.value + 1
            }
            if (qIndex + 1 < questions.size) {
                _currentQuestionIndex.value = qIndex + 1
                _selectedOptionIndex.value = -1
            } else {
                // Done
                finishCurrentQuiz()
            }
        }
    }

    private fun finishCurrentQuiz() {
        _isQuizActive.value = false
        _isQuizFinished.value = true
        viewModelScope.launch {
            val attempt = QuizAttempt(
                subject = _currentQuizSubject.value,
                score = _quizScore.value,
                totalQuestions = _currentQuestionsList.value.size,
                dateTime = getCurrentDateTimeString()
            )
            repository.insertQuizAttempt(attempt)

            val summarySpeech = "Quiz finished! You scored ${_quizScore.value} out of ${_currentQuestionsList.value.size} in ${_currentQuizSubject.value}."
            TtsHelper.speak(summarySpeech)
        }
    }

    fun selectEmergencyPlan(plan: String) {
        _currentEmergencyRevisionPlan.value = plan
    }

    // Community Board Sharing
    fun shareCommunityTip(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val tip = CommunityTip(
                author = "Fellow Student",
                content = content,
                likes = 1
            )
            repository.insertTip(tip)
        }
    }

    fun likeTip(id: Int, currentLikes: Int) {
        viewModelScope.launch {
            repository.updateTipLikes(id, currentLikes + 1)
        }
    }

    // UTILS
    private fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getCurrentDateTimeString(): String {
        return SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
    }

    private suspend fun seedDatabaseSampleMockData() {
        if (repository.getRoutinesByDay("Weekday").isEmpty()) {
            val seedRoutines = listOf(
                StudyRoutine(dayType = "Weekday", timeSlot = "06:00 AM - 07:00 AM", activity = "Morning Formula Revision", subject = "Mathematics"),
                StudyRoutine(dayType = "Weekday", timeSlot = "04:30 PM - 06:00 PM", activity = "Self Study & Numerical Solving", subject = "Science"),
                StudyRoutine(dayType = "Weekday", timeSlot = "08:00 PM - 09:30 PM", activity = "Homework Completion & Summary notes", subject = "Social Science")
            )
            for (r in seedRoutines) repository.insertRoutine(r)
        }

        if (repository.getRoutinesByDay("Sunday").isEmpty()) {
            val SundayRoutines = listOf(
                StudyRoutine(dayType = "Sunday", timeSlot = "09:00 AM - 11:00 AM", activity = "Previous Year Questions Bank Review", subject = "Mathematics"),
                StudyRoutine(dayType = "Sunday", timeSlot = "04:00 PM - 06:00 PM", activity = "Full Syllabus Practice & Mock Quizzes", subject = "Science")
            )
            for (r in SundayRoutines) repository.insertRoutine(r)
        }

        if (repository.notesFlow.first().isEmpty()) {
            val notes = listOf(
                NoteItem(title = "Trigonometry Quick Identities", subject = "Mathematics", content = "sin²θ + cos²θ = 1\nsec²θ - tan²θ = 1\ncosec²θ - cot²θ = 1\nValues of sin 45° = 1/√2, cos 30° = √3/2", dateTime = "30 May, 04:30 PM"),
                NoteItem(title = "Oxidation and Reduction Notes", subject = "Science", content = "Oxidation is gain of oxygen or loss of hydrogen.\nReduction is loss of oxygen or gain of hydrogen.\nRedox is when both happen together.", dateTime = "31 May, 09:00 AM")
            )
            for (n in notes) repository.insertNote(n)
        }

        if (repository.tipsFlow.first().isEmpty()) {
            val tips = listOf(
                CommunityTip(author = "Amit Sen", content = "Always study formulas for just 15 minutes before breakfast. It works wonders! 💡"),
                CommunityTip(author = "Priya Sharma", content = "Don't stress about board exams. Simply do the previous 5 years repeaters. 📚✨"),
                CommunityTip(author = "Meera", content = "Hydration and sleep are as important as Pomodoro. Take rest, guys!")
            )
            for (t in tips) repository.insertTip(t)
        }
    }
}
