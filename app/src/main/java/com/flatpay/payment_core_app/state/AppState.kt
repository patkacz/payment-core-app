package com.flatpay.payment_core_app.state

/**
 * Represents different states of the application.
 */
sealed class AppState {
    object Loading : AppState()
    data class Main(val connect: Boolean) : AppState()
    data class Error(val message: String) : AppState()
    data class Success(val data: Any) : AppState()
}