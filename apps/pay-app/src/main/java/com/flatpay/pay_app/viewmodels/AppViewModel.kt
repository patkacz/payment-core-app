package com.flatpay.pay_app.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flatpay.pay_app.data.datastore.DataStore
import com.flatpay.pay_app.data.models.AppModel
import com.flatpay.pay_app.state.AppState
import com.flatpay.log.AppLog


class AppViewModel(private val dataStore: DataStore) : ViewModel() {
    private val _appState = MutableLiveData<AppState>()
    val appState: LiveData<AppState> get() = _appState

    init {
        AppLog.LOGI("START VIEW MODEL!")
        //Live data binding
        _appState.value = AppState.Loading
        fetchData()

    }

    // Method to fetch data and update appState accordingly
    private fun fetchData() {
        // Simulating data fetching
        try {
            val data = dataStore.getData() // Assume this fetches data
            // On success
            _appState.value = AppState.Success(data)
            // Update AppModel if needed
            // For example:
            val appModel = AppModel(isLoading = false, successData = data)
        } catch (e: Exception) {
            _appState.value = AppState.Error(e.message ?: "An error occurred")
        }
    }

    fun onMainButtonClicked() {
        AppLog.LOGI("button clicked")
    }
}

