package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TtsResult
import com.example.services.AudioPlayerState
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.VoiceWaveCyan
import com.example.ui.theme.YtRed

@Composable
fun AudioPlayerCard(
    result: TtsResult?,
    playerState: AudioPlayerState,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onDownloadMp3: (filePath: String, fileName: String) -> Unit,
    onShare: (filePath: String, voiceName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_player_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (result != null) YtRed.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (result == null && playerState.activeFilePath == null) {
            // EMPTY STATE (Prompt Item 11)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .testTag("audio_player_empty_state"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Audiotrack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Belum ada audio",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Masukkan naskah hingga 5.000 karakter dan klik Generate Voice untuk membuat voice-over pertama Anda.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            // RESULT AUDIO PLAYER
            val activeVoiceName = playerState.activeVoiceName ?: result?.voiceName ?: "Voice-over"
            val filePath = playerState.activeFilePath ?: result?.audioFilePath ?: ""
            val fileName = result?.audioFileName ?: "yt_voice_audio.mp3"

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("audio_player_active_panel")
            ) {
                // Success Badge & Voice Name Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Voice berhasil dibuat!",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = SuccessGreen
                        )
                    }

                    // Voice Tag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = activeVoiceName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Playback Controls Row: Play/Pause + Timeline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play / Pause Circular Button
                    FilledTonalButton(
                        onClick = onPlayPause,
                        shape = CircleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = YtRed,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("button_play_pause")
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Progress Bar & Timestamp
                    Column(modifier = Modifier.weight(1f)) {
                        Slider(
                            value = playerState.progress,
                            onValueChange = onSeek,
                            colors = SliderDefaults.colors(
                                thumbColor = YtRed,
                                activeTrackColor = YtRed,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("slider_audio_progress")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = playerState.formattedCurrentTime,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = playerState.formattedDuration,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Volume row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vol: ${(playerState.volume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = playerState.volume,
                        onValueChange = onVolumeChanged,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            activeTrackColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("slider_player_volume")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Download MP3 and Share Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onDownloadMp3(filePath, fileName) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YtRed,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("button_download_mp3")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Download MP3",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = { onShare(filePath, activeVoiceName) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("button_share_audio")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bagikan",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
