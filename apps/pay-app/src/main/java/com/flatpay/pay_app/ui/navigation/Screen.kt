package com.flatpay.pay_app.ui.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Settings : Screen("settings")
    object Processing : Screen("processing")
    object Previous : Screen("previousScreen")

    companion object {
        fun getStartDestination() = Main.route
    }
}


