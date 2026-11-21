package com.emon.savetrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emon.savetrack.ui.addweek.AddWeekScreen
import com.emon.savetrack.ui.history.HistoryScreen
import com.emon.savetrack.ui.home.HomeScreen

/**
 * Defines navigation routes for the SaveTrack app.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddWeek : Screen("add_week")
    object History : Screen("history")
}

/**
 * AppNavHost manages screen navigation using Jetpack Navigation Compose.
 * Contains routes for Home ("home"), Add Week ("add_week"), and History ("history").
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Route 1: Home screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddWeek = { navController.navigate(Screen.AddWeek.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) }
            )
        }

        // Route 2: Add Week screen
        composable(Screen.AddWeek.route) {
            AddWeekScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Route 3: History screen
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}