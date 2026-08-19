package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

@Composable
fun HomeScreen(
    activeStudent: StudentEntity?,
    tomorrowSummary: TomorrowPreparationSummary?,
    todaySlots: List<TimetableSlotWithSubject>,
    pendingHomework: List<HomeworkWithSubject>,
    upcomingExams: List<ExamWithSubject>,
    onToggleHomework: (Long, Boolean) -> Unit,
    onMarkAllBackpackReady: () -> Unit,
    onNavigateToTomorrow: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToHomework: () -> Unit,
    onNavigateToAssistant: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. High Density Hero Card: AI Assistant Insight (Indigo 900)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = IndigoHero,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_hero_insight_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(IndigoHero, IndigoHeroSurface)
                            )
                        )
                ) {
                    // Decorative glow circle in background
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .offset(x = (-30).dp, y = (-30).dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.06f))
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = "المساعد الذكي",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "تحديث فوري",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        val studentName = activeStudent?.name ?: "ابنك"
                        val tomorrowSlotCount = tomorrowSummary?.slots?.size ?: 0
                        val pendingCount = pendingHomework.size
                        val nextExam = upcomingExams.firstOrNull()

                        val dynamicSummaryText = if (tomorrowSlotCount > 0) {
                            "مرحباً بك، غداً ${tomorrowSummary?.dayLabelAr ?: "يوم حافل"} لـ $studentName. لديه $tomorrowSlotCount حصص، ${if (pendingCount > 0) "وواجب ${pendingHomework.first().subject?.nameAr ?: ""} لم يُنجز بعد." else "وجميع واجباته منجزة."} ${if (nextExam != null) "كما يقترب اختبار ${nextExam.subject?.nameAr ?: ""}." else ""}"
                        } else {
                            "مرحباً بك، غداً يوم راحة لـ $studentName. فرصة ممتازة للمراجعة الخفيفة وتحضير الأسبوع القادم."
                        }

                        Text(
                            text = dynamicSummaryText,
                            color = Color.White,
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Button(
                            onClick = onNavigateToTomorrow,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = IndigoHero
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "مراجعة خطة الغد والمحفظة",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. High Density 2-Column Metric Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Nearest Exam
                val nextExam = upcomingExams.firstOrNull()
                val examDaysLeft = if (nextExam != null) {
                    val diff = nextExam.exam.examDateMillis - System.currentTimeMillis()
                    (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
                } else null

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToHomework() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(OrangeContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⌛", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = nextExam?.let { "اختبار ${it.subject?.nameAr ?: ""}" } ?: "الاختبارات",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (examDaysLeft != null) "بعد $examDaysLeft يوم" else "لا اختبار قريب",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate900
                        )
                    }
                }

                // Card 2: Pending Homework
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToHomework() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(EmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📝", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "الواجبات",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate500
                        )
                        Text(
                            text = "${pendingHomework.size} متبقية",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate900
                        )
                    }
                }
            }
        }

        // 3. High Density Backpack Section (White Container with Slate rows)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tomorrow_hero_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Title Header with Day Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎒 تجهيز المحفظة (الغد)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextSlate900
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = IndigoContainer
                        ) {
                            Text(
                                text = tomorrowSummary?.dayLabelAr ?: "اليوم القادم",
                                color = IndigoPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Tomorrow's Required Items preview
                    val requiredSlots = tomorrowSummary?.slots ?: emptyList()
                    if (requiredSlots.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            requiredSlots.take(3).forEach { slotWithSub ->
                                val sub = slotWithSub.subject
                                val itemsSummary = sub?.defaultItemsAr?.split(",")?.take(2)?.joinToString(" + ") ?: "الكراس والكتاب"

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = SurfaceVariantLight,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .border(2.dp, IndigoPrimary, RoundedCornerShape(6.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {}

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${sub?.nameAr ?: "مادة"}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = TextSlate900
                                            )
                                            Text(
                                                text = itemsSummary,
                                                fontSize = 11.sp,
                                                color = TextSlate500,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "🏖️ غداً يوم عطلة أو راحة، لا توجد حصص مبرمجة.",
                            fontSize = 12.sp,
                            color = TextSlate500
                        )
                    }

                    // Special Alert Pill if sports or lab supplies needed
                    if (tomorrowSummary?.specialSuppliesAlerts?.isNotEmpty() == true) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = OrangeContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("⚠️", fontSize = 13.sp)
                                Text(
                                    text = tomorrowSummary.specialSuppliesAlerts.first(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnOrangeContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Emerald Big Action Button
                    Button(
                        onClick = onMarkAllBackpackReady,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tomorrowSummary?.isBackpackReady == true) EmeraldAccentDark else EmeraldAccent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ready_backpack_button")
                    ) {
                        Text(
                            text = if (tomorrowSummary?.isBackpackReady == true) "المحفظة جاهزة بالكامل 🎉" else "✅ تم تجهيز المحفظة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 4. Today's Classes Section (High Density styled)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🗓️ حصص اليوم",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextSlate900
                        )

                        TextButton(
                            onClick = onNavigateToTimetable,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "عرض الجدول", fontSize = 11.5.sp, color = IndigoPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (todaySlots.isEmpty()) {
                        Text(
                            text = "لا توجد حصص لليوم (يوم راحة).",
                            fontSize = 12.sp,
                            color = TextSlate500
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            todaySlots.forEach { slotWithSub ->
                                val sub = slotWithSub.subject
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SurfaceVariantLight,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            SubjectColorBadge(name = sub?.nameAr ?: "مادة", colorHex = sub?.colorHex ?: "#4F46E5")
                                            Column {
                                                Text(
                                                    text = "${slotWithSub.slot.startTime} - ${slotWithSub.slot.endTime}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextSlate900
                                                )
                                                if (slotWithSub.slot.roomOrTeacher.isNotBlank()) {
                                                    Text(
                                                        text = slotWithSub.slot.roomOrTeacher,
                                                        fontSize = 10.5.sp,
                                                        color = TextSlate500
                                                    )
                                                }
                                            }
                                        }

                                        Icon(
                                            imageVector = Icons.Default.CheckCircleOutline,
                                            contentDescription = null,
                                            tint = TextSlate400,
                                            modifier = Modifier.size(16.dp)
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
}
