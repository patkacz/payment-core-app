package com.flatpay.pay_app.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationState {
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Main)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun navigateToNext() {
        val nextScreen = when (_currentScreen.value) {
            is Screen.Main -> Screen.Sale
            is Screen.Sale -> Screen.Processing
            is Screen.PreAuth -> Screen.Processing
            is Screen.Refund -> Screen.Processing
            is Screen.Processing -> Screen.Main
            Screen.Settings -> Screen.Main
        }
        _currentScreen.value = nextScreen
    }
}