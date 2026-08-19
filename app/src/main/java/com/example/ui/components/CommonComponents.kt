package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EducationLevel
import com.example.data.model.StudentEntity
import com.example.ui.theme.*

@Composable
fun ChildSelectorHeader(
    students: List<StudentEntity>,
    activeStudent: StudentEntity?,
    onSelectStudent: (Long) -> Unit,
    onAddStudentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BgLight,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Main Top Bar in High Density style
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar circle with border and shadow
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(IndigoPrimary)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeStudent?.avatarEmoji ?: "🎓",
                            fontSize = 20.sp
                        )
                    }

                    Column {
                        Text(
                            text = "قِرايتي",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate900,
                            fontSize = 18.sp,
                            lineHeight = 22.sp
                        )
                        val level = activeStudent?.let { EducationLevel.fromCode(it.levelCode) }
                        Text(
                            text = "${activeStudent?.name ?: "التلميذ"} • ${level?.titleAr ?: "المدرسة الجزائرية"}",
                            fontSize = 12.sp,
                            color = TextSlate500
                        )
                    }
                }

                // Add Child button
                FilledTonalButton(
                    onClick = onAddStudentClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.White,
                        contentColor = TextSlate700
                    ),
                    elevation = ButtonDefaults.filledTonalButtonElevation(defaultElevation = 1.dp),
                    modifier = Modifier.testTag("add_child_header_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "إضافة تلميذ",
                        modifier = Modifier.size(16.dp),
                        tint = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "إضافة ابن", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal Children Badges
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(students, key = { it.id }) { student ->
                    val isSelected = student.id == activeStudent?.id
                    val level = EducationLevel.fromCode(student.levelCode)

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) IndigoContainer else Color.White,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, IndigoPrimary)
                                else androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                        shadowElevation = if (isSelected) 1.dp else 0.5.dp,
                        modifier = Modifier
                            .clickable { onSelectStudent(student.id) }
                            .testTag("student_chip_${student.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = student.avatarEmoji, fontSize = 16.sp)
                            Column {
                                Text(
                                    text = student.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) IndigoPrimaryDark else TextSlate900
                                )
                                Text(
                                    text = level.code,
                                    fontSize = 10.sp,
                                    color = if (isSelected) IndigoPrimary else TextSlate400
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectColorBadge(
    name: String,
    colorHex: String,
    modifier: Modifier = Modifier,
    isSmall: Boolean = false
) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        IndigoPrimary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(parsedColor.copy(alpha = 0.12f))
            .padding(horizontal = if (isSmall) 6.dp else 10.dp, vertical = if (isSmall) 2.dp else 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = parsedColor,
            fontSize = if (isSmall) 11.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ExamCountdownBadge(
    examDateMillis: Long,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val diffMillis = examDateMillis - now
    val daysLeft = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

    val (badgeText, badgeColor, containerColor) = when {
        daysLeft < 0 -> Triple("انتهى", TextSlate400, Color(0xFFF1F5F9))
        daysLeft == 0 -> Triple("اليوم!", ErrorRed, ErrorContainer)
        daysLeft == 1 -> Triple("غداً ⚠️", ErrorRed, ErrorContainer)
        daysLeft in 2..3 -> Triple("بعد $daysLeft أيام ⏰", OrangeAccent, OrangeContainer)
        else -> Triple("بعد $daysLeft أيام", IndigoPrimary, IndigoContainer)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        modifier = modifier
    ) {
        Text(
            text = badgeText,
            color = badgeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun CelebrationDialog(
    studentName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("بارك الله فيك، شكراً!", fontWeight = FontWeight.Bold)
            }
        },
        icon = {
            Text(text = "🎒✨🎉", fontSize = 36.sp)
        },
        title = {
            Text(
                text = "تم تجهيز المحفظة بنجاح!",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = TextSlate900
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "محفظة $studentName جاهزة بالكامل لدروس الغد مع جميع الكتب والكراسات والأدوات المطلوبة.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSlate700
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🌟 نوم هادئ وتوفيق دائم لابنكم في المدرسة!",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = AmberSecondary,
                    fontSize = 13.sp
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
