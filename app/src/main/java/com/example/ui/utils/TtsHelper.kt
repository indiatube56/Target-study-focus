package com.example.ui.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

object TtsHelper {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    fun init(context: Context) {
        if (tts != null) return
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val hindiLocale = Locale("hi", "IN")
                val result = tts?.setLanguage(hindiLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("TtsHelper", "Hindi language is not supported or missing data, falling back to English")
                    tts?.language = Locale.US
                }
                isInitialized = true
                Log.d("TtsHelper", "TextToSpeech successfully initialized")
            } else {
                Log.e("TtsHelper", "TextToSpeech initialization failed")
            }
        }
    }

    fun speak(text: String, forceEnglish: Boolean = false) {
        if (!isInitialized) {
            Log.w("TtsHelper", "TTS not initialized yet")
            return
        }
        val cleanText = sanitizeForSpeech(text)
        val isHindi = containsHindi(text) && !forceEnglish
        tts?.language = if (isHindi) Locale("hi", "IN") else Locale.US
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "TargetFocusStudyId")
    }

    fun stop() {
        tts?.stop()
    }

    private fun containsHindi(text: String): Boolean {
        for (char in text) {
            if (char.code in 0x0900..0x097F) {
                return true
            }
        }
        return false
    }

    private fun sanitizeForSpeech(text: String): String {
        // Remove markdown elements like stars, lines, etc.
        return text.replace("**", "")
            .replace("*", "")
            .replace("#", "")
            .replace("`", "")
            .replace("📅", "")
            .replace("🌅", "")
            .replace("💤", "")
            .replace("🚀", "")
            .replace("🎯", "")
            .replace("📚", "")
            .replace("🚶‍♂️", "")
    }
}
