package com.flatpay.pay_app.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.flatpay.pay_app.data.datastore.DataStore
import com.flatpay.pay_app.ui.sale.SaleViewModel
import com.flatpay.pay_app.navigation.NaviationManager
import com.flatpay.pay_app.navigation.NavigationViewModel
import com.flatpay.pay_app.viewmodels.AppViewModel
import com.flatpay.log.AppLog
import com.flatpay.pay_app.PaymentApplication



class MainActivity : ComponentActivity() {

    private lateinit var appViewModel: AppViewModel
    private lateinit var saleViewModel: SaleViewModel
    private lateinit var dataStore: DataStore


    override fun onCreate(savedInstanceState: Bundle?) {
        AppLog.LOGI("START MainActivity!")
        super.onCreate(savedInstanceState)

        val dependencies = (application as PaymentApplication).dependencies
        val navigationViewModel = NavigationViewModel((application as PaymentApplication))
        val factory = AppViewModelFactory(dependencies)
        appViewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        //setupNavigation()
  //      observeViewModel()
    //    registerEventHandlers()

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NaviationManager(
                navController = navController,
                viewModel = appViewModel,
                navigationViewModel = navigationViewModel
            )
        }
/*
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
*/

    }

    private fun registerEventHandlers() {
        TODO("Not yet implemented")
    }

    private fun observeViewModel() {
        TODO("Not yet implemented")
    }

    private fun setupNavigation() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}



