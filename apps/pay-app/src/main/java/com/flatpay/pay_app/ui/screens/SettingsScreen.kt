package com.flatpay.pay_app.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
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
fun SettingsScreen(viewModel: AppViewModel, navController: NavController) {
    // Observe the button click message from the ViewModel
    Box (
        modifier = Modifier.fillMaxSize(), // Make Box take the full size of the screen
        contentAlignment = Alignment.Center // Center the content within the Box
    ) {
            Button(
                onClick = { navController.navigate("main") },
                modifier = Modifier
                    .fillMaxWidth() // Make the button fill the width
                    .padding(horizontal = 16.dp) // Horizontal padding
                    .height(80.dp) // Set a fixed height for the button
            ) {
            Text("Back!", modifier = Modifier.padding(16.dp))
        }
    }
}