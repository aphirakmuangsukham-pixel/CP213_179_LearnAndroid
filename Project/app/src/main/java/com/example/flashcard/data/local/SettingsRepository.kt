package com.example.flashcard.data.local

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("flashcard_settings", Context.MODE_PRIVATE)

    fun getGeminiApiKey(): String? {
        return prefs.getString("gemini_api_key", "")
    }

    fun saveGeminiApiKey(apiKey: String) {
        prefs.edit().putString("gemini_api_key", apiKey).apply()
    }
}
