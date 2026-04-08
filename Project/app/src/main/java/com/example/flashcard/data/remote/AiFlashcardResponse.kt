package com.example.flashcard.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class AiFlashcardResponse(
    val frontText: String,
    val backText: String
)
