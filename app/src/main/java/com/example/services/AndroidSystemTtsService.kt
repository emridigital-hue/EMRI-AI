package com.example.services

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.TtsResult
import com.example.model.Voice
import com.example.model.VoiceSettings
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume

class AndroidSystemTtsService(
    private val context: Context
) : TtsService {

    override val providerName: String = "Android Native TTS Engine"

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var initDeferred: CompletableDeferred<Boolean>? = null
    private var currentPreviewOnDone: (() -> Unit)? = null

    override suspend fun initialize(): Boolean {
        if (isInitialized && textToSpeech != null) return true

        val deferred = CompletableDeferred<Boolean>()
        initDeferred = deferred

        withContext(Dispatchers.Main) {
            textToSpeech = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val tts = textToSpeech
                    // Try Indonesian locale first, fallback to device default or English
                    val idLocale = Locale("id", "ID")
                    val result = tts?.setLanguage(idLocale)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.w("AndroidSystemTts", "Indonesian language missing or not supported, falling back to default")
                        tts?.setLanguage(Locale.getDefault())
                    }
                    isInitialized = true
                    deferred.complete(true)
                } else {
                    Log.e("AndroidSystemTts", "Failed to initialize TextToSpeech: status=$status")
                    isInitialized = false
                    deferred.complete(false)
                }
            }
        }

        return deferred.await()
    }

    override suspend fun generateVoice(
        text: String,
        voice: Voice,
        settings: VoiceSettings,
        outputFile: File
    ): Result<TtsResult> = withContext(Dispatchers.IO) {
        if (!initialize()) {
            return@withContext Result.failure(Exception("Mesin TTS tidak dapat diinisialisasi pada perangkat."))
        }

        val tts = textToSpeech ?: return@withContext Result.failure(Exception("TextToSpeech null"))

        try {
            // Apply voice pitch & speed
            val finalPitch = (voice.basePitch * settings.pitch).coerceIn(0.5f, 2.0f)
            val finalRate = (voice.baseRate * settings.speed).coerceIn(0.5f, 2.0f)

            withContext(Dispatchers.Main) {
                tts.setPitch(finalPitch)
                tts.setSpeechRate(finalRate)
            }

            if (outputFile.exists()) {
                outputFile.delete()
            }
            outputFile.parentFile?.mkdirs()

            val utteranceId = "yt_voice_gen_" + UUID.randomUUID().toString()
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.volume.coerceIn(0f, 1f))
            }

            // Synthesize to file and wait for utterance completion
            val synthesisSuccess = suspendCancellableCoroutine<Boolean> { continuation ->
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(uttId: String?) {}

                    override fun onDone(uttId: String?) {
                        if (uttId == utteranceId && continuation.isActive) {
                            continuation.resume(true)
                        }
                    }

                    override fun onError(uttId: String?) {
                        if (uttId == utteranceId && continuation.isActive) {
                            continuation.resume(false)
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(uttId: String?, errorCode: Int) {
                        if (uttId == utteranceId && continuation.isActive) {
                            Log.e("AndroidSystemTts", "TTS error code: $errorCode")
                            continuation.resume(false)
                        }
                    }
                })

                val ret = tts.synthesizeToFile(text, params, outputFile, utteranceId)
                if (ret != TextToSpeech.SUCCESS) {
                    if (continuation.isActive) {
                        continuation.resume(false)
                    }
                }
            }

            if (!synthesisSuccess || !outputFile.exists() || outputFile.length() == 0L) {
                return@withContext Result.failure(Exception("Gagal membuat file suara dari mesin TTS."))
            }

            // Measure duration using MediaMetadataRetriever
            val durationMs = extractDuration(outputFile, text.length, finalRate)

            val snippet = if (text.length > 80) text.take(80) + "..." else text
            val result = TtsResult(
                audioFilePath = outputFile.absolutePath,
                audioFileName = outputFile.name,
                voiceId = voice.id,
                voiceName = voice.displayName,
                characterCount = text.length,
                durationMs = durationMs,
                timestamp = System.currentTimeMillis(),
                scriptSnippet = snippet
            )

            Result.success(result)
        } catch (e: Exception) {
            Log.e("AndroidSystemTts", "Exception during synthesis", e)
            Result.failure(e)
        }
    }

    override suspend fun previewVoice(
        voice: Voice,
        settings: VoiceSettings,
        onDone: () -> Unit
    ) {
        if (!initialize()) return
        val tts = textToSpeech ?: return

        withContext(Dispatchers.Main) {
            tts.stop()
            val finalPitch = (voice.basePitch * settings.pitch).coerceIn(0.5f, 2.0f)
            val finalRate = (voice.baseRate * settings.speed).coerceIn(0.5f, 2.0f)
            tts.setPitch(finalPitch)
            tts.setSpeechRate(finalRate)

            currentPreviewOnDone = onDone
            val previewId = "preview_" + UUID.randomUUID().toString()
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.volume.coerceIn(0f, 1f))
            }

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(uttId: String?) {}

                override fun onDone(uttId: String?) {
                    if (uttId == previewId) {
                        currentPreviewOnDone?.invoke()
                        currentPreviewOnDone = null
                    }
                }

                override fun onError(uttId: String?) {
                    if (uttId == previewId) {
                        currentPreviewOnDone?.invoke()
                        currentPreviewOnDone = null
                    }
                }
            })

            tts.speak(voice.previewSample, TextToSpeech.QUEUE_FLUSH, params, previewId)
        }
    }

    override fun stop() {
        textToSpeech?.stop()
        currentPreviewOnDone?.invoke()
        currentPreviewOnDone = null
    }

    override fun release() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("AndroidSystemTts", "Error shutting down TTS", e)
        }
    }

    private fun extractDuration(file: File, charLength: Int, rate: Float): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            durationStr?.toLongOrNull() ?: estimateDuration(charLength, rate)
        } catch (e: Exception) {
            estimateDuration(charLength, rate)
        }
    }

    private fun estimateDuration(charLength: Int, rate: Float): Long {
        // Average speaking rate in Indonesian: ~14 characters per second at 1.0x rate
        val seconds = (charLength / (14f * rate)).coerceAtLeast(1.0f)
        return (seconds * 1000).toLong()
    }
}
