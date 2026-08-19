package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeworkAndExamsScreen(
    activeStudent: StudentEntity?,
    homeworkList: List<HomeworkWithSubject>,
    examsList: List<ExamWithSubject>,
    allSubjects: List<SubjectEntity>,
    onToggleHomework: (Long, Boolean) -> Unit,
    onDeleteHomework: (Long) -> Unit,
    onAddHomework: (subjectId: Long, title: String, desc: String, dueMillis: Long, priority: Priority) -> Unit,
    onUpdateExamRevision: (Long, RevisionStatus) -> Unit,
    onDeleteExam: (Long) -> Unit,
    onAddExam: (subjectId: Long, title: String, type: ExamType, dateMillis: Long, dur: Int, syllabus: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Homework, 1: Exams, 2: Revision Plan
    var showAddHwDialog by remember { mutableStateOf(false) }
    var showAddExamDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddHwDialog = true else showAddExamDialog = true
                },
                containerColor = if (selectedTab == 0) IndigoPrimary else OrangeAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_hw_exam_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة")
            }
        },
        containerColor = BgLight,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Tab Switcher in High Density card style
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = IndigoPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "📝 الواجبات (${homeworkList.count { !it.homework.isCompleted }})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "🎯 الاختبارات (${examsList.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "🧠 خطة المراجعة",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 6.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Homework Tab
                        if (homeworkList.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    emoji = "🎉",
                                    title = "لا توجد واجبات مسجلة",
                                    subtitle = "اضغط على زر (+) في الأسفل لإضافة واجب مدرسي جديد."
                                )
                            }
                        } else {
                            items(homeworkList, key = { it.homework.id }) { hwWithSub ->
                                val hw = hwWithSub.homework
                                val sub = hwWithSub.subject
                                val dueDate = SimpleDateFormat("EEEE d MMMM", Locale("ar")).format(Date(hw.dueDateMillis))

                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            // Custom High Density Checkbox
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (hw.isCompleted) EmeraldAccent else Color.Transparent)
                                                    .border(
                                                        width = 2.dp,
                                                        color = if (hw.isCompleted) EmeraldAccent else SlateDivider,
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .clickable { onToggleHomework(hw.id, !hw.isCompleted) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (hw.isCompleted) {
                                                    Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    SubjectColorBadge(name = sub?.nameAr ?: "مادة", colorHex = sub?.colorHex ?: "#4F46E5", isSmall = true)
                                                    Text(
                                                        text = "تسليم: $dueDate",
                                                        fontSize = 11.sp,
                                                        color = TextSlate500
                                                    )
                                                }
                                                Text(
                                                    text = hw.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.5.sp,
                                                    color = if (hw.isCompleted) TextSlate400 else TextSlate900,
                                                    textDecoration = if (hw.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                                )
                                                if (hw.description.isNotBlank()) {
                                                    Text(
                                                        text = hw.description,
                                                        fontSize = 11.5.sp,
                                                        color = TextSlate500
                                                    )
                                                }
                                            }
                                        }

                                        IconButton(onClick = { onDeleteHomework(hw.id) }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Delete,
                                                contentDescription = "حذف الواجب",
                                                tint = ErrorRed.copy(alpha = 0.6f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Exams Tab
                        if (examsList.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    emoji = "🎯",
                                    title = "لا توجد فروض أو اختبارات مسجلة",
                                    subtitle = "اضغط على زر (+) في الأسفل لإضافة فرض أو اختبار مدرسي مع موعده ومحاوره."
                                )
                            }
                        } else {
                            items(examsList, key = { it.exam.id }) { examWithSub ->
                                val exam = examWithSub.exam
                                val sub = examWithSub.subject
                                val examDate = SimpleDateFormat("EEEE d MMMM yyyy", Locale("ar")).format(Date(exam.examDateMillis))

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                SubjectColorBadge(name = sub?.nameAr ?: "مادة", colorHex = sub?.colorHex ?: "#2563EB")
                                                ExamCountdownBadge(examDateMillis = exam.examDateMillis)
                                            }

                                            IconButton(onClick = { onDeleteExam(exam.id) }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = "حذف الاختبار",
                                                    tint = ErrorRed.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = exam.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp,
                                            color = TextSlate900
                                        )

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(16.dp))
                                            Text(text = "الموعد: $examDate", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSlate700)
                                        }

                                        if (exam.syllabusTopics.isNotBlank()) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = SurfaceVariantLight,
                                                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight)
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Text(text = "📖 المحاور المقررة:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextSlate700)
                                                    Text(text = exam.syllabusTopics, fontSize = 12.sp, color = TextSlate500)
                                                }
                                            }
                                        }

                                        // Revision Status Selector
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "المراجعة:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSlate700)
                                            RevisionStatus.entries.forEach { status ->
                                                val isSelected = exam.revisionStatus == status.name
                                                FilterChip(
                                                    selected = isSelected,
                                                    onClick = { onUpdateExamRevision(exam.id, status) },
                                                    label = { Text(status.titleAr, fontSize = 10.sp) },
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Smart Revision Planner Tab (Indigo High Density)
                        item {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = IndigoContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🧠", fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "برنامج المراجعة الذكي المقترح",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = OnIndigoContainer
                                        )
                                    }
                                    Text(
                                        text = "موزع علمياً حسب معاملات المواد في المنهاج الجزائري ومواعيد الفروض القادمة لـ ${activeStudent?.name ?: "التلميذ"}:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnIndigoContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }

                        val revisionPlanItems = listOf(
                            Triple("جلسة المساء 1 (18:00 - 18:45)", "الرياضيات (معامل 4)", "حل تمارين نظرية طالس وحساب المساحات (45 دقيقة)."),
                            Triple("استراحة واسترجاع نشاط (18:45 - 19:00)", "لمجة صحية 🍎", "راحة العين وشرب الماء 15 دقيقة."),
                            Triple("جلسة المساء 2 (19:00 - 19:40)", "اللغة العربية والفرنسية", "حفظ القواعد وكتابة فقرة تعبيرية قصيرة (40 دقيقة)."),
                            Triple("جلسة ما قبل النوم (20:15 - 20:30)", "تجهيز المحفظة 🎒", "مراجعة كراس المراسلة ووضع كتب الغد ومستلزمات الرياضة/المخبر.")
                        )

                        items(revisionPlanItems) { (time, subject, detail) ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                                shadowElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = time, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = IndigoPrimary)
                                        Text(text = subject, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OrangeAccent)
                                    }
                                    Text(text = detail, style = MaterialTheme.typography.bodyMedium, color = TextSlate700)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddHwDialog) {
        AddHomeworkDialog(
            subjects = allSubjects,
            onDismiss = { showAddHwDialog = false },
            onConfirm = { subId, title, desc, dueMillis, priority ->
                onAddHomework(subId, title, desc, dueMillis, priority)
                showAddHwDialog = false
            }
        )
    }

    if (showAddExamDialog) {
        AddExamDialog(
            subjects = allSubjects,
            onDismiss = { showAddExamDialog = false },
            onConfirm = { subId, title, type, dateMillis, dur, syllabus, notes ->
                onAddExam(subId, title, type, dateMillis, dur, syllabus, notes)
                showAddExamDialog = false
            }
        )
    }
}

@Composable
fun EmptyStateCard(
    emoji: String,
    title: String,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(emoji, fontSize = 34.sp)
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextSlate900)
            Text(
                text = subtitle,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 12.sp,
                color = TextSlate500
            )
        }
    }
}
