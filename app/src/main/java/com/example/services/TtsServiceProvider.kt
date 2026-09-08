package com.example.services

import android.content.Context

enum class TtsEngineType(val title: String, val description: String) {
    SYSTEM_NATIVE(
        "Mesin Sistem Android (Cepat & Offline)",
        "Menggunakan engine speech bawaan perangkat tanpa kuota internet."
    ),
    CLOUD_API(
        "Cloud / SaaS TTS Provider (Kustom API)",
        "Dapat dihubungkan ke server backend suara studio."
    )
}

class TtsServiceProvider(
    private val context: Context
) {
    private val systemService by lazy { AndroidSystemTtsService(context) }
    private var cloudService: CloudTtsService? = null

    private var activeType: TtsEngineType = TtsEngineType.SYSTEM_NATIVE

    fun getService(): TtsService {
        return when (activeType) {
            TtsEngineType.SYSTEM_NATIVE -> systemService
            TtsEngineType.CLOUD_API -> cloudService ?: CloudTtsService()
        }
    }

    fun setEngineType(type: TtsEngineType, apiKey: String? = null, endpoint: String? = null) {
        activeType = type
        if (type == TtsEngineType.CLOUD_API) {
            cloudService = CloudTtsService(apiKey, endpoint)
        }
    }

    fun getActiveEngineType(): TtsEngineType = activeType

    fun release() {
        systemService.release()
        cloudService?.release()
    }
}
