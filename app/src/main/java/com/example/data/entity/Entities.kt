package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Rahul Kumar",
    val studentClass: String = "Class 10",
    val board: String = "CBSE",
    val subjectPreferences: String = "Maths, Science",
    val language: String = "English", // "English" or "Hindi"
    val themeMode: String = "Dark", // "Dark" or "Light"
    val scoreGoal: Int = 95,
    val dailyScreenLimitMinutes: Int = 120,
    val completedStreak: Int = 5,
    val lastCheckInDate: String = "2026-05-30"
)

@Entity(tableName = "study_routines")
data class StudyRoutine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayType: String = "Weekday", // "Weekday" or "Sunday"
    val timeSlot: String,
    val activity: String,
    val subject: String = "General",
    val isCompleted: Boolean = false
)

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey val date: String, // "yyyy-MM-dd"
    val isAttended: Boolean = true,
    val studyHours: Double = 4.5
)

@Entity(tableName = "doubts")
data class DoubtItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionText: String,
    val dateTime: String,
    val answerText: String,
    val imageUri: String? = null
)

@Entity(tableName = "notes")
data class NoteItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val content: String,
    val dateTime: String
)

@Entity(tableName = "community_tips")
data class CommunityTip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val author: String,
    val content: String,
    val likes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_attempts")
data class QuizAttempt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val score: Int,
    val totalQuestions: Int,
    val dateTime: String
)
