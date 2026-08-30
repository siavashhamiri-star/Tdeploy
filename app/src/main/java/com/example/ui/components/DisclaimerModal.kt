package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DisclaimerModal(
    isOpen: Boolean,
    onAccept: () -> Unit
) {
    if (!isOpen) return

    var isChecked by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { /* Modal must be explicitly accepted */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .testTag("disclaimer_modal"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Header Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "هشدار",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "اطلاعیه مهم و شرایط استفاده",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Color(0xFFDC2626)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Legal Text Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DisclaimerItem(
                        number = "۱",
                        title = "ماهیت ابزار:",
                        desc = "«شهرِ مجازیِ توانا» صرفاً یک بستر دیجیتال برای معرفی مشاغل، تخصص‌ها و تسهیل ارتباط و همکاری میان کاربران است."
                    )

                    DisclaimerItem(
                        number = "۲",
                        title = "عدم تضمین صحت اطلاعات:",
                        desc = "پلتفرم صحت ادعاها، سوابق و اطلاعات ارائه‌شده توسط کاربران را تضمین نمی‌کند."
                    )

                    DisclaimerItem(
                        number = "۳",
                        title = "مسئولیت شخصی کاربران:",
                        desc = "تمامی معاملات، توافقات و ارتباطات بین کاربران، با مسئولیت شخصی خود طرفین انجام می‌شود و کاربران موظف‌اند جوانب احتیاط و راستی‌آزمایی را رعایت کنند."
                    )

                    DisclaimerItem(
                        number = "۴",
                        title = "پذیرش شرایط:",
                        desc = "ورود به اپلیکیشن و استفاده از بخش‌های لیگ، چت و معرفی به معنای پذیرش شرایط استفاده است."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Acceptance Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isChecked = !isChecked }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        modifier = Modifier.testTag("accept_terms_checkbox")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "شرایط استفاده، سلب مسئولیت و حریم خصوصی را مطالعه کردم و می‌پذیرم.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Enter Button
                Button(
                    onClick = {
                        if (isChecked) {
                            onAccept()
                        }
                    },
                    enabled = isChecked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("enter_app_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                ) {
                    Text(
                        text = "ورود به شهر مجازی توانا",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DisclaimerItem(
    number: String,
    title: String,
    desc: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$number. $title",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                lineHeight = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
