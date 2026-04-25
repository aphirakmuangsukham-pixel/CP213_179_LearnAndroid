package com.example.flashcard.data.local

import android.content.Context
import android.content.SharedPreferences

import java.time.LocalDate

private const val DEFAULT_API_KEY = "AIzaSyDmvXyItBFGC2vykKGy7QklKSSOhpGhxuw"
private const val PREF_API_KEY = "gemini_api_key"
private const val PREF_STREAK = "study_streak"
private const val PREF_LAST_STUDY_DATE = "last_study_date"
private const val PREF_CARDS_STUDIED_TODAY = "cards_studied_today"

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

    fun getStreak(): Int {
        return prefs.getInt(PREF_STREAK, 0)
    }

    fun getCardsStudiedToday(): Int {
        val lastDate = prefs.getLong(PREF_LAST_STUDY_DATE, 0L)
        val today = LocalDate.now().toEpochDay()
        return if (lastDate == today) {
            prefs.getInt(PREF_CARDS_STUDIED_TODAY, 0)
        } else {
            0
        }
    }

    fun updateStudyStats(cardsStudied: Int) {
        val today = LocalDate.now().toEpochDay()
        val lastDate = prefs.getLong(PREF_LAST_STUDY_DATE, 0L)
        var streak = prefs.getInt(PREF_STREAK, 0)
        var currentDailyCards = prefs.getInt(PREF_CARDS_STUDIED_TODAY, 0)

        if (lastDate == today) {
            currentDailyCards += cardsStudied
        } else if (today - lastDate == 1L) {
            streak += 1
            currentDailyCards = cardsStudied
        } else {
            streak = 1
            currentDailyCards = cardsStudied
        }

        prefs.edit()
            .putLong(PREF_LAST_STUDY_DATE, today)
            .putInt(PREF_STREAK, streak)
            .putInt(PREF_CARDS_STUDIED_TODAY, currentDailyCards)
            .apply()
    }
}
