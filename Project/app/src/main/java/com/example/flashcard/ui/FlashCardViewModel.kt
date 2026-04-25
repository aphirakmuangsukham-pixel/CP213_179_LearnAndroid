package com.example.flashcard.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard.data.local.AppDatabase
import com.example.flashcard.data.local.Category
import com.example.flashcard.data.local.FlashCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.LocalDate
import kotlin.random.Random

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import com.example.flashcard.data.remote.AiFlashcardResponse

class FlashCardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val categoryDao = db.categoryDao()
    private val flashCardDao = db.flashCardDao()
    private val settingsRepository = com.example.flashcard.data.local.SettingsRepository(application)

    val categories = categoryDao.getAllCategories()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _cardsStudiedToday = MutableStateFlow(0)
    val cardsStudiedToday: StateFlow<Int> = _cardsStudiedToday.asStateFlow()

    init {
        loadStats()
        preloadSampleDataIfNeeded()
    }

    private fun preloadSampleDataIfNeeded() {
        viewModelScope.launch {
            val existingCategories = categoryDao.getAllCategories().first()
            if (existingCategories.isEmpty()) {
                val sampleId = categoryDao.insertCategory(Category(name = "General Knowledge")).toInt()
                flashCardDao.insertCard(FlashCard(categoryId = sampleId, frontText = "What is the capital of France?", backText = "Paris"))
                flashCardDao.insertCard(FlashCard(categoryId = sampleId, frontText = "What is 8 x 7?", backText = "56"))
                flashCardDao.insertCard(FlashCard(categoryId = sampleId, frontText = "Which planet is known as the Red Planet?", backText = "Mars"))
                flashCardDao.insertCard(FlashCard(categoryId = sampleId, frontText = "What is the chemical symbol for water?", backText = "H2O"))
                flashCardDao.insertCard(FlashCard(categoryId = sampleId, frontText = "Who painted the Mona Lisa?", backText = "Leonardo da Vinci"))
            }
        }
    }

    fun loadStats() {
        _streak.value = settingsRepository.getStreak()
        _cardsStudiedToday.value = settingsRepository.getCardsStudiedToday()
    }

    private val _cardsForCategory = MutableStateFlow<List<FlashCard>>(emptyList())
    val cardsForCategory: StateFlow<List<FlashCard>> = _cardsForCategory.asStateFlow()

    fun loadCardsForCategory(categoryId: Int) {
        viewModelScope.launch {
            flashCardDao.getCardsByCategory(categoryId).collect { cards ->
                _cardsForCategory.value = cards
            }
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            categoryDao.insertCategory(Category(name = name))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.deleteCategory(category)
        }
    }

    fun addCard(categoryId: Int, frontText: String, backText: String) {
        viewModelScope.launch {
            flashCardDao.insertCard(FlashCard(categoryId = categoryId, frontText = frontText, backText = backText))
        }
    }

    fun updateCard(card: FlashCard) {
        viewModelScope.launch {
            flashCardDao.updateCard(card)
        }
    }

    fun deleteCard(card: FlashCard) {
        viewModelScope.launch {
            flashCardDao.deleteCard(card)
        }
    }

    fun finishStudySession(categoryId: Int, correctCount: Int, totalCards: Int) {
        viewModelScope.launch {
            if (totalCards > 0) {
                val accuracy = (correctCount.toFloat() / totalCards) * 100f
                val category = categoryDao.getCategoryById(categoryId)
                if (category != null) {
                    val updatedCategory = category.copy(lastAccuracy = accuracy)
                    categoryDao.updateCategory(updatedCategory)
                }
                settingsRepository.updateStudyStats(totalCards)
                loadStats()
            }
        }
    }

    fun getGeminiApiKey(): String {
        return settingsRepository.getGeminiApiKey() ?: ""
    }

    fun saveGeminiApiKey(key: String) {
        settingsRepository.saveGeminiApiKey(key)
    }

    // Reminders
    fun isReminderEnabled(): Boolean = settingsRepository.isReminderEnabled()
    fun setReminderEnabled(enabled: Boolean) {
        settingsRepository.setReminderEnabled(enabled)
    }
    fun getReminderHour(): Int = settingsRepository.getReminderHour()
    fun getReminderMinute(): Int = settingsRepository.getReminderMinute()
    fun setReminderTime(hour: Int, minute: Int) {
        settingsRepository.setReminderTime(hour, minute)
    }

    // Focus Mode
    private val _isFocusModeEnabled = MutableStateFlow(settingsRepository.isFocusModeEnabled())
    val isFocusModeEnabled: StateFlow<Boolean> = _isFocusModeEnabled.asStateFlow()

    fun setFocusModeEnabled(enabled: Boolean) {
        settingsRepository.setFocusModeEnabled(enabled)
        _isFocusModeEnabled.value = enabled
    }

    // --- Challenge Modes ---
    private val _challengeCards = MutableStateFlow<List<FlashCard>>(emptyList())
    val challengeCards: StateFlow<List<FlashCard>> = _challengeCards.asStateFlow()

    private val _challengeTimeLeft = MutableStateFlow(0)
    val challengeTimeLeft: StateFlow<Int> = _challengeTimeLeft.asStateFlow()

    fun startQuizMode(categoryId: Int?, onReady: (Boolean) -> Unit) {
        viewModelScope.launch {
            val allCards = flashCardDao.getAllCardsSync()
            val filteredCards = if (categoryId != null && categoryId != -1) {
                allCards.filter { it.categoryId == categoryId }
            } else {
                allCards
            }
            
            if (filteredCards.size < 5) {
                onReady(false)
                return@launch
            }
            _challengeCards.value = filteredCards.shuffled().take(20)
            onReady(true)
        }
    }

    fun startTimeAttack(categoryId: Int?, timeLimit: Int, onReady: (Boolean) -> Unit) {
        viewModelScope.launch {
            val allCards = flashCardDao.getAllCardsSync()
            val filteredCards = if (categoryId != null && categoryId != -1) {
                allCards.filter { it.categoryId == categoryId }
            } else {
                allCards
            }

            if (filteredCards.size < 5) {
                onReady(false)
                return@launch
            }
            _challengeCards.value = filteredCards.shuffled()
            _challengeTimeLeft.value = timeLimit
            onReady(true)
            
            // Start timer
            launch {
                while (_challengeTimeLeft.value > 0) {
                    delay(1000)
                    _challengeTimeLeft.value -= 1
                }
            }
        }
    }

    fun startDailyChallenge(categoryId: Int?, onReady: (Boolean) -> Unit) {
        viewModelScope.launch {
            val allCards = flashCardDao.getAllCardsSync()
            val filteredCards = if (categoryId != null && categoryId != -1) {
                allCards.filter { it.categoryId == categoryId }
            } else {
                allCards
            }

            if (filteredCards.size < 5) {
                onReady(false)
                return@launch
            }
            // Use today's epoch day as a seed so it's the same random set for the whole day
            val seed = LocalDate.now().toEpochDay()
            val random = Random(seed)
            _challengeCards.value = filteredCards.shuffled(random).take(10)
            onReady(true)
        }
    }

    /**
     * Send text to Gemini to generate flashcards
     */
    fun generateFlashcardsWithAi(text: String, categoryId: Int, onResult: (Boolean, String) -> Unit) {
        val apiKey = getGeminiApiKey()
        if (apiKey.isBlank()) {
            onResult(false, "API Key is missing. Please set it in Settings.")
            return
        }

        viewModelScope.launch {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.0-flash",
                    apiKey = apiKey
                )

                val prompt = """
                    Analyze the following text and automatically summarize it into Q&A style flashcards. 
                    Extract the key concepts and form them into clear questions (frontText) and concise answers (backText). 
                    Reply ONLY with a JSON array, no markdown formatting, and no explanation. 
                    Format: [{"frontText":"question","backText":"answer"}]. 
                    Text: $text
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val jsonText = response.text ?: ""

                android.util.Log.d("FlashcardAI", "Raw AI response: $jsonText")

                // Extract the JSON array from the response
                val startIndex = jsonText.indexOf('[')
                val endIndex = jsonText.lastIndexOf(']')

                if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
                    onResult(false, "Unexpected response from AI:\n\n${jsonText.take(300)}")
                    return@launch
                }

                val cleanedJson = jsonText.substring(startIndex, endIndex + 1)
                val format = Json { ignoreUnknownKeys = true }
                val aiCards = format.decodeFromString<List<AiFlashcardResponse>>(cleanedJson)

                if (aiCards.isEmpty()) {
                    onResult(false, "AI generated no cards. Please try with more detailed text.")
                } else {
                    aiCards.forEach {
                        flashCardDao.insertCard(
                            FlashCard(categoryId = categoryId, frontText = it.frontText, backText = it.backText)
                        )
                    }
                    onResult(true, "Successfully generated ${aiCards.size} cards!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Error: ${e.localizedMessage}")
            }
        }
    }
}
