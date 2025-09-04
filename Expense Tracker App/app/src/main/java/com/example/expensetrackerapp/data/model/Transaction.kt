package com.example.expensetrackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.expensetrackerapp.data.database.TransactionTypeConverter

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val category: String,
    @TypeConverters(TransactionTypeConverter::class)
    val date: Long = System.currentTimeMillis()
)
