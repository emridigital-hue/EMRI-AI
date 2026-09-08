package com.example.services

import com.example.model.TtsResult
import com.example.model.Voice
import com.example.model.VoiceSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Cloud/API-based TTS Provider template.
 * Easily pluggable for custom SaaS backend (e.g. OpenAI TTS, ElevenLabs, Google Cloud Text-to-Speech).
 * Strictly avoids fake audio: errors out if backend/API key is not configured.
 */
class CloudTtsService(
    private val apiKey: String? = null,
    private val apiEndpoint: String? = null
) : TtsService {

    override val providerName: String = "Cloud AI TTS (SaaS Ready)"

    override suspend fun initialize(): Boolean {
        // Only initializes if API endpoint/key is provided
        return !apiKey.isNullOrBlank() && !apiEndpoint.isNullOrBlank()
    }

    override suspend fun generateVoice(
        text: String,
        voice: Voice,
        settings: VoiceSettings,
        outputFile: File
    ): Result<TtsResult> = withContext(Dispatchers.IO) {
        if (!initialize()) {
            return@withContext Result.failure(
                IllegalStateException("Cloud TTS belum dikonfigurasi. Harap gunakan Android Native Engine atau masukkan konfigurasi API di Pengaturan.")
            )
        }

        // Implementation point for remote HTTP POST request to SaaS / Cloud TTS
        Result.failure(NotImplementedError("Endpoint Cloud TTS belum terhubung."))
    }

    override suspend fun previewVoice(
        voice: Voice,
        settings: VoiceSettings,
        onDone: () -> Unit
    ) {
        onDone()
    }

    override fun stop() {}

    override fun release() {}
}
