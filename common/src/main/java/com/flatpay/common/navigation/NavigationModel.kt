package com.flatpay.common.navigation

import androidx.lifecycle.ViewModel
import com.flatpay.common.core.event.AppEvent
import com.flatpay.common.core.event.EventBus
import com.flatpay.common.screens.Screen
import com.flatpay.log.AppLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NavigationModel() : ViewModel() {

    private val navigationState = NavigationState.instance
    val currentScreen = navigationState.currentScreen
    private val eventBus = EventBus.instance
    val navigationModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)  // Removed private

    init {
        AppLog.LOGI("NavigationModel: init Event observator")
        // Start handling navigation events
        AppLog.LOGI("NavigationModel: Starting event handling")
        EventBus.eventScope.launch(Dispatchers.Main.immediate) {
            eventBus.collectEvents(
                setOf(
                    AppEvent.Navigation.NavigateNext::class,
                    AppEvent.Navigation.NavigateTo::class
                )
            ) { event ->
                when (event) {
                    is AppEvent.Navigation.NavigateNext -> navigateToNext()
                    is AppEvent.Navigation.NavigateTo -> {
                        AppLog.LOGI("NavigationModel: Navigating to: ${event.screen}")
                        navigateTo(event.screen)
                    }

                    else -> {}
                }
            }
        }
    }

    fun observeNavigation() {
        navigationModelScope.launch {
            navigationState.currentScreen.collect { screen ->
                // Update UI or perform navigation based on the current screen
                navigateTo(screen)
            }
        }
    }

    fun changeScreen(screen: Screen) {
        // This can be called from any thread
        navigationState.navigateTo(screen)
    }

    fun navigateTo(screen: Screen) {
        AppLog.LOGI("NavigationModel: NavigateTo Screen")
        navigationState.navigateTo(screen)
    }

    private fun navigateToNext() {
        AppLog.LOGI("NavigationModel: NavigateToNext")
        navigationState.navigateToNext()
    }

}