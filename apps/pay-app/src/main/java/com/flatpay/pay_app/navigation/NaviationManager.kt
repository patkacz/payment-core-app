package com.flatpay.pay_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flatpay.common.navigation.NavigationModel
import com.flatpay.common.screens.Screen
import com.flatpay.pay_app.ui.main.MainScreen
import com.flatpay.pay_app.ui.screens.ProcessingScreen
import com.flatpay.pay_app.ui.settings.viewmodels.SettingsScreen
import com.flatpay.pay_app.viewmodels.AppViewModel

@Composable
fun NaviationManager(
    navController: NavHostController,
    viewModel: AppViewModel,
    navigationViewModel: NavigationModel
) {
    // Observe navigation state
    LaunchedEffect(true) {
        navigationViewModel.currentScreen.collect { screen ->
            navController.navigate(screen.route) {
                // Optional: Configure navigation options
                popUpTo(Screen.Main.route) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.getStartDestination()
    ) {
        composable(Screen.Main.route) {
            MainScreen(viewModel, navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel, navController)
        }
        composable(Screen.Processing.route) {
             ProcessingScreen(viewModel, navController)
        }
        // Add other screens...
    }
}