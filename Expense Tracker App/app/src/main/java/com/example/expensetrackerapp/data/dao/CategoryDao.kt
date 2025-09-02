package com.example.expensetrackerapp.data.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expensetrackerapp.data.model.Category
import com.example.expensetrackerapp.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type")
    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryDao>>

    @Query("""
        SELECT c.name, COALESCE(SUM(t.amount), 0.0) AS total
        FROM categories c
        LEFT JOIN transactions t ON c.name = t.category
        WHERE c.type = :type
        AND (t.date >= :startDate OR t.date IS NULL)
        GROUP BY c.name
    """)
    fun getCategoryTotals(type: TransactionType, startDate: Long = 0): Flow<Map<String, Double>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)
}