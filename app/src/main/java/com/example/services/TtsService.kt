package com.example.services

import com.example.model.TtsResult
import com.example.model.Voice
import com.example.model.VoiceSettings
import java.io.File

interface TtsService {
    val providerName: String

    /**
     * Initializes the TTS engine if needed.
     */
    suspend fun initialize(): Boolean

    /**
     * Synthesizes the given script text into an actual audio file.
     */
    suspend fun generateVoice(
        text: String,
        voice: Voice,
        settings: VoiceSettings,
        outputFile: File
    ): Result<TtsResult>

    /**
     * Plays a quick live preview of the voice character.
     */
    suspend fun previewVoice(
        voice: Voice,
        settings: VoiceSettings,
        onDone: () -> Unit = {}
    )

    /**
     * Stops any currently active synthesis or preview playback.
     */
    fun stop()

    /**
     * Releases system resources.
     */
    fun release()
}
