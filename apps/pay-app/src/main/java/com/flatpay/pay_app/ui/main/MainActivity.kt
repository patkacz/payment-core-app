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
import com.flatpay.common.navigation.NavigationModel
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

        val app = application as PaymentApplication

        val dependencies = app.dependencies
        val navigationModel = NavigationModel()
        val workflowContext = app.workflowContext
        val factory = AppViewModelFactory(dependencies, workflowContext)
        appViewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        workflowContext.registerObject(navigationModel)
        //navigationModel.observeNavigation()
        //workflowContext.registerObject()
        //workflowContext.registerController(navigationModel)

        //setupNavigation()
        //observeViewModel()
        //registerEventHandlers()

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NaviationManager(
                navController = navController,
                viewModel = appViewModel,
                navigationViewModel = navigationModel
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}



