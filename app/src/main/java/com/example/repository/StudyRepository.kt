package com.example.repository

import com.example.data.dao.StudyDao
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

class StudyRepository(private val studyDao: StudyDao) {

    val profileFlow: Flow<StudentProfile?> = studyDao.getProfileFlow()

    suspend fun getProfileSingle(): StudentProfile? = studyDao.getProfileSingle()

    suspend fun saveProfile(profile: StudentProfile) {
        studyDao.insertProfile(profile)
    }

    fun getRoutinesByDayFlow(dayType: String): Flow<List<StudyRoutine>> =
        studyDao.getRoutinesByDayFlow(dayType)

    suspend fun getRoutinesByDay(dayType: String): List<StudyRoutine> =
        studyDao.getRoutinesByDay(dayType)

    suspend fun insertRoutine(routine: StudyRoutine) {
        studyDao.insertRoutine(routine)
    }

    suspend fun clearRoutinesByDay(dayType: String) {
        studyDao.clearRoutinesByDay(dayType)
    }

    suspend fun updateRoutineCompletion(id: Int, isCompleted: Boolean) {
        studyDao.updateRoutineCompletion(id, isCompleted)
    }

    val attendanceFlow: Flow<List<Attendance>> = studyDao.getAllAttendanceFlow()

    suspend fun insertAttendance(attendance: Attendance) {
        studyDao.insertAttendance(attendance)
    }

    suspend fun getAttendanceByDate(date: String): Attendance? {
        return studyDao.getAttendanceByDate(date)
    }

    val doubtsFlow: Flow<List<DoubtItem>> = studyDao.getAllDoubtsFlow()

    suspend fun insertDoubt(doubt: DoubtItem) {
        studyDao.insertDoubt(doubt)
    }

    suspend fun deleteDoubt(id: Int) {
        studyDao.deleteDoubt(id)
    }

    val notesFlow: Flow<List<NoteItem>> = studyDao.getAllNotesFlow()

    fun searchNotes(query: String): Flow<List<NoteItem>> {
        return if (query.isBlank()) {
            studyDao.getAllNotesFlow()
        } else {
            studyDao.searchNotesFlow(query)
        }
    }

    suspend fun insertNote(note: NoteItem) {
        studyDao.insertNote(note)
    }

    suspend fun deleteNote(id: Int) {
        studyDao.deleteNote(id)
    }

    val tipsFlow: Flow<List<CommunityTip>> = studyDao.getAllTipsFlow()

    suspend fun insertTip(tip: CommunityTip) {
        studyDao.insertTip(tip)
    }

    suspend fun updateTipLikes(id: Int, likes: Int) {
        studyDao.updateTipLikes(id, likes)
    }

    val quizAttemptsFlow: Flow<List<QuizAttempt>> = studyDao.getAllQuizAttemptsFlow()

    suspend fun insertQuizAttempt(attempt: QuizAttempt) {
        studyDao.insertQuizAttempt(attempt)
    }
}
