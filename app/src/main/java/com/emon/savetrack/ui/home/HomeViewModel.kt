package com.emon.savetrack.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the Home screen following MVVM architecture.
 * Manages HomeUiState using StateFlow.
 */
class HomeViewModel : ViewModel() {

    // Internal state flow holding total savings state
    private val _uiState = MutableStateFlow(HomeUiState(totalSavings = 1250.00))

    // Public read-only state flow exposed to the UI
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}