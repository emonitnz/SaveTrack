package com.emon.savetrack.ui.home

import java.text.NumberFormat
import java.util.Locale

/**
 * Holds the state for the Home screen.
 * Stores the total savings amount formatted in NZD.
 */
data class HomeUiState(
    val totalSavings: Double = 1250.00
) {
    /**
     * Formats the total savings as NZD currency (e.g., "$1,250.00").
     */
    fun formattedTotalSavings(): String {
        val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-NZ"))
        return format.format(totalSavings)
    }
}