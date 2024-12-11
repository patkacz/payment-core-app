package com.flatpay.common.navigation

import com.flatpay.common.screens.Screen
import com.flatpay.log.AppLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationState {
    companion object {
        val instance: NavigationState by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            NavigationState()
        }
    }

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Main)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        AppLog.LOGI("NavigationState: NavigateTo $screen")
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