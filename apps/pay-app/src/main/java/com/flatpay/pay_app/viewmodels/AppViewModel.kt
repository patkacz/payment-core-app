package com.flatpay.pay_app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.flatpay.common.view.models.BaseViewModel
import com.flatpay.pay_app.data.datastore.DataStore
import com.flatpay.pay_app.state.AppState
import com.flatpay.log.AppLog
import com.flatpay.pay_app.data.models.DependencyModel
import com.flatpay.pay_app.repositories.MyWorkflowRepository
import java.beans.PropertyChangeEvent

class AppViewModel(private val dataStore: DataStore) : BaseViewModel() {
    private val _appState = MutableLiveData<AppState>()
    private val _dependencies = MutableLiveData<DependencyModel>()
    private val _repository = MyWorkflowRepository()
    private val _navigationEvent = MutableLiveData<NavigationCommand?>()
    val navigationEvent: LiveData<NavigationCommand?> = _navigationEvent
    //lateinit var dependencies: Dependencies // move that to Application Class, where main activity will be started

    val appState: LiveData<AppState> get() = _appState
    val dependencies: LiveData<DependencyModel> get() = _dependencies

    init {
        AppLog.LOGI("START VIEW MODEL!")
        //Live data binding
        _appState.value = AppState.Loading
        //repository.dependencies = dependencies
        fetchData()
    }

    fun setDependencies(dependencies: DependencyModel) {
        _dependencies.value = dependencies
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

    override fun navigateToNextScreen() {
        _navigationEvent.postValue(NavigationCommand.NextScreen)
    }

    override fun hideProcessingScreen() {
        TODO("Not yet implemented")
    }

    fun onNavigationHandled() {
        _navigationEvent.postValue(null)
    }

    fun onMainButtonClicked() {
        AppLog.LOGI("button clicked")
        dependencies.value?.let { _repository.runWorkflow(it.info) }
    }

    fun onSettingsButtonClicked() {
        AppLog.LOGI("enter settings")
        _repository.runWorkflowSettings()
    }

    // Define an sealed class or simple enum for navigation commands
    sealed class NavigationCommand {
        object NextScreen : NavigationCommand()
    }

    override fun propertyChange(p0: PropertyChangeEvent?) {
        TODO("Not yet implemented")
    }
}

