package com.emon.savetrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emon.savetrack.data.repository.TransactionRepository
import com.emon.savetrack.ui.addweek.AddWeekViewModel
import com.emon.savetrack.ui.history.HistoryViewModel
import com.emon.savetrack.ui.home.HomeViewModel

/**
 * ViewModelProvider.Factory for instantiating ViewModels with TransactionRepository dependency.
 */
class SaveTrackViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddWeekViewModel::class.java) -> {
                AddWeekViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}