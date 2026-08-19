package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TimetableScreen(
    activeStudent: StudentEntity?,
    selectedDayIndex: Int,
    slots: List<TimetableSlotWithSubject>,
    allSubjects: List<SubjectEntity>,
    onSelectDay: (Int) -> Unit,
    onAddSlot: (periodOrder: Int, start: String, end: String, subjectId: Long, room: String) -> Unit,
    onDeleteSlot: (Long) -> Unit,
    onResetToCurriculumTemplate: () -> Unit,
    onOpenOcrScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_slot_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة حصة")
            }
        },
        containerColor = BgLight,
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight)
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Action Buttons (OCR AI Scanner + Template Import)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onOpenOcrScanner,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = IndigoContainer,
                            contentColor = IndigoPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_ocr_scanner_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مسح الجدول بالـ AI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = onResetToCurriculumTemplate,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSlate700),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "نموذج الوزارة", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Days of the Week Selector (Sunday to Saturday)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AlgerianDayOfWeek.entries) { day ->
                        val isSelected = day.dayIndex == selectedDayIndex
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectDay(day.dayIndex) },
                            label = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = day.nameAr,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isSelected) IndigoPrimary else TextSlate700
                                    )
                                    Text(
                                        text = if (day.isSchoolDay) "دراسة" else "عطلة",
                                        fontSize = 10.sp,
                                        color = if (isSelected) IndigoPrimary else if (day.isSchoolDay) TextSlate400 else AmberSecondary
                                    )
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White,
                                selectedContainerColor = IndigoContainer,
                                selectedLabelColor = OnIndigoContainer
                            ),
                            border = if (isSelected) FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = true,
                                borderColor = IndigoPrimary,
                                borderWidth = 1.5.dp
                            ) else FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = false,
                                borderColor = SlateBorderLight,
                                borderWidth = 1.dp
                            ),
                            modifier = Modifier.testTag("day_chip_${day.dayIndex}")
                        )
                    }
                }
            }

            // Day Title & Slot Count
            item {
                val currentDay = AlgerianDayOfWeek.fromIndex(selectedDayIndex)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "برنامج يوم ${currentDay.nameAr} (${slots.size} حصص)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextSlate900
                    )

                    if (!currentDay.isSchoolDay) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AmberContainer
                        ) {
                            Text(
                                text = "🏖️ عطلة نهاية الأسبوع",
                                color = OnAmberContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Slots List
            if (slots.isEmpty()) {
                item {
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
                            Text("🗓️", fontSize = 32.sp)
                            Text(
                                text = "لا توجد حصص مسجلة لهذا اليوم.",
                                fontWeight = FontWeight.Bold,
                                color = TextSlate900,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "اضغط على زر (+) في الأسفل لإضافة حصة، أو استخدم المسح بالذكاء الاصطناعي.",
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontSize = 12.sp,
                                color = TextSlate500
                            )
                        }
                    }
                }
            } else {
                items(slots, key = { it.slot.id }) { slotWithSub ->
                    val slot = slotWithSub.slot
                    val sub = slotWithSub.subject

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slot_item_${slot.id}")
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
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                // Time Column
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SurfaceVariantLight,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                                    modifier = Modifier.width(68.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = slot.startTime,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = TextSlate900
                                        )
                                        Text(
                                            text = slot.endTime,
                                            fontSize = 11.sp,
                                            color = TextSlate500
                                        )
                                    }
                                }

                                // Subject Info
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    SubjectColorBadge(name = sub?.nameAr ?: "مادة", colorHex = sub?.colorHex ?: "#4F46E5")
                                    if (slot.roomOrTeacher.isNotBlank()) {
                                        Text(
                                            text = slot.roomOrTeacher,
                                            fontSize = 12.sp,
                                            color = TextSlate500,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    if (sub?.defaultItemsAr?.isNotBlank() == true) {
                                        Text(
                                            text = "🎒 ${sub.defaultItemsAr.split(",").take(2).joinToString(" + ")}",
                                            fontSize = 11.sp,
                                            color = TextSlate400,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            // Delete button
                            IconButton(
                                onClick = { onDeleteSlot(slot.id) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "حذف الحصة",
                                    tint = ErrorRed.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTimetableSlotDialog(
            dayIndex = selectedDayIndex,
            subjects = allSubjects,
            onDismiss = { showAddDialog = false },
            onConfirm = { period, start, end, subId, room ->
                onAddSlot(period, start, end, subId, room)
                showAddDialog = false
            }
        )
    }
}
