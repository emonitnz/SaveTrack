package com.emon.savetrack.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emon.savetrack.data.local.TransactionType
import com.emon.savetrack.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for Home screen, calculating total savings dynamically from Room database transactions.
 */
class HomeViewModel(repository: TransactionRepository) : ViewModel() {

    // Total savings state derived from all database transactions (Income - Expenses)
    val uiState: StateFlow<HomeUiState> = repository.allTransactions
        .map { transactions ->
            val totalIncome = transactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
            val totalExpenses = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
            HomeUiState(totalSavings = totalIncome - totalExpenses)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(totalSavings = 0.0)
        )
}