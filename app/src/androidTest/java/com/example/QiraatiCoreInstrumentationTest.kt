package com.example

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.database.QiraatiDatabase
import com.example.data.model.StudentEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.TimetableSlotEntity
import com.example.notifications.DailyReminderScheduler
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QiraatiCoreInstrumentationTest {
    private lateinit var database: QiraatiDatabase
    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(context, QiraatiDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun timetableInput_persistsSlotForStudent() = runBlocking {
        val studentId = database.studentDao().insertStudent(
            StudentEntity(name = "اختبار", levelCode = "3AM")
        )
        val subjectId = database.subjectDao().insertSubject(
            SubjectEntity(
                nameAr = "رياضيات",
                nameFr = "Mathématiques",
                shortCode = "MATH",
                colorHex = "#4F46E5",
                iconName = "calculate",
                defaultItemsAr = "كراس الرياضيات"
            )
        )

        database.timetableDao().insertSlot(
            TimetableSlotEntity(
                studentId = studentId,
                dayOfWeek = 1,
                periodOrder = 1,
                startTime = "08:00",
                endTime = "09:00",
                subjectId = subjectId
            )
        )

        val slots = database.timetableDao().getSlotsForDayDirect(studentId, 1)
        assertEquals(1, slots.size)
        assertEquals("رياضيات", slots.single().subject.nameAr)
    }

    @Test
    fun localNotifications_createDailyChannel() {
        DailyReminderScheduler.schedule(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            assertNotNull(manager.getNotificationChannel("daily_study_reminder"))
        }
        DailyReminderScheduler.cancel(context)
    }

    @Test
    fun studentProfile_persistsAndCanBeRead() = runBlocking {
        val studentId = database.studentDao().insertStudent(
            StudentEntity(
                name = "سارة",
                levelCode = "4AP",
                schoolName = "مدرسة الاختبار",
                isDefault = true
            )
        )

        val saved = database.studentDao().getDefaultStudent()
        assertNotNull(saved)
        assertEquals(studentId, saved?.id)
        assertEquals("سارة", saved?.name)
        assertEquals("4AP", saved?.levelCode)
    }
}
