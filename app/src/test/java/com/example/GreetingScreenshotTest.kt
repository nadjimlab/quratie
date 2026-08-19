package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.AlgerianDayOfWeek
import com.example.data.model.TomorrowPreparationSummary
import com.example.ui.screens.TomorrowPreparationHeroCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun greeting_screenshot() {
        val sampleSummary = TomorrowPreparationSummary(
            targetDay = AlgerianDayOfWeek.THURSDAY,
            dayLabelAr = "الخميس",
            dayLabelFr = "Jeudi",
            dateFormatted = "الخميس 20 نوفمبر",
            slots = emptyList(),
            requiredMaterialsBySubject = emptyList(),
            specialSuppliesAlerts = listOf("🏃 بدلة رياضية وحذاء"),
            dueHomeworkList = emptyList(),
            upcomingExamsList = emptyList(),
            dailyBackpackItems = emptyList(),
            isBackpackReady = false,
            totalItemsCount = 5,
            packedItemsCount = 3
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                TomorrowPreparationHeroCard(
                    summary = sampleSummary,
                    onMarkAllReady = {},
                    onViewDetails = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    }
}
