package com.emon.savetrack.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emon.savetrack.data.local.Transaction
import com.emon.savetrack.data.local.TransactionType
import com.emon.savetrack.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Summary of one week (Monday to Sunday) shown as a card on the History screen.
 */
data class WeekSummary(
    val weekStart: Long,          // Monday 00:00 in millis, used as a unique key
    val dateRangeText: String,    // e.g. "Mon 16 Nov – Sun 22 Nov"
    val earned: Double,
    val spent: Double,
    val saved: Double,
    val spentPercent: Double,     // spent / earned × 100 (0 if no earnings)
    val savedPercent: Double,     // saved / earned × 100 (0 if no earnings)
    val expenses: List<Transaction>
)

/**
 * ViewModel for HistoryScreen: groups all transactions into weeks, newest first.
 */
class HistoryViewModel(
    repository: TransactionRepository
) : ViewModel() {

    // Every time the database changes, the weeks are recalculated automatically.
    val weeks: StateFlow<List<WeekSummary>> = repository.allTransactions
        .map { transactions -> groupByWeek(transactions) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private fun groupByWeek(transactions: List<Transaction>): List<WeekSummary> {
        return transactions
            .groupBy { weekStartOf(it.date) }          // key = Monday of that week
            .map { (weekStart, weekTxs) -> buildSummary(weekStart, weekTxs) }
            .sortedByDescending { it.weekStart }        // newest week first
    }

    private fun buildSummary(weekStart: Long, weekTxs: List<Transaction>): WeekSummary {
        val earned = weekTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = weekTxs.filter { it.type == TransactionType.EXPENSE }
            .sortedByDescending { it.date }
        val spent = expenses.sumOf { it.amount }
        val saved = earned - spent

        val spentPercent = if (earned > 0) spent / earned * 100 else 0.0
        val savedPercent = if (earned > 0) saved / earned * 100 else 0.0

        return WeekSummary(
            weekStart = weekStart,
            dateRangeText = formatRange(weekStart),
            earned = earned,
            spent = spent,
            saved = saved,
            spentPercent = spentPercent,
            savedPercent = savedPercent,
            expenses = expenses
        )
    }

    /** Returns Monday 00:00 of the week that [millis] falls in. */
    private fun weekStartOf(millis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // DAY_OF_WEEK: Sunday = 1, Monday = 2 ... Saturday = 7
        // daysSinceMonday: Monday = 0 ... Sunday = 6
        val daysSinceMonday = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
        cal.add(Calendar.DAY_OF_YEAR, -daysSinceMonday)
        return cal.timeInMillis
    }

    private fun formatRange(weekStart: Long): String {
        val sdf = SimpleDateFormat("EEE d MMM", Locale.ENGLISH)
        val cal = Calendar.getInstance().apply { timeInMillis = weekStart }
        val start = sdf.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, 6)
        val end = sdf.format(cal.time)
        return "$start – $end"
    }
}
