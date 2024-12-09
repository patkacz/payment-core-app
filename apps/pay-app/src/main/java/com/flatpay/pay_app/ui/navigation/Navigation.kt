package com.flatpay.pay_app.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flatpay.pay_app.ui.screens.MainScreen
import com.flatpay.pay_app.ui.screens.ProcessingScreen
import com.flatpay.pay_app.ui.screens.SettingsScreen
import com.flatpay.pay_app.viewmodels.AppViewModel

@Composable
fun Navigation(navController: NavHostController, viewModel: AppViewModel) {
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
        composable(Screen.Previous.route) {
            MainScreen(viewModel, navController)
        }
        composable(Screen.Processing.route) {
            ProcessingScreen(viewModel, navController)
        }
    }
}