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
import kotlinx.coroutines.launch

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

                val prompt = "Create flashcards from this text. Reply ONLY with a JSON array, no markdown, no explanation. Format: [{\"frontText\":\"question\",\"backText\":\"answer\"}]. Text: $text"

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
