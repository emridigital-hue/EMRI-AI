package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_records")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val voiceName: String,
    val voiceId: String,
    val characterCount: Int,
    val durationMs: Long,
    val filePath: String,
    val fileName: String,
    val scriptSnippet: String
)
