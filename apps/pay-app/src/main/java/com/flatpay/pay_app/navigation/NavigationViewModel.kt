package com.flatpay.pay_app.navigation

import android.app.Application
import androidx.lifecycle.ViewModel
import com.flatpay.pay_app.PaymentApplication


class NavigationViewModel (
    application: Application
) : ViewModel() {
    private val navigationState = (application as PaymentApplication).navigationState
    val currentScreen = navigationState.currentScreen

    fun navigateTo(screen: Screen) {
        navigationState.navigateTo(screen)
    }

    fun navigateToNext() {
        navigationState.navigateToNext()
    }
}