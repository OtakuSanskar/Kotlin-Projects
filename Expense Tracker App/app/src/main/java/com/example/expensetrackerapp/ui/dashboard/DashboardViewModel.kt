package com.example.expensetrackerapp.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerapp.data.database.AppDatabase
import com.example.expensetrackerapp.data.model.CategoryTotal
import com.example.expensetrackerapp.data.model.DashboardState
import com.example.expensetrackerapp.data.model.ExpenseCategoryData
import com.example.expensetrackerapp.data.model.TransactionType
import com.example.expensetrackerapp.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository = FinanceRepository(
        AppDatabase.getDatabase(application).transactionDao(),
        AppDatabase.getDatabase(application).categoryDao()
    )

    // Create reactive flows for transaction totals
    private val incomeTotalFLow = repository.allTransactions
        .map { transactions ->
            transactions.filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
        }

    private val expenseTotalFlow = repository.allTransactions
        .map { transactions ->
            transactions.filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
        }

    val dashboardState: StateFlow<DashboardState> = combine(
        incomeTotalFLow,
        expenseTotalFlow,
        repository.getCategoryTotals(TransactionType.EXPENSE)
    ) { income:Double, expense:Double, categoryTotals: List<CategoryTotal> ->
        DashboardState(
            totalIncome = income,
            totalExpense = expense,
            balance = income - expense,
            expenseCategories = categoryTotals.map { categoryTotal ->
                ExpenseCategoryData(
                    category = categoryTotal.name,
                    amount = categoryTotal.total
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )
}