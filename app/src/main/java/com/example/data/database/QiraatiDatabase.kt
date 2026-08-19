package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.algeria.AlgerianCurriculum
import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudentEntity::class,
        SubjectEntity::class,
        TimetableSlotEntity::class,
        HomeworkEntity::class,
        ExamEntity::class,
        BackpackItemEntity::class,
        RevisionSessionEntity::class,
        AppNotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class QiraatiDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun subjectDao(): SubjectDao
    abstract fun timetableDao(): TimetableDao
    abstract fun homeworkDao(): HomeworkDao
    abstract fun examDao(): ExamDao
    abstract fun backpackDao(): BackpackDao
    abstract fun revisionDao(): RevisionDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: QiraatiDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): QiraatiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QiraatiDatabase::class.java,
                    "qiraati_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

suspend fun populateInitialData(database: QiraatiDatabase) {
    val subjectDao = database.subjectDao()
    val studentDao = database.studentDao()
    val timetableDao = database.timetableDao()
    val homeworkDao = database.homeworkDao()
    val examDao = database.examDao()
    val backpackDao = database.backpackDao()
    val notifDao = database.notificationDao()

    // Check if already populated
    if (studentDao.getDefaultStudent() != null) return

    // 1. Insert Subjects
    subjectDao.insertSubjects(AlgerianCurriculum.OFFICIAL_SUBJECTS)

    // 2. Insert Default Students (Mohammed & Sarah)
    val student1Id = studentDao.insertStudent(
        StudentEntity(
            name = "محمد",
            levelCode = "3AM",
            stream = "NONE",
            schoolName = "متوسطة الشهيد زيغود يوسف",
            avatarEmoji = "👦🏻",
            colorHex = "#00695C",
            isDefault = true
        )
    )

    val student2Id = studentDao.insertStudent(
        StudentEntity(
            name = "سارة",
            levelCode = "4AP",
            stream = "NONE",
            schoolName = "ابتدائية الأمير عبد القادر",
            avatarEmoji = "👧🏻",
            colorHex = "#9333EA",
            isDefault = false
        )
    )

    // 3. Populate Timetable for Mohammed (3AM - Middle School)
    // Sunday (1)
    timetableDao.insertSlots(
        listOf(
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 1, periodOrder = 1, startTime = "08:00", endTime = "09:00", subjectId = 2, roomOrTeacher = "أ. بلحاج - قاعة 4"), // رياضيات
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 1, periodOrder = 2, startTime = "09:00", endTime = "10:00", subjectId = 1, roomOrTeacher = "أ. بن عيسى - قاعة 4"), // عربية
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 1, periodOrder = 3, startTime = "10:15", endTime = "11:15", subjectId = 6, roomOrTeacher = "مخبر العلوم 1"), // علوم طبيعية
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 1, periodOrder = 4, startTime = "11:15", endTime = "12:15", subjectId = 3, roomOrTeacher = "قاعة 4"), // إسلامية
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 1, periodOrder = 5, startTime = "13:30", endTime = "14:30", subjectId = 4, roomOrTeacher = "أ. قادري"), // فرنسية
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 1, periodOrder = 6, startTime = "14:30", endTime = "15:30", subjectId = 8, roomOrTeacher = "قاعة 4") // تاريخ وجغرافيا
        )
    )

    // Monday (2)
    timetableDao.insertSlots(
        listOf(
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 2, periodOrder = 1, startTime = "08:00", endTime = "09:00", subjectId = 7, roomOrTeacher = "مخبر الفيزياء"), // فيزياء
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 2, periodOrder = 2, startTime = "09:00", endTime = "10:00", subjectId = 2, roomOrTeacher = "أ. بلحاج"), // رياضيات
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 2, periodOrder = 3, startTime = "10:15", endTime = "11:15", subjectId = 5, roomOrTeacher = "قاعة 4"), // إنجليزية
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 2, periodOrder = 4, startTime = "11:15", endTime = "12:15", subjectId = 9, roomOrTeacher = "قاعة 4"), // مدنية
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 2, periodOrder = 5, startTime = "13:30", endTime = "15:30", subjectId = 10, roomOrTeacher = "الملعب البلدي") // تربية بدنية (ساعتان)
        )
    )

    // Tuesday (3)
    timetableDao.insertSlots(
        listOf(
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 3, periodOrder = 1, startTime = "08:00", endTime = "09:00", subjectId = 1, roomOrTeacher = "قاعة 4"), // عربية
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 3, periodOrder = 2, startTime = "09:00", endTime = "10:00", subjectId = 4, roomOrTeacher = "قاعة 4"), // فرنسية
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 3, periodOrder = 3, startTime = "10:15", endTime = "11:15", subjectId = 6, roomOrTeacher = "مخبر 1"), // علوم
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 3, periodOrder = 4, startTime = "11:15", endTime = "12:15", subjectId = 11, roomOrTeacher = "ورشة الرسم") // فنون
        )
    )

    // Wednesday (4)
    timetableDao.insertSlots(
        listOf(
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 4, periodOrder = 1, startTime = "08:00", endTime = "09:00", subjectId = 2, roomOrTeacher = "قاعة 4"), // رياضيات
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 4, periodOrder = 2, startTime = "09:00", endTime = "10:00", subjectId = 7, roomOrTeacher = "مخبر الفيزياء"), // فيزياء
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 4, periodOrder = 3, startTime = "10:15", endTime = "11:15", subjectId = 1, roomOrTeacher = "قاعة 4"), // عربية
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 4, periodOrder = 4, startTime = "11:15", endTime = "12:15", subjectId = 8, roomOrTeacher = "قاعة 4"), // تاريخ وجغرافيا
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 4, periodOrder = 5, startTime = "13:30", endTime = "14:30", subjectId = 5, roomOrTeacher = "قاعة 4") // إنجليزية
        )
    )

    // Thursday (5)
    timetableDao.insertSlots(
        listOf(
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 5, periodOrder = 1, startTime = "08:00", endTime = "09:00", subjectId = 2, roomOrTeacher = "قاعة 4"), // رياضيات
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 5, periodOrder = 2, startTime = "09:00", endTime = "10:00", subjectId = 4, roomOrTeacher = "قاعة 4"), // فرنسية
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 5, periodOrder = 3, startTime = "10:15", endTime = "11:15", subjectId = 6, roomOrTeacher = "مخبر العلوم"), // علوم
            TimetableSlotEntity(studentId = student1Id, dayOfWeek = 5, periodOrder = 4, startTime = "11:15", endTime = "12:15", subjectId = 10, roomOrTeacher = "الملعب") // تربية بدنية
        )
    )

    // 4. Default Backpack Items for Mohammed
    AlgerianCurriculum.DEFAULT_BACKPACK_ESSENTIALS.forEach { item ->
        backpackDao.insertBackpackItem(
            BackpackItemEntity(
                studentId = student1Id,
                itemName = item,
                category = "TOOL",
                isDefaultDaily = true,
                isPacked = false
            )
        )
    }

    // 5. Initial Homework for Mohammed
    val now = System.currentTimeMillis()
    val dayMillis = 24 * 60 * 60 * 1000L

    homeworkDao.insertHomework(
        HomeworkEntity(
            studentId = student1Id,
            subjectId = 2, // رياضيات
            title = "حل التمرين 14 و 15 ص 42 (مبرهنة طالس)",
            description = "إحضار كراس التمارين وأدوات الهندسة كاملة للرسم الدقيق.",
            dueDateMillis = now + dayMillis,
            priority = "HIGH",
            isCompleted = false
        )
    )

    homeworkDao.insertHomework(
        HomeworkEntity(
            studentId = student1Id,
            subjectId = 4, // فرنسية
            title = "Production écrite : Le patrimoine algérien",
            description = "Écrire un paragraphe de 8 lignes sur Casbah d'Alger.",
            dueDateMillis = now + (2 * dayMillis),
            priority = "MEDIUM",
            isCompleted = false
        )
    )

    homeworkDao.insertHomework(
        HomeworkEntity(
            studentId = student1Id,
            subjectId = 6, // علوم
            title = "رسم مخطط الجهاز الهضمي للإنسان وتسمية الأعضاء",
            description = "استعمال الألوان الخشبية والتخطيط بدقة على كراس القسم.",
            dueDateMillis = now + (3 * dayMillis),
            priority = "LOW",
            isCompleted = true,
            completedAtMillis = now - 3600000L
        )
    )

    // 6. Upcoming Exams for Mohammed
    examDao.insertExam(
        ExamEntity(
            studentId = student1Id,
            subjectId = 2, // رياضيات
            title = "الفرض الأول للفصل الأول (حساب حرفي وطالس)",
            examType = "TEST_1",
            examDateMillis = now + (2 * dayMillis),
            durationMinutes = 60,
            syllabusTopics = "الأعداد الناطقة، نظرية طالس وحساب الأطوال، الحساب الحرفي",
            targetGrade = 18.0,
            revisionStatus = "IN_PROGRESS",
            notes = "إحضار الآلة الحاسبة وأدوات الهندسة ضروري."
        )
    )

    examDao.insertExam(
        ExamEntity(
            studentId = student1Id,
            subjectId = 6, // علوم
            title = "الفرض المحروس للعلوم الطبيعية",
            examType = "TEST_1",
            examDateMillis = now + (5 * dayMillis),
            durationMinutes = 60,
            syllabusTopics = "الهضم وامتصاص المغذيات، الدورة الدموية",
            targetGrade = 16.5,
            revisionStatus = "NOT_STARTED",
            notes = "التركيز على التجارب ومصطلحات الإنزيمات."
        )
    )

    // 7. Initial Notification
    notifDao.insertNotification(
        AppNotificationEntity(
            studentId = student1Id,
            title = "🎒 تذكير تجهيز محفظة الغد",
            message = "لدى محمد غداً مادة الرياضيات والتربية البدنية. لا تنس وضع بدلة الرياضة وأدوات الهندسة!",
            type = "BACKPACK",
            timestampMillis = now,
            isRead = false
        )
    )
}
