package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.YtRed
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ScriptInputCard(
    text: String,
    onTextChanged: (String) -> Unit,
    onClear: () -> Unit,
    onUseSample: () -> Unit,
    charLimit: Int = 5000,
    warningThreshold: Int = 4500,
    validationError: String? = null,
    modifier: Modifier = Modifier
) {
    val charCount = text.length
    val isNearLimit = charCount in warningThreshold..charLimit
    val isExceeded = charCount > charLimit

    val formattedCount = NumberFormat.getNumberInstance(Locale.GERMANY).format(charCount)
    val formattedLimit = NumberFormat.getNumberInstance(Locale.GERMANY).format(charLimit)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("script_input_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isExceeded || validationError != null) 1.5.dp else 1.dp,
            color = when {
                isExceeded -> MaterialTheme.colorScheme.error
                validationError != null -> MaterialTheme.colorScheme.error
                isNearLimit -> WarningAmber
                else -> MaterialTheme.colorScheme.outline
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = YtRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Naskah Voice-over",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Quick actions: sample & clear
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onUseSample,
                        modifier = Modifier.testTag("button_use_sample")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoStories,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Contoh",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    if (text.isNotEmpty()) {
                        TextButton(
                            onClick = onClear,
                            modifier = Modifier.testTag("button_clear_script")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Hapus",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Compact Textarea
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 130.dp)
                    .testTag("textarea_script"),
                placeholder = {
                    Text(
                        text = "Ketik atau tempelkan naskah cerita, berita, drama, atau narasi YouTube Anda di sini (maksimal 5.000 karakter)...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 22.sp
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isExceeded) MaterialTheme.colorScheme.error else YtRed,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                isError = isExceeded || validationError != null
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Character Counter and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Warning / Error banner text
                if (validationError != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = validationError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else if (isExceeded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Naskah terlalu panjang! Kurangi ${(charCount - charLimit)} karakter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else if (isNearLimit) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mendekati batas maksimal 5.000 karakter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningAmber,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "Tips: Beri tanda titik/koma untuk jeda narasi yang lebih alami.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Real-time counter badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        isExceeded -> MaterialTheme.colorScheme.errorContainer
                        isNearLimit -> WarningAmber.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.testTag("character_counter")
                ) {
                    Text(
                        text = "$formattedCount / $formattedLimit karakter",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = when {
                            isExceeded -> MaterialTheme.colorScheme.onErrorContainer
                            isNearLimit -> WarningAmber
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
