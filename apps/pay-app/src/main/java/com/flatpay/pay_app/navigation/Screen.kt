package com.flatpay.pay_app.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Sale : Screen("sale")
    object PreAuth : Screen("preauth")
    object Refund : Screen("refund")
    object Settings : Screen("settings")
    object Processing : Screen("processing")

    companion object {
        fun getStartDestination() = Main.route
    }
}


