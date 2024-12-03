package com.flatpay.payment_core_app.data.models

data class AppModel (
       val isLoading: Boolean = false,
       val errorMessage: String? = null,
       val successData: Any? = null
)
