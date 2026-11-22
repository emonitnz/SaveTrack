package com.emon.savetrack.ui.addweek

import com.emon.savetrack.data.local.Transaction
import java.text.NumberFormat
import java.util.Locale

/**
 * UI State for the AddWeek screen containing inputs, errors, and calculated weekly stats.
 */
data class AddWeekUiState(
    val totalSavings: Double = 0.0,
    val earningAmountInput: String = "",
    val earningAmountError: String? = null,
    val expenseAmountInput: String = "",
    val expenseItemNameInput: String = "",
    val expenseAmountError: String? = null,
    val expenseItemNameError: String? = null,
    val weekDateRangeText: String = "",
    val thisWeekEarned: Double = 0.0,
    val thisWeekSpent: Double = 0.0,
    val thisWeekSaved: Double = 0.0,
    val savingPercent: Double = 0.0,
    val thisWeekExpenses: List<Transaction> = emptyList()
) {
    fun formattedTotalSavings(): String = formatCurrency(totalSavings)
    fun formattedThisWeekEarned(): String = formatCurrency(thisWeekEarned)
    fun formattedThisWeekSpent(): String = formatCurrency(thisWeekSpent)
    fun formattedThisWeekSaved(): String = formatCurrency(thisWeekSaved)

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-NZ"))
        return format.format(amount)
    }
}