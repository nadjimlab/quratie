package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val levelCode: String, // e.g. "3AM", "4AM", "3AS"
    val stream: String = "NONE", // SecondaryStream name
    val schoolName: String = "",
    val avatarEmoji: String = "🎓",
    val colorHex: String = "#00695C",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameAr: String,
    val nameFr: String,
    val shortCode: String,
    val colorHex: String,
    val iconName: String,
    val defaultItemsAr: String, // Comma separated, e.g. "كتاب الرياضيات,كراس 96 صفحة,أدوات الهندسة,آلة حاسبة"
    val coefficient: Double = 2.0,
    val applicableCycles: String = "PRIMARY,MIDDLE,SECONDARY" // Comma separated cycles
)

@Entity(
    tableName = "timetable_slots",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"]), Index(value = ["subjectId"])]
)
data class TimetableSlotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val dayOfWeek: Int, // 1 for Sunday to 7 for Saturday (Algerian school week)
    val periodOrder: Int, // 1, 2, 3, 4 (Morning) / 5, 6, 7, 8 (Afternoon)
    val startTime: String, // e.g. "08:00"
    val endTime: String,   // e.g. "09:00"
    val subjectId: Long,
    val roomOrTeacher: String = "",
    val customNotes: String = ""
)

@Entity(
    tableName = "homework",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"]), Index(value = ["subjectId"])]
)
data class HomeworkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val subjectId: Long,
    val title: String,
    val description: String = "",
    val dueDateMillis: Long,
    val priority: String = "MEDIUM", // Priority enum name
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val reminderTimeMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "exams",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"]), Index(value = ["subjectId"])]
)
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val subjectId: Long,
    val title: String,
    val examType: String = "TEST_1", // ExamType enum name
    val examDateMillis: Long,
    val durationMinutes: Int = 60,
    val syllabusTopics: String = "",
    val targetGrade: Double? = null,
    val revisionStatus: String = "NOT_STARTED", // RevisionStatus enum name
    val notes: String = ""
)

@Entity(
    tableName = "backpack_items",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"])]
)
data class BackpackItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val itemName: String,
    val category: String = "TOOL", // BackpackCategory enum name
    val isDefaultDaily: Boolean = true, // e.g. Pencil case, ID badge, Water bottle
    val targetDayOfWeek: Int? = null, // null for every day, or specific day 1..7
    val isPacked: Boolean = false,
    val lastPackedDate: String = "" // "YYYY-MM-DD"
)

@Entity(
    tableName = "revision_sessions",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"]), Index(value = ["subjectId"])]
)
data class RevisionSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val subjectId: Long,
    val title: String,
    val scheduledDateMillis: Long,
    val durationMinutes: Int = 45,
    val isCompleted: Boolean = false,
    val notes: String = ""
)

@Entity(
    tableName = "app_notifications",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"])]
)
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val title: String,
    val message: String,
    val type: String = "BACKPACK", // BACKPACK, HOMEWORK, EXAM, REVISION, HOLIDAY
    val timestampMillis: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
