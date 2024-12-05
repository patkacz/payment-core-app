package com.flatpay.pay_app.ui.main

import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.flatpay.pay_app.viewmodels.AppViewModelFactory
import com.flatpay.pay_app.data.datastore.DataStore
import com.flatpay.pay_app.viewmodels.AppViewModel
import com.flatpay.log.AppLog

class MainActivity : ComponentActivity() {

    private lateinit var appViewModel: AppViewModel
    private lateinit var dataStore: DataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        AppLog.LOGI("START MainActivity!")
        super.onCreate(savedInstanceState)

        dataStore = DataStore()
        appViewModel = ViewModelProvider(this, AppViewModelFactory(dataStore)).get(AppViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            MainScreen(appViewModel)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}



