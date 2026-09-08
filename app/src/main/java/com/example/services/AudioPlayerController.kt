package com.example.services

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isPrepared: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val activeFilePath: String? = null,
    val activeVoiceName: String? = null,
    val volume: Float = 1.0f
) {
    val progress: Float
        get() = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val formattedCurrentTime: String
        get() = formatTime(currentPositionMs)

    val formattedDuration: String
        get() = formatTime(durationMs)

    private fun formatTime(ms: Long): String {
        val totalSec = (ms / 1000).coerceAtLeast(0)
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec)
    }
}

class AudioPlayerController(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    fun play(filePath: String, voiceName: String, estimatedDurationMs: Long = 0L) {
        try {
            stop()
            val file = File(filePath)
            if (!file.exists()) {
                Log.e("AudioPlayer", "Audio file not found: $filePath")
                return
            }

            val player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener { mp ->
                    val actualDuration = if (mp.duration > 0) mp.duration.toLong() else estimatedDurationMs
                    _playerState.value = _playerState.value.copy(
                        isPrepared = true,
                        isPlaying = true,
                        durationMs = actualDuration,
                        currentPositionMs = 0L,
                        activeFilePath = filePath,
                        activeVoiceName = voiceName
                    )
                    mp.start()
                    startProgressTracker()
                }
                setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        currentPositionMs = _playerState.value.durationMs
                    )
                    stopProgressTracker()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayer", "MediaPlayer error: what=$what extra=$extra")
                    stop()
                    true
                }
                val vol = _playerState.value.volume
                setVolume(vol, vol)
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error starting playback", e)
            stop()
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playerState.value = _playerState.value.copy(isPlaying = false)
                stopProgressTracker()
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _playerState.value = _playerState.value.copy(isPlaying = true)
                startProgressTracker()
            }
        }
    }

    fun seekTo(progressFraction: Float) {
        mediaPlayer?.let { player ->
            val targetMs = (_playerState.value.durationMs * progressFraction.coerceIn(0f, 1f)).toInt()
            player.seekTo(targetMs)
            _playerState.value = _playerState.value.copy(currentPositionMs = targetMs.toLong())
        }
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _playerState.value = _playerState.value.copy(volume = clamped)
        mediaPlayer?.setVolume(clamped, clamped)
    }

    fun stop() {
        stopProgressTracker()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error releasing MediaPlayer", e)
        }
        mediaPlayer = null
        _playerState.value = _playerState.value.copy(
            isPlaying = false,
            isPrepared = false,
            currentPositionMs = 0L
        )
    }

    fun release() {
        stop()
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = coroutineScope.launch(Dispatchers.Main) {
            while (isActive && mediaPlayer?.isPlaying == true) {
                val current = mediaPlayer?.currentPosition?.toLong() ?: 0L
                _playerState.value = _playerState.value.copy(currentPositionMs = current)
                delay(200)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    /**
     * Exports/saves the audio file into public storage (Downloads) or shares it via Android system share.
     */
    fun downloadOrExportAudio(
        sourceFilePath: String,
        targetFileName: String = "yt_voice_${System.currentTimeMillis()}.mp3"
    ): Result<String> {
        val sourceFile = File(sourceFilePath)
        if (!sourceFile.exists()) {
            return Result.failure(Exception("File sumber audio tidak ditemukan."))
        }

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, targetFileName)
                    put(MediaStore.Downloads.MIME_TYPE, "audio/mpeg")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/YTVoice")
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: return Result.failure(Exception("Gagal mengakses penyimpanan Downloads."))

                context.contentResolver.openOutputStream(uri)?.use { out ->
                    FileInputStream(sourceFile).use { input ->
                        input.copyTo(out)
                    }
                }
                Result.success("Tersimpan di folder Downloads/YTVoice/$targetFileName")
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val targetDir = File(downloadsDir, "YTVoice").apply { mkdirs() }
                val targetFile = File(targetDir, targetFileName)
                FileInputStream(sourceFile).use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                Result.success("Tersimpan di: ${targetFile.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Failed saving audio file", e)
            Result.failure(e)
        }
    }

    /**
     * Opens system share intent to share audio file to other apps (WhatsApp, YouTube editor, Drive, etc.)
     */
    fun shareAudio(sourceFilePath: String, title: String) {
        val file = File(sourceFilePath)
        if (!file.exists()) return

        try {
            val uri: Uri = try {
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } catch (e: Exception) {
                Uri.fromFile(file)
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Audio Voice-over dibuat dengan YT Voice.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Voice-over").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error sharing audio file", e)
        }
    }
}
