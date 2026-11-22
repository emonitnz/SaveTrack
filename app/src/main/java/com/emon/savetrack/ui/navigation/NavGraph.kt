package com.emon.savetrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emon.savetrack.SaveTrackApplication
import com.emon.savetrack.ui.SaveTrackViewModelFactory
import com.emon.savetrack.ui.addweek.AddWeekScreen
import com.emon.savetrack.ui.addweek.AddWeekViewModel
import com.emon.savetrack.ui.history.HistoryScreen
import com.emon.savetrack.ui.history.HistoryViewModel
import com.emon.savetrack.ui.home.HomeScreen
import com.emon.savetrack.ui.home.HomeViewModel

/**
 * Defines navigation routes for the SaveTrack app.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddWeek : Screen("add_week")
    object History : Screen("history")
}

/**
 * AppNavHost manages screen navigation using Jetpack Navigation Compose and manual DI.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    val context = LocalContext.current
    val app = context.applicationContext as SaveTrackApplication
    val factory = remember { SaveTrackViewModelFactory(app.repository) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Route 1: Home screen
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(
                onNavigateToAddWeek = { navController.navigate(Screen.AddWeek.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                viewModel = homeViewModel
            )
        }

        // Route 2: Add Week screen
        composable(Screen.AddWeek.route) {
            val addWeekViewModel: AddWeekViewModel = viewModel(factory = factory)
            AddWeekScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = addWeekViewModel
            )
        }

        // Route 3: History screen
        composable(Screen.History.route) {
            val historyViewModel: HistoryViewModel = viewModel(factory = factory)
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = historyViewModel
            )
        }
    }
}