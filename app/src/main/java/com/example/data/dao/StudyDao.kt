package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun getProfileFlow(): Flow<StudentProfile?>

    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileSingle(): StudentProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: StudentProfile)

    @Query("SELECT * FROM study_routines WHERE dayType = :dayType")
    fun getRoutinesByDayFlow(dayType: String): Flow<List<StudyRoutine>>

    @Query("SELECT * FROM study_routines WHERE dayType = :dayType")
    suspend fun getRoutinesByDay(dayType: String): List<StudyRoutine>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: StudyRoutine)

    @Query("DELETE FROM study_routines WHERE dayType = :dayType")
    suspend fun clearRoutinesByDay(dayType: String)

    @Query("UPDATE study_routines SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateRoutineCompletion(id: Int, isCompleted: Boolean)

    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendanceFlow(): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance WHERE date = :date LIMIT 1")
    suspend fun getAttendanceByDate(date: String): Attendance?

    @Query("SELECT * FROM doubts ORDER BY id DESC")
    fun getAllDoubtsFlow(): Flow<List<DoubtItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubt(doubt: DoubtItem)

    @Query("DELETE FROM doubts WHERE id = :id")
    suspend fun deleteDoubt(id: Int)

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR subject LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchNotesFlow(query: String): Flow<List<NoteItem>>

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotesFlow(): Flow<List<NoteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteItem)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Int)

    @Query("SELECT * FROM community_tips ORDER BY createdAt DESC")
    fun getAllTipsFlow(): Flow<List<CommunityTip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: CommunityTip)

    @Query("UPDATE community_tips SET likes = :likes WHERE id = :id")
    suspend fun updateTipLikes(id: Int, likes: Int)

    @Query("SELECT * FROM quiz_attempts ORDER BY id DESC")
    fun getAllQuizAttemptsFlow(): Flow<List<QuizAttempt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(attempt: QuizAttempt)
}
