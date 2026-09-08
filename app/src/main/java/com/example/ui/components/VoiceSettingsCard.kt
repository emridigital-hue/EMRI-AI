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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.VoiceSettings
import com.example.ui.theme.YtRed
import java.util.Locale

@Composable
fun VoiceSettingsCard(
    settings: VoiceSettings,
    onSpeedChanged: (Float) -> Unit,
    onPitchChanged: (Float) -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_settings_card"),
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
            // Header with Reset button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = YtRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pengaturan Voice",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("button_reset_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reset Default",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Speed Slider (0.75x - 1.25x)
            SettingSliderItem(
                title = "Kecepatan (Speed)",
                valueLabel = String.format(Locale.getDefault(), "%.2fx %s", settings.speed, if (settings.speed == 1.0f) "(Normal)" else ""),
                icon = Icons.Default.Speed,
                value = settings.speed,
                range = 0.75f..1.25f,
                steps = 10,
                onValueChange = onSpeedChanged,
                testTag = "slider_speed"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Pitch Slider (0.75 - 1.25)
            val pitchLabel = when {
                settings.pitch < 0.90f -> "Rendah"
                settings.pitch > 1.10f -> "Tinggi"
                else -> "Normal"
            }
            SettingSliderItem(
                title = "Nada Suara (Pitch)",
                valueLabel = String.format(Locale.getDefault(), "%.2f (%s)", settings.pitch, pitchLabel),
                icon = Icons.Default.GraphicEq,
                value = settings.pitch,
                range = 0.75f..1.25f,
                steps = 10,
                onValueChange = onPitchChanged,
                testTag = "slider_pitch"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Volume Slider (0% - 100%)
            val volumePercentage = (settings.volume * 100).toInt()
            SettingSliderItem(
                title = "Volume Output",
                valueLabel = "$volumePercentage%",
                icon = Icons.Default.VolumeUp,
                value = settings.volume,
                range = 0.0f..1.0f,
                steps = 10,
                onValueChange = onVolumeChanged,
                testTag = "slider_volume"
            )
        }
    }
}

@Composable
private fun SettingSliderItem(
    title: String,
    valueLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = valueLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = YtRed,
                activeTrackColor = YtRed,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}
