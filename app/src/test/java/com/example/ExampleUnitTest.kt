package com.example

import com.example.algeria.AlgerianCurriculum
import com.example.data.model.AlgerianDayOfWeek
import com.example.data.model.EducationCycle
import com.example.data.model.EducationLevel
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun algerianEducationLevels_areProperlyStructured() {
        val primary5 = EducationLevel.fromCode("5AP")
        assertEquals(EducationCycle.PRIMARY, primary5.cycle)
        assertEquals("السنة الخامسة ابتدائي", primary5.titleAr)

        val middle4 = EducationLevel.fromCode("4AM")
        assertEquals(EducationCycle.MIDDLE, middle4.cycle)

        val bac = EducationLevel.fromCode("3AS")
        assertEquals(EducationCycle.SECONDARY, bac.cycle)
    }

    @Test
    fun algerianWeekCycle_computesTomorrowCorrectly() {
        // Sunday (1) -> Monday (2)
        assertEquals(AlgerianDayOfWeek.MONDAY, AlgerianDayOfWeek.getTomorrow(1))
        // Thursday (5) -> Friday (6)
        assertEquals(AlgerianDayOfWeek.FRIDAY, AlgerianDayOfWeek.getTomorrow(5))
        // Saturday (7) -> Sunday (1)
        assertEquals(AlgerianDayOfWeek.SUNDAY, AlgerianDayOfWeek.getTomorrow(7))
    }

    @Test
    fun algerianCurriculum_containsOfficialSubjectsAndSupplies() {
        val subjects = AlgerianCurriculum.OFFICIAL_SUBJECTS
        assertTrue(subjects.isNotEmpty())
        assertTrue(subjects.any { it.nameAr == "الرياضيات" })
        assertTrue(subjects.any { it.nameAr == "اللغة العربية" })
        assertTrue(subjects.any { it.nameAr == "العلوم الطبيعية والحياة" })

        val math = subjects.find { it.nameAr == "الرياضيات" }
        assertNotNull(math)
        assertTrue(math!!.defaultItemsAr.contains("الهندسة"))
    }
}
