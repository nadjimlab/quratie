package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY isDefault DESC, id ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    fun getStudentById(studentId: Long): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultStudent(): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("UPDATE students SET isDefault = 0")
    suspend fun clearDefaultStudent()

    @Query("UPDATE students SET isDefault = 1 WHERE id = :studentId")
    suspend fun setDefaultStudent(studentId: Long)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY nameAr ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Long): SubjectEntity?

    @Query("SELECT * FROM subjects WHERE nameAr LIKE '%' || :name || '%' OR nameFr LIKE '%' || :name || '%' LIMIT 1")
    suspend fun findSubjectByName(name: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectEntity)
}

@Dao
interface TimetableDao {
    @Transaction
    @Query("SELECT * FROM timetable_slots WHERE studentId = :studentId ORDER BY dayOfWeek ASC, periodOrder ASC")
    fun getTimetableForStudent(studentId: Long): Flow<List<TimetableSlotWithSubject>>

    @Transaction
    @Query("SELECT * FROM timetable_slots WHERE studentId = :studentId AND dayOfWeek = :dayOfWeek ORDER BY periodOrder ASC")
    fun getSlotsForDay(studentId: Long, dayOfWeek: Int): Flow<List<TimetableSlotWithSubject>>

    @Transaction
    @Query("SELECT * FROM timetable_slots WHERE studentId = :studentId AND dayOfWeek = :dayOfWeek ORDER BY periodOrder ASC")
    suspend fun getSlotsForDayDirect(studentId: Long, dayOfWeek: Int): List<TimetableSlotWithSubject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: TimetableSlotEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlots(slots: List<TimetableSlotEntity>)

    @Update
    suspend fun updateSlot(slot: TimetableSlotEntity)

    @Query("DELETE FROM timetable_slots WHERE id = :slotId")
    suspend fun deleteSlotById(slotId: Long)

    @Query("DELETE FROM timetable_slots WHERE studentId = :studentId")
    suspend fun clearTimetableForStudent(studentId: Long)
}

@Dao
interface HomeworkDao {
    @Transaction
    @Query("SELECT * FROM homework WHERE studentId = :studentId ORDER BY isCompleted ASC, dueDateMillis ASC")
    fun getHomeworkForStudent(studentId: Long): Flow<List<HomeworkWithSubject>>

    @Transaction
    @Query("SELECT * FROM homework WHERE studentId = :studentId AND isCompleted = 0 ORDER BY dueDateMillis ASC")
    fun getPendingHomeworkForStudent(studentId: Long): Flow<List<HomeworkWithSubject>>

    @Transaction
    @Query("SELECT * FROM homework WHERE studentId = :studentId AND dueDateMillis BETWEEN :startMillis AND :endMillis ORDER BY dueDateMillis ASC")
    fun getHomeworkBetweenDates(studentId: Long, startMillis: Long, endMillis: Long): Flow<List<HomeworkWithSubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: HomeworkEntity): Long

    @Update
    suspend fun updateHomework(homework: HomeworkEntity)

    @Query("UPDATE homework SET isCompleted = :isCompleted, completedAtMillis = :completedAt WHERE id = :homeworkId")
    suspend fun setHomeworkCompletion(homeworkId: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("DELETE FROM homework WHERE id = :homeworkId")
    suspend fun deleteHomeworkById(homeworkId: Long)
}

@Dao
interface ExamDao {
    @Transaction
    @Query("SELECT * FROM exams WHERE studentId = :studentId ORDER BY examDateMillis ASC")
    fun getExamsForStudent(studentId: Long): Flow<List<ExamWithSubject>>

    @Transaction
    @Query("SELECT * FROM exams WHERE studentId = :studentId AND examDateMillis >= :nowMillis ORDER BY examDateMillis ASC")
    fun getUpcomingExamsForStudent(studentId: Long, nowMillis: Long): Flow<List<ExamWithSubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Query("UPDATE exams SET revisionStatus = :status WHERE id = :examId")
    suspend fun updateRevisionStatus(examId: Long, status: String)

    @Query("DELETE FROM exams WHERE id = :examId")
    suspend fun deleteExamById(examId: Long)
}

@Dao
interface BackpackDao {
    @Query("SELECT * FROM backpack_items WHERE studentId = :studentId ORDER BY category ASC, id ASC")
    fun getBackpackItems(studentId: Long): Flow<List<BackpackItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackpackItem(item: BackpackItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackpackItems(items: List<BackpackItemEntity>)

    @Update
    suspend fun updateBackpackItem(item: BackpackItemEntity)

    @Query("UPDATE backpack_items SET isPacked = :isPacked, lastPackedDate = :packedDate WHERE id = :itemId")
    suspend fun setItemPacked(itemId: Long, isPacked: Boolean, packedDate: String)

    @Query("UPDATE backpack_items SET isPacked = :isPacked, lastPackedDate = :packedDate WHERE studentId = :studentId")
    suspend fun setAllPackedForStudent(studentId: Long, isPacked: Boolean, packedDate: String)

    @Query("DELETE FROM backpack_items WHERE id = :itemId")
    suspend fun deleteBackpackItemById(itemId: Long)
}

@Dao
interface RevisionDao {
    @Transaction
    @Query("SELECT * FROM revision_sessions WHERE studentId = :studentId ORDER BY scheduledDateMillis ASC")
    fun getRevisionSessions(studentId: Long): Flow<List<RevisionWithSubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: RevisionSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<RevisionSessionEntity>)

    @Update
    suspend fun updateSession(session: RevisionSessionEntity)

    @Query("UPDATE revision_sessions SET isCompleted = :isCompleted WHERE id = :sessionId")
    suspend fun setSessionCompleted(sessionId: Long, isCompleted: Boolean)

    @Query("DELETE FROM revision_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM app_notifications WHERE studentId = :studentId ORDER BY timestampMillis DESC")
    fun getNotificationsForStudent(studentId: Long): Flow<List<AppNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity): Long

    @Query("UPDATE app_notifications SET isRead = 1 WHERE studentId = :studentId")
    suspend fun markAllAsRead(studentId: Long)

    @Query("DELETE FROM app_notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)
}
