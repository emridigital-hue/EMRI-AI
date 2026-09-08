package com.example.model

enum class VoiceGender(val label: String) {
    PRIA("Suara Pria"),
    WANITA("Suara Wanita")
}

data class Voice(
    val id: String,
    val name: String,
    val character: String,
    val gender: VoiceGender,
    val basePitch: Float,
    val baseRate: Float,
    val description: String,
    val previewSample: String
) {
    val displayName: String
        get() = "$name — $character"
}

data class VoiceSettings(
    val speed: Float = 1.0f,    // 0.75f - 1.25f
    val pitch: Float = 1.0f,    // 0.75f - 1.25f
    val volume: Float = 1.0f    // 0.0f - 1.0f
) {
    companion object {
        val DEFAULT = VoiceSettings(speed = 1.0f, pitch = 1.0f, volume = 1.0f)
    }
}

data class TtsResult(
    val audioFilePath: String,
    val audioFileName: String,
    val voiceId: String,
    val voiceName: String,
    val characterCount: Int,
    val durationMs: Long,
    val timestamp: Long,
    val scriptSnippet: String
)

object VoiceCatalog {
    val maleVoices = listOf(
        Voice(
            id = "pria_1",
            name = "Pria 1",
            character = "Natural",
            gender = VoiceGender.PRIA,
            basePitch = 0.95f,
            baseRate = 1.0f,
            description = "Suara netral, jernih & artikulasi seimbang untuk video fakta atau edukasi.",
            previewSample = "Halo kreator YouTube. Ini adalah contoh suara Pria Satu dengan karakter natural untuk narasi Anda."
        ),
        Voice(
            id = "pria_2",
            name = "Pria 2",
            character = "Deep",
            gender = VoiceGender.PRIA,
            basePitch = 0.80f,
            baseRate = 0.95f,
            description = "Suara berat, berbobot & mantap, sangat pas untuk misteri, trailer, dan dokumenter.",
            previewSample = "Di balik kegelapan malam, kisah ini baru saja dimulai. Ini adalah suara Pria Dua dengan karakter deep."
        ),
        Voice(
            id = "pria_3",
            name = "Pria 3",
            character = "Warm",
            gender = VoiceGender.PRIA,
            basePitch = 0.90f,
            baseRate = 1.0f,
            description = "Suara hangat, ramah & bersahabat untuk video motivasi, finansial, dan tutorial.",
            previewSample = "Selamat datang kembali di channel ini. Ini adalah karakter suara Pria Tiga yang hangat dan bersahabat."
        ),
        Voice(
            id = "pria_4",
            name = "Pria 4",
            character = "Storyteller",
            gender = VoiceGender.PRIA,
            basePitch = 0.88f,
            baseRate = 0.92f,
            description = "Suara ekspresif dan mendalam untuk alur cerita drama, dongeng, dan narasi sejarah.",
            previewSample = "Alkisah, pada sebuah masa yang jauh dari peradaban. Ini karakter Pria Empat, khusus bercerita dan storytelling."
        )
    )

    val femaleVoices = listOf(
        Voice(
            id = "wanita_1",
            name = "Wanita 1",
            character = "Natural",
            gender = VoiceGender.WANITA,
            basePitch = 1.15f,
            baseRate = 1.0f,
            description = "Suara perempuan jernih, profesional & modern untuk video ulasan dan konten umum.",
            previewSample = "Halo semuanya! Ini adalah contoh suara Wanita Satu dengan karakter vokal natural dan jernih."
        ),
        Voice(
            id = "wanita_2",
            name = "Wanita 2",
            character = "Soft",
            gender = VoiceGender.WANITA,
            basePitch = 1.20f,
            baseRate = 0.95f,
            description = "Suara lembut, menenangkan & halus, cocok untuk video meditasi, puisi, dan vlog santai.",
            previewSample = "Tarik napas perlahan dan rasakan ketenangan. Ini adalah contoh vokal Wanita Dua dengan nuansa lembut."
        ),
        Voice(
            id = "wanita_3",
            name = "Wanita 3",
            character = "Warm",
            gender = VoiceGender.WANITA,
            basePitch = 1.08f,
            baseRate = 1.0f,
            description = "Suara hangat, ceria & antusias untuk video lifestyle, kuliner, dan tips harian.",
            previewSample = "Yuk kita bahas tips praktis hari ini. Ini karakter suara Wanita Tiga yang hangat dan menyenangkan."
        ),
        Voice(
            id = "wanita_4",
            name = "Wanita 4",
            character = "Storyteller",
            gender = VoiceGender.WANITA,
            basePitch = 1.12f,
            baseRate = 0.94f,
            description = "Suara puitis dan teatrikal untuk pembacaan buku, kisah romansa, dan drama emosional.",
            previewSample = "Di sudut kota itu, sebuah rahasia tersimpan rapi. Ini adalah suara Wanita Empat dengan gaya narator cerita."
        )
    )

    val allVoices = maleVoices + femaleVoices

    fun findById(id: String): Voice {
        return allVoices.firstOrNull { it.id == id } ?: maleVoices.first()
    }
}
