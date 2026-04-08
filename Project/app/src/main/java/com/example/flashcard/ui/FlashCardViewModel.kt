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
                    modelName = "gemini-1.5-flash",
                    apiKey = apiKey
                )

                val prompt = """
                    You are a helpful study assistant. Extract key concepts from the following text and turn them into flashcards.
                    Return ONLY a raw JSON array of objects. Do not use Markdown blocks like ```json.
                    Each object must have exactly two string fields: "frontText" and "backText".
                    
                    Text:
                    $text
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val jsonText = response.text?.trim() ?: ""
                
                // Clean markdown code blocks if the model accidentally outputs them
                val cleanedJson = jsonText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()

                val format = Json { ignoreUnknownKeys = true }
                val aiCards = format.decodeFromString<List<AiFlashcardResponse>>(cleanedJson)

                if (aiCards.isEmpty()) {
                    onResult(false, "AI generated no cards.")
                } else {
                    aiCards.forEach {
                        flashCardDao.insertCard(FlashCard(categoryId = categoryId, frontText = it.frontText, backText = it.backText))
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
