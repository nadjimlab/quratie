package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TimetableSlotWithSubject(
    @Embedded val slot: TimetableSlotEntity,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "id"
    )
    val subject: SubjectEntity?
)

data class HomeworkWithSubject(
    @Embedded val homework: HomeworkEntity,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "id"
    )
    val subject: SubjectEntity?
)

data class ExamWithSubject(
    @Embedded val exam: ExamEntity,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "id"
    )
    val subject: SubjectEntity?
)

data class RevisionWithSubject(
    @Embedded val revision: RevisionSessionEntity,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "id"
    )
    val subject: SubjectEntity?
)

data class StudentWithFullProfile(
    @Embedded val student: StudentEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "studentId"
    )
    val timetableSlots: List<TimetableSlotEntity> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "studentId"
    )
    val homeworks: List<HomeworkEntity> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "studentId"
    )
    val exams: List<ExamEntity> = emptyList()
)

data class TomorrowPreparationSummary(
    val targetDay: AlgerianDayOfWeek,
    val dayLabelAr: String,
    val dayLabelFr: String,
    val dateFormatted: String,
    val slots: List<TimetableSlotWithSubject>,
    val requiredMaterialsBySubject: List<Pair<SubjectEntity, List<String>>>,
    val specialSuppliesAlerts: List<String>, // e.g. "🏃 بدلة رياضية وحذاء", "📐 أدوات الهندسة", "🔬 مئزر أبيض"
    val dueHomeworkList: List<HomeworkWithSubject>,
    val upcomingExamsList: List<ExamWithSubject>,
    val dailyBackpackItems: List<BackpackItemEntity>,
    val isBackpackReady: Boolean,
    val totalItemsCount: Int,
    val packedItemsCount: Int
)

data class AlgerianHoliday(
    val nameAr: String,
    val nameFr: String,
    val startDate: String,
    val endDate: String,
    val seasonBadge: String,
    val iconEmoji: String
)
