package com.flatpay.pay_app.ui.main

import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flatpay.common.workflows.Dependencies
import com.flatpay.pay_app.viewmodels.AppViewModelFactory
import com.flatpay.pay_app.data.datastore.DataStore
import com.flatpay.pay_app.viewmodels.AppViewModel
import com.flatpay.log.AppLog
import com.flatpay.pay_app.data.models.DependencyModel
import com.flatpay.pay_app.ui.navigation.Navigation
import com.flatpay.pay_app.ui.theme.PaymentcoreappTheme


class MainActivity : ComponentActivity() {

    private lateinit var appViewModel: AppViewModel
    private lateinit var dataStore: DataStore
    private lateinit var dependencies: Dependencies
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        AppLog.LOGI("START MainActivity!")
        super.onCreate(savedInstanceState)

        dataStore = DataStore()
        appViewModel = ViewModelProvider(this, AppViewModelFactory(dataStore)).get(AppViewModel::class.java)
        AppLog.LOGI("MainActivity will create depenencies!")

        dependencies = Dependencies()
        dependencies.currentViewModel = appViewModel
        val depenciesData = DependencyModel(info =dependencies )
        appViewModel.setDependencies(depenciesData)


        enableEdgeToEdge()
        setContent {
            PaymentcoreappTheme {
                navController = rememberNavController()
                Navigation(navController = navController, viewModel = appViewModel)
            }
        }
        // Observe navigation commands
        appViewModel.navigationEvent.observe(this, Observer { command ->
            AppLog.LOGI("OBSERVERD EVENT!")
            when (command) {
                AppViewModel.NavigationCommand.NextScreen -> {
                    navController.navigate(command) // Navigate to the next screen
                    appViewModel.onNavigationHandled() // Reset navigation command
                }
                null -> {
                    // No action needed as event is handled
                }
            }

        })
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}



