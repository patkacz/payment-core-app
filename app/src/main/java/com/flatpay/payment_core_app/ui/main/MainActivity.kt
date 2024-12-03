package com.flatpay.payment_core_app.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.flatpay.payment_core_app.viewmodels.AppViewModelFactory
import com.flatpay.payment_core_app.data.datastore.DataStore
import com.flatpay.payment_core_app.ui.theme.PaymentcoreappTheme
import com.flatpay.payment_core_app.viewmodels.AppViewModel

class MainActivity : ComponentActivity() {
    private lateinit var appViewModel: AppViewModel
    private lateinit var dataStore: DataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        println("CREATE!!!")
        super.onCreate(savedInstanceState)

        dataStore = DataStore()
        // Use ViewModelFactory to instantiate AppViewModel
        appViewModel = ViewModelProvider(this, AppViewModelFactory(dataStore)).get(AppViewModel::class.java)


        enableEdgeToEdge()
        setContent {
            PaymentcoreappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PaymentcoreappTheme {
        Greeting("Android")
    }
}

