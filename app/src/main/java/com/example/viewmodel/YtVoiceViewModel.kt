package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.HistoryEntity
import com.example.data.HistoryRepository
import com.example.model.TtsResult
import com.example.model.Voice
import com.example.model.VoiceCatalog
import com.example.model.VoiceGender
import com.example.model.VoiceSettings
import com.example.services.AudioPlayerController
import com.example.services.AudioPlayerState
import com.example.services.TtsEngineType
import com.example.services.TtsServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class DashboardTab(val label: String) {
    GENERATOR("Voice Generator"),
    HISTORY("History"),
    SETTINGS("Settings")
}

data class UiNotification(
    val message: String,
    val isError: Boolean = false,
    val id: Long = System.currentTimeMillis()
)

class YtVoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val database = AppDatabase.getInstance(context)
    private val repository = HistoryRepository(database.historyDao())

    val historyList: StateFlow<List<HistoryEntity>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val ttsProvider = TtsServiceProvider(context)
    val audioPlayerController = AudioPlayerController(context, viewModelScope)
    val playerState: StateFlow<AudioPlayerState> = audioPlayerController.playerState

    // Navigation state
    private val _currentTab = MutableStateFlow(DashboardTab.GENERATOR)
    val currentTab: StateFlow<DashboardTab> = _currentTab.asStateFlow()

    // Script text state
    private val _scriptText = MutableStateFlow("")
    val scriptText: StateFlow<String> = _scriptText.asStateFlow()

    // Character limit is strictly 5,000 characters
    val charLimit: Int = 5000
    val warningThreshold: Int = 4500

    // Voice Selection state
    private val _selectedGender = MutableStateFlow(VoiceGender.PRIA)
    val selectedGender: StateFlow<VoiceGender> = _selectedGender.asStateFlow()

    private val _selectedVoice = MutableStateFlow<Voice>(VoiceCatalog.maleVoices.first())
    val selectedVoice: StateFlow<Voice> = _selectedVoice.asStateFlow()

    // Voice Settings state
    private val _voiceSettings = MutableStateFlow(VoiceSettings.DEFAULT)
    val voiceSettings: StateFlow<VoiceSettings> = _voiceSettings.asStateFlow()

    // Preview state
    private val _previewingVoiceId = MutableStateFlow<String?>(null)
    val previewingVoiceId: StateFlow<String?> = _previewingVoiceId.asStateFlow()

    // Generation state
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationProgress = MutableStateFlow(0f)
    val generationProgress: StateFlow<Float> = _generationProgress.asStateFlow()

    // Last generated result
    private val _lastResult = MutableStateFlow<TtsResult?>(null)
    val lastResult: StateFlow<TtsResult?> = _lastResult.asStateFlow()

    // Notifications & Validation Error messages
    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    // Cloud TTS settings
    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _customApiEndpoint = MutableStateFlow("")
    val customApiEndpoint: StateFlow<String> = _customApiEndpoint.asStateFlow()

    private val _activeEngineType = MutableStateFlow(TtsEngineType.SYSTEM_NATIVE)
    val activeEngineType: StateFlow<TtsEngineType> = _activeEngineType.asStateFlow()

    init {
        viewModelScope.launch {
            ttsProvider.getService().initialize()
        }
    }

    fun setTab(tab: DashboardTab) {
        _currentTab.value = tab
    }

    fun onScriptTextChanged(newText: String) {
        // Do not auto-truncate: let user see if exceeded limit
        _scriptText.value = newText
        if (_validationError.value != null && newText.isNotBlank() && newText.length <= charLimit) {
            _validationError.value = null
        }
    }

    fun setSampleScript() {
        val sample = "Halo semuanya! Selamat datang di channel kami. Hari ini kita akan membahas sebuah fakta menarik yang jarang diketahui banyak orang. Simak penjelasannya sampai selesai, jangan lupa like dan subscribe!"
        onScriptTextChanged(sample)
    }

    fun clearScriptText() {
        _scriptText.value = ""
        _validationError.value = null
    }

    fun onGenderSelected(gender: VoiceGender) {
        _selectedGender.value = gender
        val voices = if (gender == VoiceGender.PRIA) VoiceCatalog.maleVoices else VoiceCatalog.femaleVoices
        // Default to first voice in the selected category
        _selectedVoice.value = voices.first()
        stopPreview()
    }

    fun onVoiceSelected(voice: Voice) {
        _selectedVoice.value = voice
        _selectedGender.value = voice.gender
        if (_validationError.value != null) {
            _validationError.value = null
        }
    }

    fun onSpeedChanged(speed: Float) {
        _voiceSettings.value = _voiceSettings.value.copy(speed = (speed * 100).toInt() / 100f)
    }

    fun onPitchChanged(pitch: Float) {
        _voiceSettings.value = _voiceSettings.value.copy(pitch = (pitch * 100).toInt() / 100f)
    }

    fun onVolumeChanged(volume: Float) {
        _voiceSettings.value = _voiceSettings.value.copy(volume = (volume * 100).toInt() / 100f)
    }

    fun resetSettings() {
        _voiceSettings.value = VoiceSettings.DEFAULT
        audioPlayerController.setVolume(1.0f)
        showNotification("Pengaturan suara telah direset ke default.")
    }

    fun previewVoice(voice: Voice) {
        if (_previewingVoiceId.value == voice.id) {
            stopPreview()
            return
        }

        stopAudioPlayback()
        _previewingVoiceId.value = voice.id

        viewModelScope.launch {
            val service = ttsProvider.getService()
            service.previewVoice(voice, _voiceSettings.value) {
                _previewingVoiceId.value = null
            }
        }
    }

    fun stopPreview() {
        ttsProvider.getService().stop()
        _previewingVoiceId.value = null
    }

    fun generateVoice() {
        val text = _scriptText.value.trim()
        val voice = _selectedVoice.value

        // Validation 1: Text empty
        if (text.isEmpty()) {
            _validationError.value = "Teks naskah tidak boleh kosong."
            showNotification("Teks naskah tidak boleh kosong.", isError = true)
            return
        }

        // Validation 2: Character limit exceeded
        if (text.length > charLimit) {
            val errorMsg = "Naskah terlalu panjang. Maksimal 5.000 karakter untuk sekali Generate."
            _validationError.value = errorMsg
            showNotification(errorMsg, isError = true)
            return
        }

        // Prevent double generation
        if (_isGenerating.value) return

        _validationError.value = null
        _isGenerating.value = true
        _generationProgress.value = 0.15f
        stopPreview()
        stopAudioPlayback()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _generationProgress.value = 0.40f
                val service = ttsProvider.getService()

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val safeVoice = voice.name.replace(" ", "_").lowercase()
                val fileName = "ytvoice_${safeVoice}_${timeStamp}.wav"
                val audioDir = File(context.filesDir, "audio_output").apply { mkdirs() }
                val targetFile = File(audioDir, fileName)

                _generationProgress.value = 0.70f
                val result = service.generateVoice(
                    text = text,
                    voice = voice,
                    settings = _voiceSettings.value,
                    outputFile = targetFile
                )

                _generationProgress.value = 1.0f

                result.onSuccess { ttsResult ->
                    _lastResult.value = ttsResult
                    _isGenerating.value = false

                    // Save to Room database
                    val historyRecord = HistoryEntity(
                        timestamp = ttsResult.timestamp,
                        voiceName = ttsResult.voiceName,
                        voiceId = ttsResult.voiceId,
                        characterCount = ttsResult.characterCount,
                        durationMs = ttsResult.durationMs,
                        filePath = ttsResult.audioFilePath,
                        fileName = ttsResult.audioFileName,
                        scriptSnippet = ttsResult.scriptSnippet
                    )
                    repository.insert(historyRecord)

                    showNotification("Voice berhasil dibuat!")

                    // Automatically prepare & play result in audio player
                    audioPlayerController.play(
                        filePath = ttsResult.audioFilePath,
                        voiceName = ttsResult.voiceName,
                        estimatedDurationMs = ttsResult.durationMs
                    )
                }.onFailure { error ->
                    _isGenerating.value = false
                    val errorMsg = error.message ?: "Gagal membuat voice. Silakan coba lagi."
                    showNotification(errorMsg, isError = true)
                }
            } catch (e: Exception) {
                _isGenerating.value = false
                val errorMsg = e.message ?: "Gagal membuat voice. Silakan coba lagi."
                showNotification(errorMsg, isError = true)
            }
        }
    }

    fun playCurrentResult() {
        val result = _lastResult.value ?: return
        if (playerState.value.activeFilePath == result.audioFilePath) {
            if (playerState.value.isPlaying) {
                audioPlayerController.pause()
            } else {
                audioPlayerController.resume()
            }
        } else {
            audioPlayerController.play(
                filePath = result.audioFilePath,
                voiceName = result.voiceName,
                estimatedDurationMs = result.durationMs
            )
        }
    }

    fun playHistoryItem(item: HistoryEntity) {
        stopPreview()
        if (playerState.value.activeFilePath == item.filePath) {
            if (playerState.value.isPlaying) {
                audioPlayerController.pause()
            } else {
                audioPlayerController.resume()
            }
        } else {
            audioPlayerController.play(
                filePath = item.filePath,
                voiceName = item.voiceName,
                estimatedDurationMs = item.durationMs
            )
        }
    }

    fun deleteHistoryItem(item: HistoryEntity) {
        viewModelScope.launch {
            if (playerState.value.activeFilePath == item.filePath) {
                audioPlayerController.stop()
            }
            // Delete file
            try {
                val file = File(item.filePath)
                if (file.exists()) file.delete()
            } catch (_: Exception) {}

            repository.deleteById(item.id)
            showNotification("Riwayat narasi dihapus.")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            audioPlayerController.stop()
            repository.clearAll()
            showNotification("Semua riwayat narasi telah dibersihkan.")
        }
    }

    fun downloadOrExport(filePath: String, fileName: String) {
        val mp3FileName = if (fileName.endsWith(".wav")) {
            fileName.removeSuffix(".wav") + ".mp3"
        } else if (!fileName.endsWith(".mp3")) {
            "$fileName.mp3"
        } else fileName

        val result = audioPlayerController.downloadOrExportAudio(filePath, mp3FileName)
        result.onSuccess { message ->
            showNotification(message)
        }.onFailure { err ->
            showNotification(err.message ?: "Gagal mengunduh audio.", isError = true)
        }
    }

    fun shareAudio(filePath: String, voiceName: String) {
        audioPlayerController.shareAudio(filePath, "YT Voice - $voiceName")
    }

    fun updateEngineConfig(type: TtsEngineType, apiKey: String, endpoint: String) {
        _activeEngineType.value = type
        _customApiKey.value = apiKey
        _customApiEndpoint.value = endpoint
        ttsProvider.setEngineType(type, apiKey, endpoint)
        showNotification("Konfigurasi mesin suara diperbarui.")
    }

    fun stopAudioPlayback() {
        audioPlayerController.pause()
    }

    fun dismissNotification() {
        _notification.value = null
    }

    private fun showNotification(msg: String, isError: Boolean = false) {
        _notification.value = UiNotification(message = msg, isError = isError)
    }

    override fun onCleared() {
        super.onCleared()
        ttsProvider.release()
        audioPlayerController.release()
    }
}
