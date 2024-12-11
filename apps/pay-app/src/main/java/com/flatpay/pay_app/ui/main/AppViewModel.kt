package com.flatpay.pay_app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flatpay.common.core.base.BaseViewModel
import com.flatpay.common.database.WorkflowContext

import com.flatpay.common.core.model.Dependencies
import com.flatpay.pay_app.data.datastore.DataStore
import com.flatpay.pay_app.state.AppState
import com.flatpay.log.AppLog
import com.flatpay.pay_app.PaymentApplication
import com.flatpay.pay_app.repositories.WorkflowRepository
import java.beans.PropertyChangeEvent

class AppViewModel(
    private val dependencies: Dependencies, override val context: WorkflowContext
) : BaseViewModel() {
    private val _appState = MutableLiveData<AppState>()
    private val _workflowRepository = WorkflowRepository()
    private val dataStore =  DataStore()
    private val navigationModel = PaymentApplication.getInstance().navigationModel
    val appState: LiveData<AppState> get() = _appState

    init {
        AppLog.LOGI("START VIEW MODEL!")
        //Live data binding
        _appState.value = AppState.Loading
        //repository.dependencies = dependencies
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
            //val appModel = AppModel(isLoading = false, successData = data)
        } catch (e: Exception) {
            _appState.value = AppState.Error(e.message ?: "An error occurred")
        }
    }



    fun onMainButtonClicked(context: WorkflowContext) {
        AppLog.LOGI("button clicked")
        _workflowRepository.runWorkflow(context, dependencies)
    }


    // Define an sealed class or simple enum for navigation commands
    sealed class NavigationCommand {
        object NextScreen : NavigationCommand()
    }

    override fun propertyChange(p0: PropertyChangeEvent?) {
        TODO("Not yet implemented")
    }
}

