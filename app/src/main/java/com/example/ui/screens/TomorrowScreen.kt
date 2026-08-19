package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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

@Composable
fun TomorrowScreen(
    activeStudent: StudentEntity?,
    summary: TomorrowPreparationSummary?,
    onToggleBackpackItem: (Long, Boolean) -> Unit,
    onMarkAllReady: () -> Unit,
    onResetBackpack: () -> Unit,
    onToggleHomework: (Long, Boolean) -> Unit,
    onAddCustomItem: (String, BackpackCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddItemDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(BackpackCategory.TOOL) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High Density Header Card
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🎒 حقيبة الغد (${summary?.dayLabelAr ?: "اليوم القادم"})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = TextSlate900
                            )
                            Text(
                                text = "${activeStudent?.name ?: "التلميذ"} • ${summary?.dateFormatted ?: ""}",
                                fontSize = 12.sp,
                                color = TextSlate500
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(IndigoContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎒", fontSize = 20.sp)
                        }
                    }

                    // Progress Bar
                    val total = summary?.totalItemsCount ?: 0
                    val packed = summary?.packedItemsCount ?: 0
                    val progress = if (total > 0) packed.toFloat() / total else 0f

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "مستوى جاهزية المحفظة",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSlate500
                            )
                            Text(
                                text = "$packed من $total مستلزمات جاهزة",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (progress >= 1f) EmeraldAccent else IndigoPrimary
                            )
                        }
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (progress >= 1f) EmeraldAccent else IndigoPrimary,
                            trackColor = SlateBorderLight
                        )
                    }

                    // Emerald Big Button
                    Button(
                        onClick = {
                            if (summary?.isBackpackReady == true) onResetBackpack() else onMarkAllReady()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (summary?.isBackpackReady == true) EmeraldAccentDark else EmeraldAccent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("tomorrow_mark_all_button")
                    ) {
                        Text(
                            text = if (summary?.isBackpackReady == true) "المحفظة جاهزة بالكامل 🎉 (إعادة ضبط)" else "✅ تم تجهيز المحفظة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Special Equipment Alerts
        if (summary?.specialSuppliesAlerts?.isNotEmpty() == true) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "⚠️ تنبيهات هامة لمستلزمات الغد:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = OrangeAccent
                    )
                    summary.specialSuppliesAlerts.forEach { alert ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = OrangeContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🔔", fontSize = 15.sp)
                                Text(
                                    text = alert,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnOrangeContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        // Required Books & Notebooks Card
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
                    Text(
                        text = "📚 الكتب والكراسات المطلوبة لغد:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextSlate900
                    )

                    if (summary?.requiredMaterialsBySubject.isNullOrEmpty()) {
                        Text(
                            text = "لا توجد حصص مبرمجة ليوم الغد.",
                            fontSize = 12.sp,
                            color = TextSlate500
                        )
                    } else {
                        summary?.requiredMaterialsBySubject?.forEach { (subject, materials) ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = SurfaceVariantLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    SubjectColorBadge(name = subject.nameAr, colorHex = subject.colorHex)
                                    materials.forEach { item ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text("•", color = IndigoPrimary, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = item,
                                                fontSize = 12.sp,
                                                color = TextSlate700
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

        // Daily Checklist Items Card
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
                            text = "📝 قائمة فحص المحفظة:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextSlate900
                        )

                        TextButton(
                            onClick = { showAddItemDialog = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "+ إضافة غرض", fontSize = 11.5.sp, color = IndigoPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    summary?.dailyBackpackItems?.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (item.isPacked) SurfaceVariantLight.copy(alpha = 0.6f) else SurfaceVariantLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleBackpackItem(item.id, !item.isPacked) }
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
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Custom rounded High Density checkbox
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (item.isPacked) IndigoPrimary else Color.Transparent)
                                            .border(
                                                width = 2.dp,
                                                color = if (item.isPacked) IndigoPrimary else SlateDivider,
                                                shape = RoundedCornerShape(6.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (item.isPacked) {
                                            Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Text(
                                        text = item.itemName,
                                        fontSize = 12.5.sp,
                                        fontWeight = if (item.isPacked) FontWeight.Normal else FontWeight.SemiBold,
                                        textDecoration = if (item.isPacked) TextDecoration.LineThrough else TextDecoration.None,
                                        color = if (item.isPacked) TextSlate400 else TextSlate900
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text(text = "إضافة غرض للمحفظة", fontWeight = FontWeight.Bold, color = TextSlate900) },
            text = {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("اسم الغرض (مثال: قصة للمطالعة، علبة ألوان)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newItemName.isNotBlank()) {
                            onAddCustomItem(newItemName.trim(), selectedCategory)
                            newItemName = ""
                            showAddItemDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) { Text("إلغاء") }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}
