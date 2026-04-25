package com.example.flashcard.data.local

import android.content.Context
import android.content.SharedPreferences

import java.time.LocalDate

private const val DEFAULT_API_KEY = "AIzaSyDmvXyItBFGC2vykKGy7QklKSSOhpGhxuw"
private const val PREF_API_KEY = "gemini_api_key"
private const val PREF_STREAK = "study_streak"
private const val PREF_LAST_STUDY_DATE = "last_study_date"
private const val PREF_CARDS_STUDIED_TODAY = "cards_studied_today"

private const val PREF_IS_REMINDER_ENABLED = "is_reminder_enabled"
private const val PREF_REMINDER_HOUR = "reminder_hour"
private const val PREF_REMINDER_MINUTE = "reminder_minute"
private const val PREF_IS_FOCUS_MODE_ENABLED = "is_focus_mode_enabled"

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

    // Reminders
    fun isReminderEnabled(): Boolean = prefs.getBoolean(PREF_IS_REMINDER_ENABLED, false)
    
    fun setReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(PREF_IS_REMINDER_ENABLED, enabled).apply()
    }

    fun getReminderHour(): Int = prefs.getInt(PREF_REMINDER_HOUR, 8)
    
    fun getReminderMinute(): Int = prefs.getInt(PREF_REMINDER_MINUTE, 0)

    fun setReminderTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(PREF_REMINDER_HOUR, hour)
            .putInt(PREF_REMINDER_MINUTE, minute)
            .apply()
    }

    // Focus Mode
    fun isFocusModeEnabled(): Boolean = prefs.getBoolean(PREF_IS_FOCUS_MODE_ENABLED, false)

    fun setFocusModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(PREF_IS_FOCUS_MODE_ENABLED, enabled).apply()
    }
}
