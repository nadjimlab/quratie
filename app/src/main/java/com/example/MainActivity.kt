package com.example

import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notifications.DailyReminderScheduler
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.QiraatiViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: QiraatiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DailyReminderScheduler.schedule(this)
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        setContent {
            MyApplicationTheme {
                // Ensure natural RTL layout direction for Arabic experience
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    QiraatiApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun QiraatiApp(viewModel: QiraatiViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppDestination.Home.route

    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val activeStudent by viewModel.activeStudent.collectAsStateWithLifecycle()
    val selectedDayIndex by viewModel.selectedDayIndex.collectAsStateWithLifecycle()
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val selectedDaySlots by viewModel.selectedDaySlots.collectAsStateWithLifecycle()
    val todaySlots by viewModel.todaySlots.collectAsStateWithLifecycle()
    val tomorrowSummary by viewModel.tomorrowSummary.collectAsStateWithLifecycle()
    val homeworkList by viewModel.homeworkList.collectAsStateWithLifecycle()
    val examsList by viewModel.examsList.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val chatMessages by viewModel.aiChatMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val isOcrScanning by viewModel.isOcrScanning.collectAsStateWithLifecycle()
    val ocrDraftSlots by viewModel.ocrDraftSlots.collectAsStateWithLifecycle()
    val showCelebration by viewModel.showCelebration.collectAsStateWithLifecycle()

    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showOcrScannerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ChildSelectorHeader(
                students = allStudents,
                activeStudent = activeStudent,
                onSelectStudent = { viewModel.selectStudent(it) },
                onAddStudentClick = { showAddStudentDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .testTag("main_bottom_nav")
                    .drawBehind {
                        drawLine(
                            color = SlateBorderLight,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                val navItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IndigoPrimary,
                    selectedTextColor = IndigoPrimary,
                    indicatorColor = IndigoContainer,
                    unselectedIconColor = TextSlate400,
                    unselectedTextColor = TextSlate400
                )

                // 1. Home
                NavigationBarItem(
                    selected = currentRoute == AppDestination.Home.route,
                    onClick = {
                        navController.navigate(AppDestination.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == AppDestination.Home.route) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "الرئيسة"
                        )
                    },
                    label = { Text("الرئيسية", fontSize = 10.5.sp, fontWeight = if (currentRoute == AppDestination.Home.route) FontWeight.Bold else FontWeight.Medium) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_home")
                )

                // 2. Timetable
                NavigationBarItem(
                    selected = currentRoute == AppDestination.Timetable.route,
                    onClick = {
                        navController.navigate(AppDestination.Timetable.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == AppDestination.Timetable.route) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "الجدول"
                        )
                    },
                    label = { Text("الجدول", fontSize = 10.5.sp, fontWeight = if (currentRoute == AppDestination.Timetable.route) FontWeight.Bold else FontWeight.Medium) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_timetable")
                )

                // 3. Homework & Exams
                NavigationBarItem(
                    selected = currentRoute == AppDestination.HomeworkAndExams.route,
                    onClick = {
                        navController.navigate(AppDestination.HomeworkAndExams.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == AppDestination.HomeworkAndExams.route) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                            contentDescription = "الواجبات"
                        )
                    },
                    label = { Text("الواجبات", fontSize = 10.5.sp, fontWeight = if (currentRoute == AppDestination.HomeworkAndExams.route) FontWeight.Bold else FontWeight.Medium) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_homework")
                )

                // 4. Tomorrow
                NavigationBarItem(
                    selected = currentRoute == AppDestination.Tomorrow.route,
                    onClick = {
                        navController.navigate(AppDestination.Tomorrow.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == AppDestination.Tomorrow.route) Icons.Filled.Backpack else Icons.Outlined.Backpack,
                            contentDescription = "الغد"
                        )
                    },
                    label = { Text("الغد", fontSize = 10.5.sp, fontWeight = if (currentRoute == AppDestination.Tomorrow.route) FontWeight.Bold else FontWeight.Medium) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_tomorrow")
                )

                // 5. Assistant & More
                NavigationBarItem(
                    selected = currentRoute == AppDestination.AssistantAndMore.route,
                    onClick = {
                        navController.navigate(AppDestination.AssistantAndMore.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == AppDestination.AssistantAndMore.route) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "المزيد"
                        )
                    },
                    label = { Text("المزيد", fontSize = 10.5.sp, fontWeight = if (currentRoute == AppDestination.AssistantAndMore.route) FontWeight.Bold else FontWeight.Medium) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_item_more")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestination.Home.route) {
                HomeScreen(
                    activeStudent = activeStudent,
                    tomorrowSummary = tomorrowSummary,
                    todaySlots = todaySlots,
                    pendingHomework = homeworkList.filter { !it.homework.isCompleted },
                    upcomingExams = examsList,
                    onToggleHomework = { id, done -> viewModel.toggleHomework(id, done) },
                    onMarkAllBackpackReady = { viewModel.markAllBackpackReady() },
                    onNavigateToTomorrow = { navController.navigate(AppDestination.Tomorrow.route) },
                    onNavigateToTimetable = { navController.navigate(AppDestination.Timetable.route) },
                    onNavigateToHomework = { navController.navigate(AppDestination.HomeworkAndExams.route) },
                    onNavigateToAssistant = { navController.navigate(AppDestination.AssistantAndMore.route) }
                )
            }

            composable(AppDestination.Tomorrow.route) {
                TomorrowScreen(
                    activeStudent = activeStudent,
                    summary = tomorrowSummary,
                    onToggleBackpackItem = { id, packed -> viewModel.toggleBackpackItem(id, packed) },
                    onMarkAllReady = { viewModel.markAllBackpackReady() },
                    onResetBackpack = { viewModel.resetBackpackForTomorrow() },
                    onToggleHomework = { id, done -> viewModel.toggleHomework(id, done) },
                    onAddCustomItem = { name, cat -> viewModel.addCustomBackpackItem(name, cat) }
                )
            }

            composable(AppDestination.Timetable.route) {
                TimetableScreen(
                    activeStudent = activeStudent,
                    selectedDayIndex = selectedDayIndex,
                    slots = selectedDaySlots,
                    allSubjects = allSubjects,
                    onSelectDay = { viewModel.setSelectedDay(it) },
                    onAddSlot = { period, start, end, subId, room ->
                        viewModel.addTimetableSlot(selectedDayIndex, period, start, end, subId, room)
                    },
                    onDeleteSlot = { viewModel.deleteTimetableSlot(it) },
                    onResetToCurriculumTemplate = {
                        activeStudent?.let { viewModel.resetToAlgerianTemplate(it.levelCode) }
                    },
                    onOpenOcrScanner = { showOcrScannerDialog = true }
                )
            }

            composable(AppDestination.HomeworkAndExams.route) {
                HomeworkAndExamsScreen(
                    activeStudent = activeStudent,
                    homeworkList = homeworkList,
                    examsList = examsList,
                    allSubjects = allSubjects,
                    onToggleHomework = { id, done -> viewModel.toggleHomework(id, done) },
                    onDeleteHomework = { viewModel.deleteHomework(it) },
                    onAddHomework = { subId, title, desc, dueMillis, priority ->
                        viewModel.addHomework(subId, title, desc, dueMillis, priority)
                    },
                    onUpdateExamRevision = { id, status -> viewModel.updateExamRevision(id, status) },
                    onDeleteExam = { viewModel.deleteExam(it) },
                    onAddExam = { subId, title, type, dateMillis, dur, syllabus, notes ->
                        viewModel.addExam(subId, title, type, dateMillis, dur, syllabus, notes)
                    }
                )
            }

            composable(AppDestination.AssistantAndMore.route) {
                AssistantAndMoreScreen(
                    activeStudent = activeStudent,
                    allStudents = allStudents,
                    allSubjects = allSubjects,
                    notifications = notifications,
                    chatMessages = chatMessages,
                    isAiLoading = isAiLoading,
                    onSendMessage = { viewModel.askAiAssistant(it) },
                    onAddStudent = { showAddStudentDialog = true },
                    onDeleteStudent = { viewModel.deleteStudent(it) }
                )
            }
        }
    }

    // Interactive Dialogs
    if (showAddStudentDialog) {
        AddStudentDialog(
            onDismiss = { showAddStudentDialog = false },
            onConfirm = { name, levelCode, stream, school, emoji, colorHex ->
                viewModel.addNewStudent(name, levelCode, stream, school, emoji, colorHex)
            }
        )
    }

    if (showOcrScannerDialog) {
        TimetableScannerDialog(
            isScanning = isOcrScanning,
            draftSlots = ocrDraftSlots,
            onScanText = { rawText ->
                viewModel.scanTimetableWithAi(null, rawText)
            },
            onScanImage = { bitmap ->
                viewModel.scanTimetableWithAi(bitmap, null)
            },
            onConfirm = { drafts ->
                viewModel.confirmOcrDrafts(drafts)
                showOcrScannerDialog = false
            },
            onDismiss = {
                viewModel.clearOcrDrafts()
                showOcrScannerDialog = false
            }
        )
    }

    if (showCelebration) {
        CelebrationDialog(
            studentName = activeStudent?.name ?: "ابنك",
            onDismiss = { viewModel.dismissCelebration() }
        )
    }

}
