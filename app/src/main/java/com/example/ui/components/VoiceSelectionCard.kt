package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Voice
import com.example.model.VoiceCatalog
import com.example.model.VoiceGender
import com.example.ui.theme.VoiceWaveBlue
import com.example.ui.theme.VoiceWaveCyan
import com.example.ui.theme.YtRed

@Composable
fun VoiceSelectionCard(
    selectedGender: VoiceGender,
    selectedVoice: Voice,
    previewingVoiceId: String?,
    onGenderSelected: (VoiceGender) -> Unit,
    onVoiceSelected: (Voice) -> Unit,
    onPreviewClicked: (Voice) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_selection_card"),
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.RecordVoiceOver,
                        contentDescription = null,
                        tint = YtRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pilihan Suara Narator",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Currently selected badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = YtRed.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = selectedVoice.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = YtRed,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gender Switch Tabs (Pria / Wanita)
            TabRow(
                selectedTabIndex = selectedGender.ordinal,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedGender.ordinal]),
                        color = YtRed,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .testTag("gender_tabs")
            ) {
                VoiceGender.entries.forEach { gender ->
                    val isSelected = selectedGender == gender
                    Tab(
                        selected = isSelected,
                        onClick = { onGenderSelected(gender) },
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .testTag("gender_tab_${gender.name.lowercase()}"),
                        text = {
                            Text(
                                text = gender.label,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // List of Voices in the current gender category
            val voices = if (selectedGender == VoiceGender.PRIA) VoiceCatalog.maleVoices else VoiceCatalog.femaleVoices

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                voices.forEach { voice ->
                    val isSelected = selectedVoice.id == voice.id
                    val isPreviewing = previewingVoiceId == voice.id

                    VoiceItemCard(
                        voice = voice,
                        isSelected = isSelected,
                        isPreviewing = isPreviewing,
                        onSelect = { onVoiceSelected(voice) },
                        onPreview = { onPreviewClicked(voice) }
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceItemCard(
    voice: Voice,
    isSelected: Boolean,
    isPreviewing: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) YtRed else MaterialTheme.colorScheme.outlineVariant,
        label = "border_color"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        },
        label = "container_color"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .testTag("voice_card_${voice.id}"),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Radio & Voice info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = YtRed
                    ),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = voice.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) YtRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = voice.character,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) YtRed else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = voice.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right: Preview button
            OutlinedButton(
                onClick = onPreview,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isPreviewing) YtRed else MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(
                    1.dp,
                    if (isPreviewing) YtRed else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.testTag("preview_button_${voice.id}")
            ) {
                Icon(
                    imageVector = if (isPreviewing) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPreviewing) "Stop Preview" else "Coba Preview",
                    modifier = Modifier.size(16.dp),
                    tint = if (isPreviewing) YtRed else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isPreviewing) "Stop" else "Preview",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
