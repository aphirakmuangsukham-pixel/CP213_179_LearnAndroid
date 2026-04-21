package com.example.flashcard.data.local

import android.content.Context
import android.content.SharedPreferences

private const val DEFAULT_API_KEY = "AIzaSyDmvXyItBFGC2vykKGy7QklKSSOhpGhxuw"
private const val PREF_API_KEY = "gemini_api_key"

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("flashcard_settings", Context.MODE_PRIVATE)

    fun getGeminiApiKey(): String {
        val saved = prefs.getString(PREF_API_KEY, "").orEmpty()
        return saved.ifBlank { DEFAULT_API_KEY }
    }

    fun saveGeminiApiKey(apiKey: String) {
        prefs.edit().putString(PREF_API_KEY, apiKey).apply()
    }
}
