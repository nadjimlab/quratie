package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algeria.AlgerianCurriculum
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import kotlinx.coroutines.launch

@Composable
fun AssistantAndMoreScreen(
    activeStudent: StudentEntity?,
    allStudents: List<StudentEntity>,
    allSubjects: List<SubjectEntity>,
    notifications: List<AppNotificationEntity>,
    chatMessages: List<ChatMessage>,
    isAiLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onAddStudent: () -> Unit,
    onDeleteStudent: (StudentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: AI Assistant Chat, 1: Children Management, 2: Subjects Directory, 3: Algerian Holidays
    var inputQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgLight)
            .padding(horizontal = 16.dp)
    ) {
        // Sub-tabs in High Density style
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
                text = { Text("🤖 المساعد", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium, fontSize = 11.5.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("👨‍👩‍👧‍👦 الأبناء", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 11.5.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("📚 المواد", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium, fontSize = 11.5.sp) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("🇩🇿 العطل", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Medium, fontSize = 11.5.sp) }
            )
        }

        when (selectedTab) {
            0 -> {
                // 🤖 AI Assistant Chat Tab
                Column(modifier = Modifier.fillMaxSize()) {
                    val quickPrompts = listOf(
                        "ماذا عند ${activeStudent?.name ?: "ابني"} غداً وماذا يضع في المحفظة؟",
                        "اقترح خطة مراجعة ذكية لهذا الأسبوع",
                        "هل لديه فروض قريبة؟",
                        "كيف أنظم وقت دراسته مع الرياضة؟"
                    )

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(top = 6.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = IndigoContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("💡", fontSize = 18.sp)
                                    Text(
                                        text = "المساعد متصل ببيانات ${activeStudent?.name ?: "التلميذ"} الحالية (الحصص، الواجبات، الفروض) للإجابة بدقة وسرعة.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnIndigoContainer
                                    )
                                }
                            }
                        }

                        items(chatMessages, key = { it.id }) { msg ->
                            val isUser = msg.sender == "USER"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(
                                        topStart = 18.dp,
                                        topEnd = 18.dp,
                                        bottomStart = if (isUser) 18.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 18.dp
                                    ),
                                    color = if (isUser) IndigoPrimary else Color.White,
                                    border = if (isUser) null else androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.widthIn(max = 300.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = msg.text,
                                            color = if (isUser) Color.White else TextSlate900,
                                            fontSize = 13.sp,
                                            lineHeight = 19.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (isAiLoading) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = IndigoPrimary)
                                    Text(text = "جارٍ التفكير والإجابة من بيانات التلميذ...", fontSize = 12.sp, color = TextSlate500)
                                }
                            }
                        }

                        item {
                            Text(
                                text = "أسئلة مقترحة سريعة:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextSlate500
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                quickPrompts.forEach { prompt ->
                                    SuggestionChip(
                                        onClick = {
                                            onSendMessage(prompt)
                                            scope.launch { listState.animateScrollToItem(chatMessages.size) }
                                        },
                                        label = { Text(prompt, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = TextSlate700) },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Chat Input Box
                    Surface(
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 75.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = inputQuery,
                                onValueChange = { inputQuery = it },
                                placeholder = { Text("اكتب سؤالك هنا...", fontSize = 13.sp, color = TextSlate400) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_chat_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent
                                )
                            )

                            IconButton(
                                onClick = {
                                    if (inputQuery.isNotBlank()) {
                                        onSendMessage(inputQuery.trim())
                                        inputQuery = ""
                                        scope.launch { listState.animateScrollToItem(chatMessages.size) }
                                    }
                                },
                                enabled = inputQuery.isNotBlank() && !isAiLoading,
                                modifier = Modifier
                                    .background(IndigoPrimary, CircleShape)
                                    .testTag("send_ai_message_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "إرسال",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // 👨‍👩‍👧‍👦 Multi-Child Management Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 6.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Button(
                            onClick = onAddStudent,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إضافة ابن جديد إلى العائلة", fontWeight = FontWeight.Bold)
                        }
                    }

                    items(allStudents) { student ->
                        val level = EducationLevel.fromCode(student.levelCode)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
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
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(IndigoContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = student.avatarEmoji, fontSize = 22.sp)
                                    }

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(text = student.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextSlate900)
                                            if (student.isDefault) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = EmeraldContainer
                                                ) {
                                                    Text(
                                                        text = "رئيسي",
                                                        fontSize = 10.sp,
                                                        color = OnEmeraldContainer,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${level.titleAr} (${level.code}) • ${student.schoolName.ifBlank { "المدرسة الجزائرية" }}",
                                            fontSize = 12.sp,
                                            color = TextSlate500
                                        )
                                    }
                                }

                                if (allStudents.size > 1) {
                                    IconButton(onClick = { onDeleteStudent(student) }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "حذف",
                                            tint = ErrorRed.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            }

            2 -> {
                // 📚 Algerian Subjects Directory
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 6.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "المواد المعتمدة في المنهاج الجزائري والمستلزمات المقررة:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSlate500
                        )
                    }

                    items(allSubjects) { subject ->
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
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SubjectColorBadge(name = subject.nameAr, colorHex = subject.colorHex)
                                    Text(
                                        text = subject.nameFr,
                                        fontSize = 11.5.sp,
                                        color = TextSlate400
                                    )
                                }
                                Text(
                                    text = "🎒 المستلزمات الافتراضية: ${subject.defaultItemsAr}",
                                    fontSize = 12.sp,
                                    color = TextSlate700
                                )
                            }
                        }
                    }
                }
            }

            3 -> {
                // 🇩🇿 Algerian School Holidays
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 6.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = IndigoContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("🇩🇿", fontSize = 24.sp)
                                Column {
                                    Text(
                                        text = "رزنامة العطل المدرسية الرسمية (وزارة التربية)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = OnIndigoContainer
                                    )
                                    Text(
                                        text = "الموسم الدراسي الحالي للجمهورية الجزائرية الديمقراطية الشعبية",
                                        fontSize = 11.sp,
                                        color = OnIndigoContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    items(AlgerianCurriculum.ALGERIAN_HOLIDAYS) { holiday ->
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
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(text = holiday.iconEmoji, fontSize = 22.sp)
                                    Column {
                                        Text(text = holiday.nameAr, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextSlate900)
                                        Text(
                                            text = "${holiday.startDate} ⬅️ ${holiday.endDate}",
                                            fontSize = 11.sp,
                                            color = TextSlate500
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = IndigoContainer
                                ) {
                                    Text(
                                        text = holiday.seasonBadge,
                                        color = IndigoPrimary,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
