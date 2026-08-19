package com.example.data.repository

import com.example.algeria.AlgerianCurriculum
import com.example.data.database.QiraatiDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class QiraatiRepository(private val database: QiraatiDatabase) {

    private val studentDao = database.studentDao()
    private val subjectDao = database.subjectDao()
    private val timetableDao = database.timetableDao()
    private val homeworkDao = database.homeworkDao()
    private val examDao = database.examDao()
    private val backpackDao = database.backpackDao()
    private val revisionDao = database.revisionDao()
    private val notificationDao = database.notificationDao()

    // Students
    val allStudents: Flow<List<StudentEntity>> = studentDao.getAllStudents()

    suspend fun getActiveStudent(): StudentEntity? {
        val default = studentDao.getDefaultStudent()
        if (default != null) return default
        return allStudents.firstOrNull()?.firstOrNull()
    }

    suspend fun addStudent(
        name: String,
        levelCode: String,
        stream: String = "NONE",
        schoolName: String = "",
        avatarEmoji: String = "👦🏻",
        colorHex: String = "#00695C"
    ): Long {
        val count = allStudents.first().size
        val isFirst = count == 0
        val student = StudentEntity(
            name = name,
            levelCode = levelCode,
            stream = stream,
            schoolName = schoolName,
            avatarEmoji = avatarEmoji,
            colorHex = colorHex,
            isDefault = isFirst
        )
        val id = studentDao.insertStudent(student)
        
        // Add default daily backpack items
        AlgerianCurriculum.DEFAULT_BACKPACK_ESSENTIALS.forEach { item ->
            backpackDao.insertBackpackItem(
                BackpackItemEntity(
                    studentId = id,
                    itemName = item,
                    category = "TOOL",
                    isDefaultDaily = true,
                    isPacked = false
                )
            )
        }
        return id
    }

    suspend fun updateStudent(student: StudentEntity) = studentDao.updateStudent(student)

    suspend fun selectStudent(studentId: Long) {
        studentDao.clearDefaultStudent()
        studentDao.setDefaultStudent(studentId)
    }

    suspend fun deleteStudent(student: StudentEntity) = studentDao.deleteStudent(student)

    // Subjects
    val allSubjects: Flow<List<SubjectEntity>> = subjectDao.getAllSubjects()

    suspend fun getSubjectById(id: Long) = subjectDao.getSubjectById(id)

    suspend fun updateSubject(subject: SubjectEntity) = subjectDao.updateSubject(subject)

    suspend fun addSubject(subject: SubjectEntity) = subjectDao.insertSubject(subject)

    // Timetable
    fun getTimetableForStudent(studentId: Long): Flow<List<TimetableSlotWithSubject>> {
        return timetableDao.getTimetableForStudent(studentId)
    }

    fun getSlotsForDay(studentId: Long, dayOfWeek: Int): Flow<List<TimetableSlotWithSubject>> {
        return timetableDao.getSlotsForDay(studentId, dayOfWeek)
    }

    suspend fun addTimetableSlot(slot: TimetableSlotEntity) = timetableDao.insertSlot(slot)

    suspend fun addTimetableSlots(slots: List<TimetableSlotEntity>) = timetableDao.insertSlots(slots)

    suspend fun updateTimetableSlot(slot: TimetableSlotEntity) = timetableDao.updateSlot(slot)

    suspend fun deleteTimetableSlot(slotId: Long) = timetableDao.deleteSlotById(slotId)

    suspend fun clearTimetable(studentId: Long) = timetableDao.clearTimetableForStudent(studentId)

    // Homework
    fun getHomeworkForStudent(studentId: Long): Flow<List<HomeworkWithSubject>> {
        return homeworkDao.getHomeworkForStudent(studentId)
    }

    fun getPendingHomeworkForStudent(studentId: Long): Flow<List<HomeworkWithSubject>> {
        return homeworkDao.getPendingHomeworkForStudent(studentId)
    }

    suspend fun addHomework(homework: HomeworkEntity) = homeworkDao.insertHomework(homework)

    suspend fun updateHomework(homework: HomeworkEntity) = homeworkDao.updateHomework(homework)

    suspend fun toggleHomeworkCompletion(homeworkId: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        homeworkDao.setHomeworkCompletion(homeworkId, isCompleted, completedAt)
    }

    suspend fun deleteHomework(homeworkId: Long) = homeworkDao.deleteHomeworkById(homeworkId)

    // Exams
    fun getExamsForStudent(studentId: Long): Flow<List<ExamWithSubject>> {
        return examDao.getExamsForStudent(studentId)
    }

    fun getUpcomingExams(studentId: Long): Flow<List<ExamWithSubject>> {
        val now = System.currentTimeMillis()
        return examDao.getUpcomingExamsForStudent(studentId, now)
    }

    suspend fun addExam(exam: ExamEntity) = examDao.insertExam(exam)

    suspend fun updateExam(exam: ExamEntity) = examDao.updateExam(exam)

    suspend fun updateExamRevisionStatus(examId: Long, status: RevisionStatus) {
        examDao.updateRevisionStatus(examId, status.name)
    }

    suspend fun deleteExam(examId: Long) = examDao.deleteExamById(examId)

    // Backpack Items
    fun getBackpackItems(studentId: Long): Flow<List<BackpackItemEntity>> {
        return backpackDao.getBackpackItems(studentId)
    }

    suspend fun addBackpackItem(item: BackpackItemEntity) = backpackDao.insertBackpackItem(item)

    suspend fun toggleBackpackItem(itemId: Long, isPacked: Boolean) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        backpackDao.setItemPacked(itemId, isPacked, if (isPacked) dateStr else "")
    }

    suspend fun markAllBackpackPacked(studentId: Long, isPacked: Boolean) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        backpackDao.setAllPackedForStudent(studentId, isPacked, if (isPacked) dateStr else "")
    }

    suspend fun deleteBackpackItem(itemId: Long) = backpackDao.deleteBackpackItemById(itemId)

    // Revision Sessions
    fun getRevisionSessions(studentId: Long): Flow<List<RevisionWithSubject>> {
        return revisionDao.getRevisionSessions(studentId)
    }

    suspend fun addRevisionSession(session: RevisionSessionEntity) = revisionDao.insertSession(session)

    suspend fun toggleRevisionSession(sessionId: Long, isCompleted: Boolean) {
        revisionDao.setSessionCompleted(sessionId, isCompleted)
    }

    suspend fun deleteRevisionSession(sessionId: Long) = revisionDao.deleteSessionById(sessionId)

    // Notifications
    fun getNotifications(studentId: Long): Flow<List<AppNotificationEntity>> {
        return notificationDao.getNotificationsForStudent(studentId)
    }

    suspend fun addNotification(notif: AppNotificationEntity) = notificationDao.insertNotification(notif)

    suspend fun markAllNotificationsAsRead(studentId: Long) = notificationDao.markAllAsRead(studentId)

    // 🎒 Signature Feature: Tomorrow Preparation Generator
    fun getTomorrowPreparationSummary(studentId: Long): Flow<TomorrowPreparationSummary> {
        val calendar = Calendar.getInstance()
        // Today's day index: Calendar.SUNDAY=1 ... SATURDAY=7
        val todayIndex = calendar.get(Calendar.DAY_OF_WEEK)
        val tomorrowDay = AlgerianDayOfWeek.getTomorrow(todayIndex)

        // Tomorrow calendar timestamp for formatting
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val arabicDateFormatter = SimpleDateFormat("EEEE d MMMM", Locale("ar"))
        val dateFormatted = arabicDateFormatter.format(calendar.time)

        val tomorrowStartMillis = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        val tomorrowEndMillis = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis

        val threeDaysLaterMillis = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 3)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
        }.timeInMillis

        return combine(
            timetableDao.getSlotsForDay(studentId, tomorrowDay.dayIndex),
            homeworkDao.getPendingHomeworkForStudent(studentId),
            examDao.getUpcomingExamsForStudent(studentId, System.currentTimeMillis()),
            backpackDao.getBackpackItems(studentId)
        ) { slots, homeworks, exams, backpackItems ->

            // Extract required materials grouped by distinct subject
            val requiredMaterials = mutableListOf<Pair<SubjectEntity, List<String>>>()
            val specialAlerts = mutableListOf<String>()

            val subjectsInTomorrow = slots.mapNotNull { it.subject }.distinctBy { it.id }
            subjectsInTomorrow.forEach { subject ->
                val itemsList = subject.defaultItemsAr.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                requiredMaterials.add(Pair(subject, itemsList))

                // Detect special alerts based on subject
                when {
                    subject.nameAr.contains("بدنية") || subject.nameFr.contains("Physique (EPS)", ignoreCase = true) -> {
                        specialAlerts.add("🏃 مادة التربية البدنية: تجهيز البدلة الرياضية، الحذاء وقارورة الماء.")
                    }
                    subject.nameAr.contains("علوم") || subject.nameAr.contains("فيزياء") -> {
                        specialAlerts.add("🔬 مادة ${subject.nameAr}: مئزر أبيض قطني إجباري في المخبر.")
                    }
                    subject.nameAr.contains("رسم") || subject.nameAr.contains("تشكيلية") -> {
                        specialAlerts.add("🎨 مادة التربية التشكيلية: كراس الرسم والألوان المائية/الخشبية.")
                    }
                    subject.nameAr.contains("رياضيات") -> {
                        specialAlerts.add("📐 مادة الرياضيات: علبة أدوات الهندسة (كوس، منقلة، مدور) والآلة الحاسبة.")
                    }
                }
            }

            // Homework due by tomorrow
            val dueHomework = homeworks.filter {
                it.homework.dueDateMillis in (tomorrowStartMillis - 86400000L)..tomorrowEndMillis
            }

            // Exams within next 3 days
            val upcomingExams = exams.filter {
                it.exam.examDateMillis in tomorrowStartMillis..threeDaysLaterMillis
            }

            val packedCount = backpackItems.count { it.isPacked }
            val totalCount = backpackItems.size
            val isReady = totalCount > 0 && packedCount == totalCount

            TomorrowPreparationSummary(
                targetDay = tomorrowDay,
                dayLabelAr = tomorrowDay.nameAr,
                dayLabelFr = tomorrowDay.nameFr,
                dateFormatted = dateFormatted,
                slots = slots,
                requiredMaterialsBySubject = requiredMaterials,
                specialSuppliesAlerts = specialAlerts,
                dueHomeworkList = dueHomework,
                upcomingExamsList = upcomingExams,
                dailyBackpackItems = backpackItems,
                isBackpackReady = isReady,
                totalItemsCount = totalCount,
                packedItemsCount = packedCount
            )
        }
    }

    // Algerian Preset Timetable Generator for fast setup
    suspend fun applyAlgerianTemplate(studentId: Long, levelCode: String) {
        timetableDao.clearTimetableForStudent(studentId)
        val allSubs = subjectDao.getAllSubjects().first()
        val subMap = allSubs.associateBy { it.id }

        // Create standard schedule
        val slots = mutableListOf<TimetableSlotEntity>()

        // Helper
        fun add(day: Int, period: Int, start: String, end: String, subId: Long, room: String = "") {
            if (subMap.containsKey(subId)) {
                slots.add(
                    TimetableSlotEntity(
                        studentId = studentId,
                        dayOfWeek = day,
                        periodOrder = period,
                        startTime = start,
                        endTime = end,
                        subjectId = subId,
                        roomOrTeacher = room
                    )
                )
            }
        }

        when {
            levelCode.startsWith("1A") || levelCode.startsWith("2A") || levelCode.startsWith("3A") && levelCode.contains("P") -> {
                // Primary School Template
                listOf(1, 2, 3, 4, 5).forEach { day ->
                    add(day, 1, "08:00", "09:15", 1) // عربية
                    add(day, 2, "09:30", "10:45", 2) // رياضيات
                    add(day, 3, "11:00", "12:00", 3) // إسلامية
                    if (day != 3) { // Tuesday afternoon off in primary
                        add(day, 4, "13:30", "14:30", if (day % 2 == 0) 4 else 8) // فرنسية أو اجتماعيات
                        add(day, 5, "14:30", "15:30", if (day == 5) 10 else 11) // رياضة أو رسم
                    }
                }
            }
            else -> {
                // Middle / Secondary standard Algerian weekly timetable
                // Sunday
                add(1, 1, "08:00", "09:00", 2, "قاعة 4")
                add(1, 2, "09:00", "10:00", 1, "قاعة 4")
                add(1, 3, "10:15", "11:15", 6, "مخبر 1")
                add(1, 4, "11:15", "12:15", 3, "قاعة 4")
                add(1, 5, "13:30", "14:30", 4, "قاعة 4")
                add(1, 6, "14:30", "15:30", 8, "قاعة 4")

                // Monday
                add(2, 1, "08:00", "09:00", 7, "مخبر فيزياء")
                add(2, 2, "09:00", "10:00", 2, "قاعة 4")
                add(2, 3, "10:15", "11:15", 5, "قاعة 4")
                add(2, 4, "11:15", "12:15", 9, "قاعة 4")
                add(2, 5, "13:30", "15:30", 10, "الملعب")

                // Tuesday
                add(3, 1, "08:00", "09:00", 1, "قاعة 4")
                add(3, 2, "09:00", "10:00", 4, "قاعة 4")
                add(3, 3, "10:15", "11:15", 6, "مخبر 1")
                add(3, 4, "11:15", "12:15", 11, "ورشة فنون")

                // Wednesday
                add(4, 1, "08:00", "09:00", 2, "قاعة 4")
                add(4, 2, "09:00", "10:00", 7, "مخبر فيزياء")
                add(4, 3, "10:15", "11:15", 1, "قاعة 4")
                add(4, 4, "11:15", "12:15", 8, "قاعة 4")
                add(4, 5, "13:30", "14:30", 5, "قاعة 4")

                // Thursday
                add(5, 1, "08:00", "09:00", 2, "قاعة 4")
                add(5, 2, "09:00", "10:00", 4, "قاعة 4")
                add(5, 3, "10:15", "11:15", 6, "مخبر 1")
                add(5, 4, "11:15", "12:15", 10, "الملعب")
            }
        }
        timetableDao.insertSlots(slots)
    }
}
