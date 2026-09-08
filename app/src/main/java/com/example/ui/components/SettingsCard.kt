package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.services.TtsEngineType
import com.example.ui.theme.YtRed

@Composable
fun SettingsCard(
    activeEngineType: TtsEngineType,
    customApiKey: String,
    customEndpoint: String,
    onSaveConfig: (type: TtsEngineType, apiKey: String, endpoint: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember(activeEngineType) { mutableStateOf(activeEngineType) }
    var apiKeyInput by remember(customApiKey) { mutableStateOf(customApiKey) }
    var endpointInput by remember(customEndpoint) { mutableStateOf(customEndpoint) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = YtRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pengaturan Engine & API TTS",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Pilih Engine Penyintesis Suara:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 1: System Native
            EngineOptionRow(
                title = TtsEngineType.SYSTEM_NATIVE.title,
                description = TtsEngineType.SYSTEM_NATIVE.description,
                icon = Icons.Default.PhoneAndroid,
                isSelected = selectedType == TtsEngineType.SYSTEM_NATIVE,
                onSelect = { selectedType = TtsEngineType.SYSTEM_NATIVE },
                testTag = "engine_option_native"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 2: Cloud TTS
            EngineOptionRow(
                title = TtsEngineType.CLOUD_API.title,
                description = TtsEngineType.CLOUD_API.description,
                icon = Icons.Default.CloudQueue,
                isSelected = selectedType == TtsEngineType.CLOUD_API,
                onSelect = { selectedType = TtsEngineType.CLOUD_API },
                testTag = "engine_option_cloud"
            )

            if (selectedType == TtsEngineType.CLOUD_API) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = endpointInput,
                    onValueChange = { endpointInput = it },
                    label = { Text("API Endpoint URL") },
                    placeholder = { Text("https://api.your-tts-provider.com/v1/synthesize") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_cloud_endpoint"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    label = { Text("API Secret Key (Disimpan aman)") },
                    placeholder = { Text("Masukkan API key") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_cloud_key"),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onSaveConfig(selectedType, apiKeyInput, endpointInput) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YtRed,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("button_save_settings")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Simpan Pengaturan",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Information Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tentang YT Voice Studio",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Aplikasi Text-to-Speech (TTS) khusus kreator YouTube untuk membuat narasi cerita, drama, fakta, dan edukasi dengan batas 5.000 karakter per proses. Hasil suara berformat MP3 siap pakai untuk software editing video.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EngineOptionRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        border = BorderStroke(
            1.dp,
            if (isSelected) YtRed else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = YtRed)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) YtRed else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
