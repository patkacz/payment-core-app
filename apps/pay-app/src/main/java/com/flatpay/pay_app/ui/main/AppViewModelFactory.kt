package com.flatpay.pay_app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flatpay.common.core.model.Dependencies
import com.flatpay.pay_app.viewmodels.AppViewModel

class AppViewModelFactory(
    private val dependencies: Dependencies,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(dependencies) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
