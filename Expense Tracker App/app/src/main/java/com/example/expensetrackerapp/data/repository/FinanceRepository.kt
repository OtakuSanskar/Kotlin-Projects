package com.example.expensetrackerapp.data.repository

import com.example.expensetrackerapp.data.dao.CategoryDao
import com.example.expensetrackerapp.data.dao.TransactionDao
import com.example.expensetrackerapp.data.model.Category
import com.example.expensetrackerapp.data.model.Transaction
import com.example.expensetrackerapp.data.model.TransactionType

class FinanceRepository (
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
){
    val allTransactions = transactionDao.getAllTransactions()
    val allCategories = categoryDao.getAllCategories()

    fun getTransactionsByType(type: TransactionType) =
        transactionDao.getTransactionsByType(type)

    fun getCategoriesByType(type: TransactionType) =
        categoryDao.getCategoriesByType(type)

    fun getCategoryTotals(type: TransactionType, startDate: Long=0) =
        transactionDao.getCategoryTotals(type, startDate)

    suspend fun getTotalByType(type: TransactionType) =
        transactionDao.getTotalByType(type) ?: 0.0

    suspend fun addTransaction(transaction: Transaction) =
        transactionDao.insert(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.delete(transaction)

    suspend fun addCategory(category: Category) =
        categoryDao.insert(category)
}