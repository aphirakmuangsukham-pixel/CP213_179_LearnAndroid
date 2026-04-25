package com.example.flashcard.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Int): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)
}

@Dao
interface FlashCardDao {
    @Query("SELECT * FROM flashcards WHERE categoryId = :categoryId")
    fun getCardsByCategory(categoryId: Int): Flow<List<FlashCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(flashCard: FlashCard)

    @Update
    suspend fun updateCard(flashCard: FlashCard)

    @Delete
    suspend fun deleteCard(flashCard: FlashCard)
}
