package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HistoryEntity
import com.example.services.AudioPlayerState
import com.example.ui.theme.YtRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryCard(
    historyList: List<HistoryEntity>,
    playerState: AudioPlayerState,
    onPlayItem: (HistoryEntity) -> Unit,
    onDownloadItem: (filePath: String, fileName: String) -> Unit,
    onDeleteItem: (HistoryEntity) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var itemToDelete by remember { mutableStateOf<HistoryEntity?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("history_panel_card"),
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
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = YtRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Riwayat Voice-over",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (historyList.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${historyList.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (historyList.isNotEmpty()) {
                    TextButton(
                        onClick = { showClearConfirm = true },
                        modifier = Modifier.testTag("button_clear_all_history")
                    ) {
                        Text(
                            text = "Hapus Semua",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (historyList.isEmpty()) {
                // Empty state per user prompt:
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 12.dp)
                        .testTag("history_empty_state"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Audiotrack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Belum ada audio",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Masukkan naskah hingga 5.000 karakter dan klik Generate Voice untuk membuat voice-over pertama Anda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    historyList.forEach { item ->
                        val isItemPlaying = playerState.isPlaying && playerState.activeFilePath == item.filePath

                        HistoryItemRow(
                            item = item,
                            isPlaying = isItemPlaying,
                            onPlay = { onPlayItem(item) },
                            onDownload = { onDownloadItem(item.filePath, item.fileName) },
                            onDelete = { itemToDelete = item }
                        )
                    }
                }
            }
        }
    }

    // Confirmation dialog for single delete
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus Voice-over?") },
            text = { Text("Apakah Anda yakin ingin menghapus audio narasi \"${item.voiceName}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteItem(item)
                        itemToDelete = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Confirmation dialog for clear all
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Hapus Semua Riwayat?") },
            text = { Text("Semua file rekaman audio voice-over di riwayat akan dihapus secara permanen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        showClearConfirm = false
                    }
                ) {
                    Text("Hapus Semua", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun HistoryItemRow(
    item: HistoryEntity,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember(item.timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        sdf.format(Date(item.timestamp))
    }

    val formattedDuration = remember(item.durationMs) {
        val totalSec = (item.durationMs / 1000).coerceAtLeast(0)
        val min = totalSec / 60
        val sec = totalSec % 60
        String.format(Locale.getDefault(), "%02d:%02d", min, sec)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("history_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(
            1.dp,
            if (isPlaying) YtRed else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Voice name badge & date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = YtRed.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = item.voiceName,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = YtRed,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Action buttons: Play, Download, Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Play / Pause Button
                    IconButton(
                        onClick = onPlay,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("history_play_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = if (isPlaying) YtRed else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Download Button
                    IconButton(
                        onClick = onDownload,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("history_download_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download MP3",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("history_delete_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Script snippet
            Text(
                text = item.scriptSnippet,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stats row: Characters & Audio Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.characterCount} karakter",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Durasi $formattedDuration",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
