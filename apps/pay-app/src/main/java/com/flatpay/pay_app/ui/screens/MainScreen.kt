package com.flatpay.pay_app.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flatpay.pay_app.viewmodels.AppViewModel

@Composable
fun MainScreen(viewModel: AppViewModel, navController: NavController) {
    // Observe the button click message from the ViewModel

    Box(
        modifier = Modifier.fillMaxSize(), // Make Box take the full size of the screen
        contentAlignment = Alignment.Center // Center the content within the Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Button(
                onClick = { viewModel.onMainButtonClicked() },
                modifier = Modifier
                    .fillMaxWidth() // Make the button fill the width
                    .padding(horizontal = 16.dp) // Horizontal padding
                    .height(80.dp) // Set a fixed height for the button
            )
            {
                Text("Process", modifier = Modifier.padding(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("settings")},
                modifier = Modifier
                    .fillMaxWidth() // Make the button fill the width
                    .padding(horizontal = 16.dp) // Horizontal padding
                    .height(80.dp) // Set a fixed height for the button
            )
            {
                Text("Settings", modifier = Modifier.padding(16.dp))
            }
        }
    }
}