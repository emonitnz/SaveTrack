package com.emon.savetrack.ui.addweek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emon.savetrack.data.local.Transaction
import com.emon.savetrack.data.local.TransactionType
import com.emon.savetrack.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * ViewModel for AddWeekScreen managing earnings, expenses, validation, and weekly summary calculations.
 */
class AddWeekViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddWeekUiState())
    val uiState: StateFlow<AddWeekUiState> = _uiState.asStateFlow()

    init {
        val (startMillis, endMillis) = getWeekBounds()
        val weekDateRange = formatWeekRange()

        _uiState.update { it.copy(weekDateRangeText = weekDateRange) }

        // Observe all transactions to update total savings and weekly totals dynamically
        viewModelScope.launch {
            repository.allTransactions.collect { allTxs ->
                val totalIncome = allTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val totalExpenses = allTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                val totalSavings = totalIncome - totalExpenses

                val weekTxs = allTxs.filter { it.date in startMillis..endMillis }
                val weekEarned = weekTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val weekSpent = weekTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                val weekSaved = weekEarned - weekSpent
                val savingPercent = if (weekEarned > 0) {
                    ((weekSaved / weekEarned) * 100).coerceIn(0.0, 100.0)
                } else 0.0

                val weekExpensesList = weekTxs.filter { it.type == TransactionType.EXPENSE }

                _uiState.update { currentState ->
                    currentState.copy(
                        totalSavings = totalSavings,
                        thisWeekEarned = weekEarned,
                        thisWeekSpent = weekSpent,
                        thisWeekSaved = weekSaved,
                        savingPercent = savingPercent,
                        thisWeekExpenses = weekExpensesList
                    )
                }
            }
        }
    }

    fun onEarningAmountChange(input: String) {
        _uiState.update { it.copy(earningAmountInput = input, earningAmountError = null) }
    }

    fun onExpenseAmountChange(input: String) {
        _uiState.update { it.copy(expenseAmountInput = input, expenseAmountError = null) }
    }

    fun onExpenseItemNameChange(input: String) {
        _uiState.update { it.copy(expenseItemNameInput = input, expenseItemNameError = null) }
    }

    fun addEarning(onSuccess: (String) -> Unit) {
        val amount = _uiState.value.earningAmountInput.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(earningAmountError = "Enter a valid amount greater than $0") }
            return
        }

        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    type = TransactionType.INCOME,
                    amount = amount,
                    itemName = "Earning"
                )
            )
            _uiState.update { it.copy(earningAmountInput = "", earningAmountError = null) }
            onSuccess("Earning added successfully!")
        }
    }

    fun addExpense(onSuccess: (String) -> Unit) {
        val amount = _uiState.value.expenseAmountInput.toDoubleOrNull()
        val name = _uiState.value.expenseItemNameInput.trim()

        val isAmountValid = amount != null && amount > 0
        val isNameValid = name.isNotEmpty()

        if (!isAmountValid || !isNameValid) {
            _uiState.update {
                it.copy(
                    expenseAmountError = if (!isAmountValid) "Enter a valid amount greater than $0" else null,
                    expenseItemNameError = if (!isNameValid) "Please enter what you spent on" else null
                )
            }
            return
        }

        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    type = TransactionType.EXPENSE,
                    amount = amount,
                    itemName = name
                )
            )
            _uiState.update {
                it.copy(
                    expenseAmountInput = "",
                    expenseItemNameInput = "",
                    expenseAmountError = null,
                    expenseItemNameError = null
                )
            }
            onSuccess("Expense added successfully!")
        }
    }

    fun deleteTransaction(transaction: Transaction, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            onSuccess("Transaction deleted")
        }
    }

    private fun getWeekBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        // Set to Monday 00:00:00.000
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startMillis = calendar.timeInMillis

        // Set to Sunday 23:59:59.999
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endMillis = calendar.timeInMillis

        return Pair(startMillis, endMillis)
    }

    private fun formatWeekRange(): String {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startDate = calendar.time

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endDate = calendar.time

        val sdf = SimpleDateFormat("EEE d MMM", Locale.ENGLISH)
        return "${sdf.format(startDate)} – ${sdf.format(endDate)}"
    }
}