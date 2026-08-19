package com.example.ui.components

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ai.ParsedSlotDraft
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, levelCode: String, stream: String, schoolName: String, emoji: String, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf(EducationLevel.MIDDLE_3) }
    var selectedStream by remember { mutableStateOf(SecondaryStream.NONE) }
    var schoolName by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("👦🏻") }
    var selectedColor by remember { mutableStateOf("#4F46E5") }

    val emojis = listOf("👦🏻", "👧🏻", "👦🏼", "👧🏼", "👦🏽", "👧🏽", "🧑‍🎓", "🎒", "🌟")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎒", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "إضافة تلميذ جديد", fontWeight = FontWeight.Bold, color = IndigoPrimary)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم التلميذ (مثال: محمد، سارة)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                item {
                    Text(text = "المستوى الدراسي الجزائري:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSlate700)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    var expandedCycle by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedCycle,
                        onExpandedChange = { expandedCycle = it }
                    ) {
                        OutlinedTextField(
                            value = selectedLevel.titleAr,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCycle) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCycle,
                            onDismissRequest = { expandedCycle = false }
                        ) {
                            EducationLevel.entries.forEach { level ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(level.titleAr, fontWeight = FontWeight.SemiBold)
                                            Text(level.titleFr, fontSize = 11.sp, color = TextSlate400)
                                        }
                                    },
                                    onClick = {
                                        selectedLevel = level
                                        expandedCycle = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (selectedLevel.cycle == EducationCycle.SECONDARY) {
                    item {
                        Text(text = "الشعبة / التخصص (للثانوي):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSlate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        var expandedStream by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedStream,
                            onExpandedChange = { expandedStream = it }
                        ) {
                            OutlinedTextField(
                                value = selectedStream.titleAr,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStream) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedStream,
                                onDismissRequest = { expandedStream = false }
                            ) {
                                SecondaryStream.entries.forEach { stream ->
                                    DropdownMenuItem(
                                        text = { Text(stream.titleAr) },
                                        onClick = {
                                            selectedStream = stream
                                            expandedStream = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = schoolName,
                        onValueChange = { schoolName = it },
                        label = { Text("اسم المؤسسة (المدرسة / المتوسطة / الثانوية)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                item {
                    Text(text = "الرمز التعبيري (الأفاتار):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSlate700)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        emojis.forEach { emoji ->
                            val isSelected = selectedEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) IndigoContainer else SurfaceVariantLight)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) IndigoPrimary else SlateBorderLight,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            name.trim(),
                            selectedLevel.code,
                            selectedStream.name,
                            schoolName.trim(),
                            selectedEmoji,
                            selectedColor
                        )
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("confirm_add_student_button")
            ) {
                Text("حفظ التلميذ", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSlate500)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeworkDialog(
    subjects: List<SubjectEntity>,
    onDismiss: () -> Unit,
    onConfirm: (subjectId: Long, title: String, description: String, dueDateMillis: Long, priority: Priority) -> Unit
) {
    var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: 1L) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var daysUntilDue by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "📝 إضافة واجب مدرسي", fontWeight = FontWeight.Bold, color = IndigoPrimary)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var expandedSubject by remember { mutableStateOf(false) }
                val currentSub = subjects.find { it.id == selectedSubjectId } ?: subjects.firstOrNull()
                
                Text(text = "المادة الدراسية:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSlate700)
                ExposedDropdownMenuBox(
                    expanded = expandedSubject,
                    onExpandedChange = { expandedSubject = it }
                ) {
                    OutlinedTextField(
                        value = currentSub?.nameAr ?: "اختر المادة",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubject) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSubject,
                        onDismissRequest = { expandedSubject = false }
                    ) {
                        subjects.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(sub.nameAr, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    selectedSubjectId = sub.id
                                    expandedSubject = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الواجب (مثال: حل تمرين 14 ص 30)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("homework_title_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("تفاصيل أو ملاحظات (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp)
                )

                Text(text = "تاريخ التسليم:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSlate700)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(1 to "غداً", 2 to "بعد يومين", 3 to "بعد 3 أيام", 7 to "الأسبوع القادم").forEach { (d, label) ->
                        val isSelected = daysUntilDue == d
                        FilterChip(
                            selected = isSelected,
                            onClick = { daysUntilDue = d },
                            label = { Text(label, fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Text(text = "درجة الأهمية:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSlate700)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Priority.entries.forEach { p ->
                        val isSelected = selectedPriority == p
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPriority = p },
                            label = { Text(p.titleAr, fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val dueMillis = System.currentTimeMillis() + (daysUntilDue * 24 * 60 * 60 * 1000L)
                        onConfirm(selectedSubjectId, title.trim(), description.trim(), dueMillis, selectedPriority)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("confirm_add_homework_button")
            ) {
                Text("إضافة الواجب", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSlate500) }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExamDialog(
    subjects: List<SubjectEntity>,
    onDismiss: () -> Unit,
    onConfirm: (subjectId: Long, title: String, examType: ExamType, dateMillis: Long, duration: Int, syllabus: String, notes: String) -> Unit
) {
    var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: 1L) }
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ExamType.TEST_1) }
    var daysUntilExam by remember { mutableStateOf(3) }
    var syllabus by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "🎯 إضافة فرض أو اختبار", fontWeight = FontWeight.Bold, color = OrangeAccent)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    var expandedSubject by remember { mutableStateOf(false) }
                    val currentSub = subjects.find { it.id == selectedSubjectId } ?: subjects.firstOrNull()
                    
                    Text(text = "المادة الدراسية:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSlate700)
                    ExposedDropdownMenuBox(
                        expanded = expandedSubject,
                        onExpandedChange = { expandedSubject = it }
                    ) {
                        OutlinedTextField(
                            value = currentSub?.nameAr ?: "اختر المادة",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubject) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSubject,
                            onDismissRequest = { expandedSubject = false }
                        ) {
                            subjects.forEach { sub ->
                                DropdownMenuItem(
                                    text = { Text(sub.nameAr) },
                                    onClick = {
                                        selectedSubjectId = sub.id
                                        expandedSubject = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Text(text = "نوع الاختبار:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSlate700)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExamType.entries.take(3).forEach { type ->
                            val isSelected = selectedType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedType = type
                                    if (title.isBlank()) title = type.titleAr
                                },
                                label = { Text(type.titleAr, fontSize = 11.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان الاختبار (مثال: الفرض الأول للفصل الثاني)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exam_title_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = syllabus,
                        onValueChange = { syllabus = it },
                        label = { Text("المحاور والدروس المعنية بالمراجعة") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                item {
                    Text(text = "موعد الاختبار:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSlate700)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(1 to "غداً", 2 to "بعد يومين", 3 to "بعد 3 أيام", 5 to "بعد 5 أيام", 7 to "بعد أسبوع").forEach { (d, label) ->
                            val isSelected = daysUntilExam == d
                            FilterChip(
                                selected = isSelected,
                                onClick = { daysUntilExam = d },
                                label = { Text(label, fontSize = 11.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات (مثال: إحضار الآلة الحاسبة وأدوات الهندسة)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTitle = if (title.isBlank()) selectedType.titleAr else title
                    val dateMillis = System.currentTimeMillis() + (daysUntilExam * 24 * 60 * 60 * 1000L)
                    onConfirm(selectedSubjectId, finalTitle.trim(), selectedType, dateMillis, 60, syllabus.trim(), notes.trim())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("confirm_add_exam_button")
            ) {
                Text("حفظ الاختبار", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSlate500) }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimetableSlotDialog(
    dayIndex: Int,
    subjects: List<SubjectEntity>,
    onDismiss: () -> Unit,
    onConfirm: (periodOrder: Int, start: String, end: String, subjectId: Long, room: String) -> Unit
) {
    var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: 1L) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("09:00") }
    var room by remember { mutableStateOf("قاعة 4") }
    var periodOrder by remember { mutableStateOf(1) }

    val dayName = AlgerianDayOfWeek.fromIndex(dayIndex).nameAr

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "⏰ إضافة حصة ليوم $dayName", fontWeight = FontWeight.Bold, color = IndigoPrimary)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var expandedSubject by remember { mutableStateOf(false) }
                val currentSub = subjects.find { it.id == selectedSubjectId } ?: subjects.firstOrNull()
                
                Text(text = "المادة الدراسية:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSlate700)
                ExposedDropdownMenuBox(
                    expanded = expandedSubject,
                    onExpandedChange = { expandedSubject = it }
                ) {
                    OutlinedTextField(
                        value = currentSub?.nameAr ?: "اختر المادة",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubject) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSubject,
                        onDismissRequest = { expandedSubject = false }
                    ) {
                        subjects.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(sub.nameAr) },
                                onClick = {
                                    selectedSubjectId = sub.id
                                    expandedSubject = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("من") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("إلى") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("القاعة / الأستاذ (مثال: قاعة 4 أو مخبر 1)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(periodOrder, startTime, endTime, selectedSubjectId, room)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("إضافة الحصة", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSlate500) }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
fun TimetableScannerDialog(
    isScanning: Boolean,
    draftSlots: List<ParsedSlotDraft>,
    onScanText: (String) -> Unit,
    onConfirm: (List<ParsedSlotDraft>) -> Unit,
    onDismiss: () -> Unit
) {
    var textInput by remember {
        mutableStateOf(
            """الأحد: رياضيات (08:00 - 09:00)، عربية (09:00 - 10:00)، علوم (10:15 - 11:15)، إسلامية (11:15 - 12:15)
الإثنين: فيزياء (08:00 - 09:00)، رياضيات (09:00 - 10:00)، إنجليزية (10:15 - 11:15)، مدنية (11:15 - 12:15)، تربية بدنية (13:30 - 15:30)
الثلاثاء: عربية (08:00 - 09:00)، فرنسية (09:00 - 10:00)، علوم (10:15 - 11:15)، رسم (11:15 - 12:15)
الأربعاء: رياضيات (08:00 - 09:00)، فيزياء (09:00 - 10:00)، عربية (10:15 - 11:15)، اجتماعيات (11:15 - 12:15)
الخميس: رياضيات (08:00 - 09:00)، فرنسية (09:00 - 10:00)، علوم (10:15 - 11:15)، تربية بدنية (11:15 - 12:15)"""
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مسح الجدول بالذكاء الاصطناعي (OCR)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = IndigoPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = TextSlate400)
                    }
                }

                if (draftSlots.isEmpty()) {
                    Text(
                        text = "التقط صورة لجدول الحصص أو الصق نصه، وسيقوم الذكاء الاصطناعي بتحليله وتحويله تلقائياً لجدول أسبوعي كامل:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate500
                    )

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(16.dp),
                        label = { Text("نص الجدول أو مستخرج الـ OCR") }
                    )

                    Button(
                        onClick = { onScanText(textInput) },
                        enabled = !isScanning && textInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_ocr_scan_button")
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جارٍ تحليل وقراءة الجدول...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تحليل واستخراج الحصص بالذكاء الاصطناعي", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Text(
                        text = "✅ تم استخراج ${draftSlots.size} حصة بنجاح! راجع الجدول ثم اضغط تأكيد:",
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary,
                        fontSize = 13.5.sp
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(draftSlots) { draft ->
                            val day = AlgerianDayOfWeek.fromIndex(draft.dayIndex).nameAr
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SurfaceVariantLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "$day • ${draft.subjectName}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextSlate900
                                        )
                                        Text(
                                            text = "${draft.startTime} - ${draft.endTime} ${if (draft.roomOrTeacher.isNotBlank()) "(${draft.roomOrTeacher})" else ""}",
                                            fontSize = 11.sp,
                                            color = TextSlate500
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onScanText(textInput) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("إعادة المحاولة", color = TextSlate700)
                        }

                        Button(
                            onClick = { onConfirm(draftSlots) },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("تأكيد وحفظ الجدول", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProUpgradeDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👑", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "باقة قِرايتي بلس | Qiraati+",
                    fontWeight = FontWeight.Bold,
                    color = OrangeAccent
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "تجربة متكاملة لمتابعة جميع أبنائك بميزات الذكاء الاصطناعي الحصرية:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSlate700
                )

                val proFeatures = listOf(
                    "👨‍👩‍👧‍👦 متابعة عدد غير محدود من الأبناء في كل الأطوار.",
                    "🤖 مساعد دراسي ذكي بدون حدود لتحليل الفروض والواجبات.",
                    "📷 مسح وقراءة غير محدودة للجداول بالـ OCR.",
                    "📊 تقارير إنجاز وتحليلات أسبوعية للنتائج.",
                    "☁️ نسخ احتياطي ومزامنة سحابية آمنة."
                )

                proFeatures.forEach { feat ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("✨", fontSize = 14.sp)
                        Text(feat, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = TextSlate700)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ترقية الحساب الآن", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("لاحقاً", color = TextSlate500) }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
