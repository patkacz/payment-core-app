package com.flatpay.pay_app.data.models

data class AppModel (
       val isLoading: Boolean = false,
       val errorMessage: String? = null,
       val successData: Any? = null
)
